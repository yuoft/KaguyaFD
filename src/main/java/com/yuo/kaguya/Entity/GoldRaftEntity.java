package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoldRaftEntity extends Boat {
    public static final EntityType<GoldRaftEntity> TYPE = EntityType.Builder.<GoldRaftEntity>of(GoldRaftEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("gold_raft");

    private int damageCooldown = 0;
    private boolean wasOnGround = false;

    public GoldRaftEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    public GoldRaftEntity(Level level) {
        super(TYPE, level);
    }

    @Override
    public @NotNull Item getDropItem() {
        return ModItems.kinkakuzi.get();
    }

    // 让船在快速下落时忽略与其他实体的碰撞（避免被实体挡住）
    @Override
    public boolean canCollideWith(Entity other) {
        if (this.getDeltaMovement().y < -0.3) {
            return false; // 下落时忽略实体碰撞，实现穿透
        }
        return super.canCollideWith(other);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.level();
        double vy = this.getDeltaMovement().y;

        if (level.isClientSide) {
            // 客户端视觉效果：下落烟雾
            if (vy < -0.5 && this.tickCount % 4 == 0) {
                level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() - 0.2, this.getZ(), 0, 0.1, 0);
            }
        } else {
            if (damageCooldown > 0) damageCooldown--;
            boolean onGround = this.onGround();

            // 检测落地瞬间（由空中变为着地）且下落速度足够
            if (!wasOnGround && onGround && vy < -0.5 && damageCooldown == 0) {
                applyImpactDamage(level, vy);
                damageCooldown = 10; // 0.5秒冷却
                // 自身轻微反弹，避免卡入地面
                this.setDeltaMovement(this.getDeltaMovement().x, 0.2, this.getDeltaMovement().z);
            }

            // 额外检测：如果船还在空中但已经极其接近正下方实体（防止穿透怪物却不落地的情况）
            if (!onGround && vy < -0.5 && damageCooldown == 0) {
                // 精确检测船下方0.2格内的实体
                AABB preciseBox = this.getBoundingBox().move(0, -0.2, 0).deflate(0.2, 0, 0.2);
                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, preciseBox, e -> !e.isSpectator() && e.isAlive());
                if (!entities.isEmpty()) {
                    applyImpactDamage(level, vy);
                    damageCooldown = 10;
                    this.setDeltaMovement(this.getDeltaMovement().x, 0.3, this.getDeltaMovement().z);
                }
            }

            wasOnGround = onGround;
        }
    }

    /**
     * 造成范围伤害 + 铁砧效果
     */
    private void applyImpactDamage(Level level, double vy) {
        float damage = (float) Math.min(40.0, -vy * 8.0);
        // 对周围1.5格内的实体造成范围伤害（类似铁砧）
        AABB rangeBox = this.getBoundingBox().inflate(1.2);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, rangeBox, e -> !e.isSpectator() && e.isAlive());
        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().anvil(this), damage);
            // 击退效果（稍微向上并后退）
            Vec3 knockback = new Vec3(0, 0.4, 0).add(target.position().subtract(this.position()).normalize().scale(0.6));
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            target.hurtMarked = true;
        }

        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.2F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY() + 0.3, this.getZ(), 30, 0.6, 0.3, 0.6, 0.1);
        }
    }
}