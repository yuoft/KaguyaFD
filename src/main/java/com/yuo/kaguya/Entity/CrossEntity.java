package com.yuo.kaguya.Entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CrossEntity extends Entity implements TraceableEntity {
    public static final EntityType<CrossEntity> TYPE = Builder.<CrossEntity>of(CrossEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(1).noSave().build("cross_entity");

    // 攻击相关
    private float damage;
    private final Set<UUID> damagedEntities = new HashSet<>();

    // 所有者
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;

    // 同步数据（当前角度，用于客户端渲染）
    protected static final EntityDataAccessor<Float> SYNC_ANGLE = SynchedEntityData.defineId(CrossEntity.class, EntityDataSerializers.FLOAT); //初始挥砍角度
    protected static final EntityDataAccessor<Float> STAR_ANGLE = SynchedEntityData.defineId(CrossEntity.class, EntityDataSerializers.FLOAT); //挥砍动画角度
    protected static final EntityDataAccessor<Integer> MAX_TICK = SynchedEntityData.defineId(CrossEntity.class, EntityDataSerializers.INT); //最大动画时间
    protected static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(CrossEntity.class, EntityDataSerializers.FLOAT); //实体基础水平角

    public CrossEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    /**
     * 构造挥砍实体（剑柄固定在玩家前方固定点，仅旋转剑身）
     * @param level 世界
     * @param owner 发起者
     * @param damage 伤害
     * @param startAngle 挥砍动画运行角度（度）
     * @param syncAngle 挥砍角度（度）
     * @param maxTick 时间tick
     */
    public CrossEntity(Level level, LivingEntity owner, float damage, float startAngle, float syncAngle, int maxTick) {
        super(TYPE, level);
        setOwner(owner);
        this.damage = damage;
        setSyncAngle(syncAngle);
        setStartAngle(startAngle);
        setMaxTick(maxTick);

        this.setSyncAngle(syncAngle);
        // 计算剑柄固定点（玩家眼睛前方1格）
        Vec3 eyePos = owner.getEyePosition();
        Vec3 lookVec = owner.getLookAngle();
        this.setPos(eyePos.add(lookVec.scale(2.0)));
        this.setYRot(-startAngle + owner.getYRot());
        setNewYRot(owner.getYRot());
        this.setXRot(owner.getXRot());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        Entity owner = getOwner();
        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        this.setYRot(getYAngle());

        if (this.tickCount >= getMaxTick()) {
            this.discard();
            return;
        }

        attackEntitiesInArc();
    }

    /**
     * 检测扇形区域内的实体并造成伤害。
     * 扇形：以 pivotPoint 为起点，沿玩家视线方向为中心，左右各 60 度（根据挥砍当前角度可动态？简化：直接使用当前剑的方向）。
     * 但这里为了简单且符合直觉，我们直接基于当前角度计算剑尖方向，然后检测前方锥形。
     * 更简单：使用射线检测+角度检查。
     */
    private void attackEntitiesInArc() {
        Entity owner = getOwner();
        if (owner == null) return;

        // 攻击方向：实体的视线方向（包含水平与俯仰）
        Vec3 attackDir = this.getLookAngle(); // 根据实体的 yRot 和 xRot 计算

        double range = 5;
        double halfAngle = Math.toRadians(getStartAngle()); // 圆锥半角 60°，总圆锥角 120°

        AABB aabb = this.getBoundingBox().inflate(range, range, range);
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, aabb,
                entity -> {
                    if (entity == owner) return false;
                    if (damagedEntities.contains(entity.getUUID())) return false;
                    if (entity instanceof TamableAnimal animal) {
                        UUID ownerUUID = getOwnerUUID();
                        return ownerUUID == null || !ownerUUID.equals(animal.getOwnerUUID());
                    }
                    return true;
                });

        for (LivingEntity target : targets) {
            Vec3 toTarget = target.position().add(0, target.getBbHeight() / 2, 0).subtract(this.position()).normalize();
            double dot = toTarget.dot(attackDir);
            if (dot > Math.cos(halfAngle)) { // 在圆锥形区域内
                double distSq = target.distanceToSqr(this.position());
                if (distSq <= range * range) {
                    attackEntity(target);
                    damagedEntities.add(target.getUUID());
                }
            }
        }
    }

    private void attackEntity(LivingEntity target) {
        Entity owner = getOwner();
        DamageSource damageSource;
        if (owner instanceof LivingEntity livingOwner) {
            damageSource = this.damageSources().mobAttack(livingOwner);
        } else {
            damageSource = this.damageSources().magic();
        }
        boolean hurt = target.hurt(damageSource, damage);
        if (hurt) {
            Vec3 knockDir = target.position().subtract(this.position()).normalize();
            target.knockback(0.3, knockDir.x, knockDir.z);
            target.setSecondsOnFire(1);
            if (owner instanceof LivingEntity livingOwner) {
                livingOwner.doEnchantDamageEffects(livingOwner, target);
            }
        }
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

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SYNC_ANGLE, 0.0f);
        this.entityData.define(STAR_ANGLE, 60f);
        this.entityData.define(MAX_TICK, 120);
        this.entityData.define(Y_ROT, 0.0f);
    }

    public float getStartAngle() {
        return this.entityData.get(STAR_ANGLE);
    }

    public void setStartAngle(float startAngle) {
        this.entityData.set(STAR_ANGLE, startAngle);
    }

    public int getMaxTick(){
        return this.entityData.get(MAX_TICK);
    }

    public void setMaxTick(int tick){
        this.entityData.set(MAX_TICK, tick);
    }

    public float getNewYRot(){
        return this.entityData.get(Y_ROT);
    }

    public void setNewYRot(float angle){
        this.entityData.set(Y_ROT, angle);
    }

    public float getSyncAngle() {
        return this.entityData.get(SYNC_ANGLE);
    }

    public void setSyncAngle(float syncAngle) {
        this.entityData.set(SYNC_ANGLE, syncAngle);
    }

    /**
     * 根据tick获取yrot实时角度
     */
    public float getYAngle(){
        int durationTicks = getMaxTick(); // 使用自定义的最大刻数
        float t = Math.min(1.0f, (float) tickCount / durationTicks);
        // 从 -startAngle 线性增加到 startAngle（假设 startAngle 为正）
        float start = -getStartAngle();
        float end = getStartAngle();
        return start + (end - start) * t + getNewYRot();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }
        this.damage = tag.getFloat("Damage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
        tag.putFloat("Damage", this.damage);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        // 碰撞箱很小，因为剑的伤害由范围检测处理
        return new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5).move(this.position());
    }

    @Override
    public boolean canBeCollidedWith() { return false; }
    @Override
    public boolean isPickable() { return false; }
}