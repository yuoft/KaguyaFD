package com.yuo.kaguya.Entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DanmakuLaser extends DanmakuBase{
    public static final EntityType<DanmakuLaser> TYPE = EntityType.Builder.<DanmakuLaser>of(DanmakuLaser::new, MobCategory.MISC)
//            .sized(0.25F, 0.25F)
            .clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_laser");
    protected static final EntityDataAccessor<Integer> LENGTH = SynchedEntityData.defineId(DanmakuLaser.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(DanmakuLaser.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(DanmakuLaser.class, EntityDataSerializers.FLOAT);

    private int length;
    public DanmakuLaser(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.setLength(3);
    }

    public DanmakuLaser(Level level, LivingEntity living, DanmakuType danmakuType, DanmakuColor danmakuColor) {
        super(TYPE, level, living);
        this.danmakuType = danmakuType;
        this.danmakuColor = danmakuColor;
        this.length = this.danmakuType.getIntSize();
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
        this.setLength(this.length);
        this.setYaw(living.getXRot());
        this.setPitch(living.getYRot());
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return new AABB(0,0,0, 0.25f, 0.25f, 0.25f);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 movement = this.getDeltaMovement();
        if (movement.lengthSqr() > 0.001) {
            // 更新实体朝向
            double yawRad = Math.atan2(movement.z(), movement.x());
            this.setYRot((float)Math.toDegrees(yawRad) - 90.0F);

            double horizontal = Math.sqrt(movement.x() * movement.x() + movement.z() * movement.z());
            this.setXRot((float)-Math.toDegrees(Math.atan2(movement.y(), horizontal)));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LENGTH, 0);
        this.entityData.define(YAW, 0f);
        this.entityData.define(PITCH, 0f);
    }

    public float getYaw() {
        return this.entityData.get(YAW);
    }

    public void setYaw(float yaw) {
        this.entityData.set(YAW, yaw);
    }

    public float getPitch() {
        return this.entityData.get(PITCH);
    }

    public void setPitch(float pitch) {
        this.entityData.set(PITCH, pitch);
    }

    public int getLength() {
        return this.entityData.get(LENGTH);
    }

    public void setLength(int length) {
        this.entityData.set(LENGTH, length);
    }
}
