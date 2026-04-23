package com.yuo.kaguya.Entity.Mob;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

//早苗 霖之助
public class SanaeMob extends BaseMobEntity implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(SanaeMob.class, EntityDataSerializers.INT);

    private UUID persistentAngerTarget;

    public SanaeMob(EntityType<? extends Monster> type, Level level) {
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
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        Level level = this.level();
        if (!level.isClientSide) {
            if (this.getType() == ModEntityTypes.KOCHIYA_SANAE.get()){
                trade(player, level, hand, Items.GOLDEN_APPLE, 3, ModItems.byoukiheiyuMamori.get(), 10);
            }else if (this.getType() == ModEntityTypes.MORICHIKA_RINNOSUKE.get()){
                trade(player, level, hand, Items.TNT, 1, ModItems.spiritualStrikeTalisman.get(), 3);
            }
        }
        return super.mobInteract(player, hand);
    }

    /**
     * 实体简单交易
     * @param input 交易所需物品
     * @param inputCount 交易所需物品数量
     * @param output 交易获得物品
     * @param expAmount 交易经验
     */
    private InteractionResult trade(Player player, Level level, InteractionHand hand, Item input, int inputCount, Item output, int expAmount) {
        ItemStack handItem = player.getItemInHand(hand);
        if (handItem.getItem() == input && handItem.getCount() >= inputCount){
            handItem.shrink(inputCount);
            player.addItem(output.getDefaultInstance());
            level.addFreshEntity(new ExperienceOrb(level, this.getX(), getY(), getZ(), expAmount));
            level.playSound(player, this, SoundEvents.VILLAGER_WORK_TOOLSMITH, SoundSource.AMBIENT, 1.0f, 1.0f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurt(DamageSource source, float value) {
        if (!this.level().isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity livingAttacker) {
                // 被攻击后愤怒
                this.setPersistentAngerTarget(livingAttacker.getUUID());
                this.startPersistentAngerTimer();

                // 通知附近的同类型实体一起反击（可选）
                this.alertOthers(livingAttacker);
            }
        }
        return super.hurt(source, value);
    }

    // 通知附近同类型实体一起攻击
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
        this.spawnAtLocation(Items.EMERALD, Mth.randomBetweenInclusive(random, 2,16));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // 每 tick 减少愤怒时间
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }

    // 更新愤怒状态
    @Override
    public void updatePersistentAnger(ServerLevel level, boolean angerPersistent) {
        int angerTime = this.getRemainingPersistentAngerTime();
        LivingEntity target = this.getTarget();

        if (angerTime > 0) {
            this.setRemainingPersistentAngerTime(angerTime - 1);

            // 如果愤怒时间结束，清除愤怒目标
            if (this.getRemainingPersistentAngerTime() == 0) {
                this.setPersistentAngerTarget(null);
            }
        }

        // 如果有愤怒目标但当前没有攻击目标，尝试找到愤怒目标
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
        this.setRemainingPersistentAngerTime(600); // 30秒 = 600 tick
    }
}