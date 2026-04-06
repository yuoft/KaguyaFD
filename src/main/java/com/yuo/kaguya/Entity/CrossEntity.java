package com.yuo.kaguya.Entity;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CrossEntity extends Entity implements TraceableEntity {
    public static final EntityType<CrossEntity> TYPE = Builder.<CrossEntity>of(CrossEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(1).noSave().build("cross_entity");

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    protected static final EntityDataAccessor<Integer> ROTATION_TYPE = SynchedEntityData.defineId(CrossEntity.class, EntityDataSerializers.INT); //旋转类型

    // 旋转相关字段
    private RotationType rotationType = RotationType.HORIZONTAL; // 旋转类型
    private double radius = 3.0; // 旋转半径
    private double angle = 0; // 当前角度
    private double speed = 0.08; // 旋转速度
    private double heightOffset = 0; // 垂直偏移（用于垂直旋转）

    // 攻击相关字段
    private float damage = 7.0f; // 伤害值
    private int attackCooldown = 0; // 攻击冷却
    private static final int ATTACK_DELAY = 10; // 攻击间隔（tick）

    public CrossEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public CrossEntity(Level level, LivingEntity living) {
        super(TYPE, level);
        setOwner(living);
    }

    // 带参数的构造函数，用于设置旋转参数
    public CrossEntity(Level level, LivingEntity living, RotationType rotationType, double radius, double speed) {
        super(TYPE, level);
        setOwner(living);
        this.rotationType = rotationType;
        setRotationType(rotationType);
        this.radius = radius;
        this.speed = speed;
        this.setBoundingBox(this.makeBoundingBox());
    }

    // 完整参数构造函数
    public CrossEntity(Level level, LivingEntity living, RotationType rotationType, double radius, double speed, float damage) {
        this(level, living, rotationType, radius, speed);
        this.damage = damage;
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        if (getRotationTypeNum() == 1) {
            return new AABB(-0.5, -0.1, -0.55, 0.5, 0.15, 0.55).move(this.position());
        }else {
            return new AABB(-0.5, -0.55, -0.125, 0.5, 0.55, 0.125).move(this.position());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROTATION_TYPE, 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
        tag.putString("RotationType", this.rotationType.name());
        tag.putDouble("Radius", this.radius);
        tag.putDouble("Angle", this.angle);
        tag.putDouble("Speed", this.speed);
        tag.putDouble("HeightOffset", this.heightOffset);
        tag.putFloat("Damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }
        if (tag.contains("RotationType")) {
            try {
                this.rotationType = RotationType.valueOf(tag.getString("RotationType"));
            } catch (IllegalArgumentException e) {
                this.rotationType = RotationType.HORIZONTAL;
            }
        }
        this.radius = tag.getDouble("Radius");
        this.angle = tag.getDouble("Angle");
        this.speed = tag.getDouble("Speed");
        this.heightOffset = tag.getDouble("HeightOffset");
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        // 生命周期限制（60秒 = 1200 tick）
        if (tickCount >= 1200){
            this.discard();
            return;
        }

        Entity owner = getOwner();
        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        // 更新冷却
        if (attackCooldown > 0) {
            attackCooldown--;
        }

        // 更新角度
        angle += speed;
        if (angle >= Math.PI * 2) {
            angle -= Math.PI * 2;
        }

        // 根据旋转类型计算新位置
        Vec3 ownerPos = owner.position();
        Vec3 newPos = calculatePosition(ownerPos);

        this.setPos(newPos.x, newPos.y, newPos.z);

        // 攻击碰撞箱内的实体
        attackEntitiesInRange();
    }

    /**
     * 攻击范围内的实体
     */
    private void attackEntitiesInRange() {
        if (attackCooldown > 0) {
            return;
        }

        Entity owner = getOwner();
        // 获取碰撞箱范围内的实体
        AABB attackBox = this.getBoundingBox().inflate(0.3); // 稍微扩大一点攻击范围

        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, attackBox,
                entity -> {
                    if (entity == owner) return false;
                    if (!(entity instanceof LivingEntity)) return false;
                    if (entity instanceof TamableAnimal animal){
                        UUID animalOwnerUUID = animal.getOwnerUUID();
                        return ownerUUID == null || !ownerUUID.equals(animalOwnerUUID);
                    }
                    return true;
                });

        if (!entities.isEmpty()) {
            for (LivingEntity living : entities) {
                attackEntity(living);
            }
            attackCooldown = ATTACK_DELAY;
        }
    }

    /**
     * 攻击实体
     */
    private void attackEntity(LivingEntity target) {
        Entity owner = getOwner();
        DamageSource damageSource;

        if (owner instanceof LivingEntity livingOwner) {
            damageSource = this.damageSources().mobAttack(livingOwner);
        } else {
            damageSource = this.damageSources().magic();
        }

        // 造成伤害
        boolean hurt = target.hurt(damageSource, damage);

        target.setSecondsOnFire(1);

        if (hurt && owner instanceof LivingEntity livingOwner) {
            // 击退效果
            float knockback = 0.2f;
            target.knockback(knockback, knockback, knockback);
            target.hurtMarked = true;

            // 触发主人的攻击效果（如吸血、附魔等）
            livingOwner.doEnchantDamageEffects(livingOwner, target);
        }
    }

    private Vec3 calculatePosition(Vec3 center) {
        switch (rotationType) {
            case HORIZONTAL:
                // 水平旋转：绕Y轴画圆
                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);
                double y = center.y + heightOffset;
                return new Vec3(x, y, z);

            case VERTICAL:
                // 垂直旋转：绕X轴或Z轴画圆（使用X和Y轴）
                double verticalX = center.x + radius * Math.cos(angle);
                double verticalY = center.y + radius * Math.sin(angle);
                double verticalZ = center.z;
                return new Vec3(verticalX, verticalY, verticalZ);

//            case SPIRAL:
//                // 螺旋旋转：水平旋转的同时Y轴上下移动
//                double spiralX = center.x + radius * Math.cos(angle);
//                double spiralZ = center.z + radius * Math.sin(angle);
//                double spiralY = center.y + Math.sin(angle * 2) * (radius / 2);
//                return new Vec3(spiralX, spiralY, spiralZ);

            default:
                return center;
        }
    }

    // 获取玩家面前的旋转位置（用于垂直旋转时保持面向玩家）
    private Vec3 calculatePositionRelativeToPlayer(Vec3 center, Vec3 playerLookDirection) {
        // 计算垂直于玩家视线的平面
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = playerLookDirection.cross(up).normalize();
        Vec3 actualUp = right.cross(playerLookDirection).normalize();

        double x = center.x + right.x * radius * Math.cos(angle) + actualUp.x * radius * Math.sin(angle);
        double y = center.y + right.y * radius * Math.cos(angle) + actualUp.y * radius * Math.sin(angle);
        double z = center.z + right.z * radius * Math.cos(angle) + actualUp.z * radius * Math.sin(angle);

        return new Vec3(x, y, z);
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUUID = entity.getUUID();
            this.cachedOwner = entity;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true; // 允许碰撞
    }

    @Override
    public boolean isPickable() {
        return false; // 不能被玩家拾取
    }

    public int getRotationTypeNum(){
        return this.entityData.get(ROTATION_TYPE);
    }

    public void setRotationTypeNum(int type){
        this.entityData.set(ROTATION_TYPE, type);
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    // Getter 和 Setter 方法
    public void setRotationType(RotationType type) {
        this.rotationType = type;
        setRotationTypeNum(type.ordinal());
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = Math.max(0.5, radius);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getHeightOffset() {
        return heightOffset;
    }

    public void setHeightOffset(double heightOffset) {
        this.heightOffset = heightOffset;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = Math.max(0, damage);
    }

    // 旋转类型枚举
    public enum RotationType {
        HORIZONTAL,  // 水平旋转（绕Y轴）
        VERTICAL,    // 垂直旋转（绕X轴或Z轴）
        SPIRAL       // 螺旋旋转（结合水平和垂直）
    }
}
