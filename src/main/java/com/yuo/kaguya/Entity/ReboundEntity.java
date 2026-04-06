package com.yuo.kaguya.Entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReboundEntity extends DanmakuBase{
    public static final EntityType<ReboundEntity> TYPE = EntityType.Builder.<ReboundEntity>of(ReboundEntity::new, MobCategory.MISC)
            .sized(1.5F, 1.5f).clientTrackingRange(6).updateInterval(10).noSave().build("rebound_shield");
    protected static final EntityDataAccessor<Float> FACE_YAW = SynchedEntityData.defineId(ReboundEntity.class, EntityDataSerializers.FLOAT);

    public ReboundEntity(EntityType<? extends net.minecraft.world.entity.projectile.ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ReboundEntity(Level level, LivingEntity living) {
        super(TYPE, level, living);
        this.danmakuType = DanmakuType.REBOUND_SHIELD;
        this.danmakuColor = DanmakuColor.GRAY;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setMaxTicksExisted(300);
        this.setNoGravity(true);

        this.setFaceY(living.yRotO);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        reboundProjectile(result.getEntity());
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        // 检测并反弹范围内的弹射物和弹幕
        reboundProjectilesInRange();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FACE_YAW, 0.0F);
    }

    public void setFaceY(float y){
        this.entityData.set(FACE_YAW, y);
    }

    public float getFaceY(){
        return this.entityData.get(FACE_YAW);
    }

    /**
     * 检测并反弹范围内的弹射物
     */
    private void reboundProjectilesInRange() {
        // 获取碰撞箱范围内的实体
        AABB reboundBox = this.getBoundingBox().inflate(0.5);

        // 检测所有弹射物（包括原版弹射物和弹幕）
        List<Projectile> projectiles = level().getEntitiesOfClass(Projectile.class, reboundBox,
                projectile -> {
                    if (projectile == this) return false;
                    if (projectile.getOwner() == this.getOwner()) return false;
                    return projectile.isAlive();
                });

        for (Projectile projectile : projectiles) {
            reboundProjectile(projectile);
        }
    }

    /**
     * 反弹弹射物
     */
    private void reboundProjectile(Projectile projectile) {
        Vec3 currentMotion = projectile.getDeltaMovement();

        // 直接反向运动方向
        projectile.setDeltaMovement(currentMotion.scale(-1));
        projectile.hurtMarked = true;

        // 更改拥有者为反弹盾的拥有者
        if (this.getOwner() instanceof LivingEntity owner) {
            projectile.setOwner(owner);
        }
    }

    /**
     * 反弹实体（碰到实体时的反弹）
     */
    private void reboundProjectile(Entity entity) {
        if (entity == null || entity == this.getOwner()) return;

        Vec3 entityPos = entity.position();
        Vec3 thisPos = this.position();
        Vec3 direction = entityPos.subtract(thisPos).normalize();

        // 给实体一个反冲力
        double knockback = 1.0;
        entity.setDeltaMovement(direction.x * knockback, direction.y * knockback + 0.3, direction.z * knockback);
        entity.hurtMarked = true;
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        return TYPE.getDimensions().makeBoundingBox(this.position());
    }
}
