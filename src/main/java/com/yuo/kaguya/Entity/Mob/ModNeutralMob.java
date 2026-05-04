package com.yuo.kaguya.Entity.Mob;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yuo.kaguya.Entity.ModEntityTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 中立生物
 */
public class ModNeutralMob extends BaseMobEntity implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(ModNeutralMob.class, EntityDataSerializers.INT);

    private UUID persistentAngerTarget;

    public ModNeutralMob(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        // 攻击 AI
        this.goalSelector.addGoal(1, new RangeAttackGoal(this, 6, 1.0d));
        this.goalSelector.addGoal(2, new ModAttackGoal(this, 1.2D, false, 6));
        // 移动 AI
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new MoveTowardsTargetGoal(this, 1.0D, 32.0F));
        // 观察 AI
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, EntityMaid.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        // 目标选择 AI
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    }

    @Override
    public boolean hurt(DamageSource source, float value) {
        if (!this.level().isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity livingAttacker) {
                this.setPersistentAngerTarget(livingAttacker.getUUID());
                this.startPersistentAngerTimer();
                this.alertOthers(livingAttacker);
            }
        }
        if (this.getType() == ModEntityTypes.KOCHIYA_SANAE.get()) //早苗减免75%伤害
            value *= 0.75f;
        return super.hurt(source, value);
    }

    private void alertOthers(LivingEntity attacker) {
        double range = 16.0D;
        this.level().getEntitiesOfClass(this.getClass(),
                        this.getBoundingBox().inflate(range))
                .stream()
                .filter(mob -> mob != this && mob.getTarget() == null)
                .filter(mob -> !mob.isAlliedTo(attacker))
                .forEach(mob -> mob.setTarget(attacker));
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean b) {
        super.dropCustomDeathLoot(source, looting, b);
        this.spawnAtLocation(Items.EMERALD, Mth.randomBetweenInclusive(random, 2, 16));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
//            // 如果玩家关闭交易界面，清除 tradingPlayer
//            if (this.tradingPlayer != null && !this.tradingPlayer.containerMenu.equals(this.tradingPlayer.inventoryMenu)) {
//                this.setTradingPlayer(null);
//            }
        }
    }

    @Override
    public void updatePersistentAnger(ServerLevel level, boolean angerPersistent) {
        int angerTime = this.getRemainingPersistentAngerTime();
        LivingEntity target = this.getTarget();

        if (angerTime > 0) {
            this.setRemainingPersistentAngerTime(angerTime - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
                this.setPersistentAngerTarget(null);
            }
        }

        if (this.getPersistentAngerTarget() != null && this.getTarget() == null) {
            Player player = level.getPlayerByUUID(this.getPersistentAngerTarget());
            if (player != null && this.canAttack(player)) {
                this.setTarget(player);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(600);
    }
}