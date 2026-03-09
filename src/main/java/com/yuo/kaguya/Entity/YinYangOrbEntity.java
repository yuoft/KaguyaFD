package com.yuo.kaguya.Entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class YinYangOrbEntity extends DanmakuBase{
    public static final EntityType<YinYangOrbEntity> TYPE = EntityType.Builder.<YinYangOrbEntity>of(YinYangOrbEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1f).clientTrackingRange(6).updateInterval(10).noSave().build("yinyang_orb");

    protected int reboundNum; // 当前已反弹次数
    protected final int MAX_REBOUND; // 最大反弹次数
    protected boolean isRebounding; // 是否正在反弹（用于避免重复反弹）
    protected static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(YinYangOrbEntity.class, EntityDataSerializers.FLOAT); //弹幕大小

    public YinYangOrbEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.MAX_REBOUND = 3;
        this.reboundNum = 0;
        this.isRebounding = false;
    }

    public YinYangOrbEntity(Level level, LivingEntity living, float size, int MAX_REBOUND) {
        super(TYPE, level, living);
        this.danmakuType = DanmakuType.YINYANG_ORB;
        this.danmakuColor = DanmakuColor.GRAY;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0.01f);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
        this.setDanmakuPierce(true);
        this.setDamage(size * 2 * danmakuType.getDamage());
        this.MAX_REBOUND = MAX_REBOUND;
        this.reboundNum = 0;
        this.isRebounding = false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (this.reboundNum >= MAX_REBOUND) {
            this.discard();
            return;
        }
        // 避免重复反弹（同一 tick 内多次触发）
        if (this.isRebounding) return;
        this.isRebounding = true; //标记状态

        // 获取碰撞面的法线方向
        Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
        Vec3 motion = this.getDeltaMovement();

        // 计算反弹后的速度：R = V - 2*(V·N)*N
        double dotProduct = motion.dot(normal);
        Vec3 reboundMotion = motion.subtract(normal.scale(2 * dotProduct));

        // 保留一部分速度（可能会有能量损失）
        double bounceFactor = 0.9; // 反弹系数，0.8 表示保留 80% 的速度
        reboundMotion = reboundMotion.scale(bounceFactor);
        this.setDeltaMovement(reboundMotion);

        // 增加反弹计数
        this.reboundNum++;
        this.playSound(SoundEvents.SLIME_ATTACK, 0.5F, 1.0F);
        this.isRebounding = false;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // 调用父类方法处理伤害
        super.onHitEntity(result);

        // 如果弹幕没有销毁（可能是穿透模式），则进行反弹
        if (this.isAlive()) {
            if (this.reboundNum >= MAX_REBOUND) {
                this.discard();
                return;
            }

            // 避免重复反弹
            if (this.isRebounding) return;
            this.isRebounding = true;

            // 获取实体碰撞点，计算反弹方向
            Vec3 hitPos = result.getLocation();
            Vec3 currentPos = this.position();

            // 计算从碰撞点到当前位置的方向（即反弹方向）
            Vec3 reboundDir = currentPos.subtract(hitPos).normalize();
            // 获取当前速度大小
            double speed = this.getDeltaMovement().length();

            // 设置反弹速度（方向为远离实体的方向）
            if (speed > 0.1) {
                this.setDeltaMovement(reboundDir.scale(speed * 0.8)); // 实体碰撞反弹损失更多速度
            } else {
                // 如果速度太小，给予一个基础反弹速度
                this.setDeltaMovement(reboundDir.scale(0.3));
            }

            this.reboundNum++;
            this.playSound(SoundEvents.SLIME_ATTACK, 0.5F, 1.0F);
            this.isRebounding = false;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;
        // 每 tick 重置反弹标志，确保下一 tick 可以再次反弹
//        if (this.isRebounding) {
//            this.isRebounding = false;
//        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE, 0.1f);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        float size = this.getSize();
        size = Math.max(0.1f, Math.min(1.0f, size));

        // 计算半宽和半高（使用尺寸值）
        float halfWidth = size / 2.0f;
        float height = size; // 让高度等于尺寸，保持比例

        // 创建以当前位置为中心的AABB
        // 调整Y轴偏移，使底部在实体中心下方一半高度处
        Vec3 pos = this.position();
        double minX = pos.x - halfWidth;
        double minY = pos.y - height / 2.0; // 中心在实体位置
        double minZ = pos.z - halfWidth;
        double maxX = pos.x + halfWidth;
        double maxY = pos.y + height / 2.0;
        double maxZ = pos.z + halfWidth;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * @param size >0.1f <1f
     */
    public void setSize(float size) {
        float i = Math.min(1, Math.max(0.1f, size));
        this.entityData.set(SIZE, i);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public int getReboundNum() {
        return reboundNum;
    }

    /**
     * 获取最大反弹次数
     */
    public int getMaxRebound() {
        return MAX_REBOUND;
    }
}
