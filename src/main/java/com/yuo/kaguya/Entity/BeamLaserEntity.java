package com.yuo.kaguya.Entity;

import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class BeamLaserEntity extends Entity {
    // 用于数据同步的字段
    protected static final EntityDataAccessor<Float> START_X = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> START_Y = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> START_Z = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> END_X = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> END_Y = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> END_Z = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(BeamLaserEntity.class, EntityDataSerializers.INT);
    private final int maxAge = 200; // 存在1秒（20 ticks）
    private float damage;
    private Player owner;
    private Vec3 startPos;
    private Vec3 laserDirection;
    private Vec3 explosionPos;
    private double length;
    private int age = 0;

    public BeamLaserEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setInvulnerable(true);
        this.laserDirection = new Vec3(0, 1, 0);
        this.startPos = Vec3.ZERO;
        this.explosionPos = Vec3.ZERO;
        this.length = 10.0;
        this.damage = 5.0f;
    }

    public BeamLaserEntity(Level level, Player owner, Vec3 startPos, Vec3 direction, Vec3 explosionPos, DanmakuColor color, double length) {
        this(ModEntityTypes.BEAM_LASER.get(), level);

        // 确保参数不为null
        if (owner == null || startPos == null || direction == null || explosionPos == null) {
            throw new IllegalArgumentException("error arguments is null");
        }

        this.owner = owner;
        this.startPos = startPos;
        this.laserDirection = direction.normalize(); // 确保标准化
        this.explosionPos = explosionPos;
        this.length = Math.max(0.1, length); // 确保最小长度

        // 设置实体位置为起点
        this.setPos(startPos.x, startPos.y, startPos.z);
        this.setAge(0);
        this.setStartPos(this.startPos);
        this.setLaserDirection(this.laserDirection);
        this.setLength((float) this.length);
    }

    @Override
    public void tick() {
        super.tick();

        // 确保必要字段已初始化
        if (laserDirection == null) {
            laserDirection = new Vec3(0, 1, 0);
        }
        if (startPos == null) {
            startPos = this.position();
        }

        age++;
        setAge(age);

        if (age >= maxAge) {
            spawnExplosion();
            this.discard(); // 时间到，消失
            return;
        }

        try {
            checkCollision();
        } catch (NullPointerException e) {
            // 如果发生NPE，记录错误并销毁实体
            System.err.println("BeamLaserEntity NPE in checkCollision: " + e.getMessage());
            this.discard();
        }
    }

    /**
     * 爆炸
     */
    private void spawnExplosion(){
        if (this.explosionPos == null) return;
        level().explode(this, explosionPos.x, explosionPos.y, explosionPos.z,16f, ExplosionInteraction.BLOCK);
    }

    /**
     * 碰撞检测
     */
    private void checkCollision() {
        if (laserDirection == null || startPos == null) {
            return;
        }

        // 获取光束路径上的所有实体
        Vec3 endPos = startPos.add(this.laserDirection.scale(length));
        AABB beamBox = new AABB(startPos, endPos).inflate(0.5); // 扩大0.5格检测范围
        List<Entity> entities = level().getEntities(this, beamBox);

        for (Entity entity : entities) {
            if (entity == owner || entity == this) continue;
            if (entity instanceof LivingEntity livingEntity) {
                if (isEntityInBeamPath(livingEntity)) {
                    livingEntity.hurt(level().damageSources().indirectMagic(this, owner), damage);
                    livingEntity.setSecondsOnFire(1);
                    Vec3 knockback = this.laserDirection.scale(0.5);
                    livingEntity.push(knockback.x, knockback.y, knockback.z);
                }
            }
        }
    }

    /**
     * 检查实体是否真的在光束路径上（更精确的检测）
     */
    private boolean isEntityInBeamPath(LivingEntity entity) {
        if (laserDirection == null || startPos == null) {
            return false;
        }

        // 获取实体中心位置
        Vec3 entityPos = entity.position().add(0, entity.getBbHeight() / 2, 0);

        // 计算点到直线的距离
        Vec3 toEntity = entityPos.subtract(startPos);
        double projection = toEntity.dot(laserDirection); // 在方向上的投影长度

        // 如果投影在光束长度范围内
        if (projection >= 0 && projection <= length) {
            // 计算垂直距离
            Vec3 closestPoint = startPos.add(laserDirection.scale(projection));
            double distanceSq = entityPos.distanceToSqr(closestPoint);

            // 如果距离小于实体半径+光束半径（假设光束半径为0.3）
            return distanceSq <= Math.pow(entity.getBbWidth() / 2 + 0.3, 2);
        }

        return false;
    }

    public Vec3 getStartPos() {
        return new Vec3(this.entityData.get(START_X), this.entityData.get(START_Y), this.entityData.get(START_Z));
    }

    public void setStartPos(Vec3 startPos) {
        this.entityData.set(START_X, (float) startPos.x);
        this.entityData.set(START_Y, (float) startPos.y);
        this.entityData.set(START_Z, (float) startPos.z);
    }

    public Vec3 getLaserDirection() {
        return new Vec3(this.entityData.get(END_X), this.entityData.get(END_Y), this.entityData.get(END_Z));
    }

    public void setLaserDirection(Vec3 laserDirection) {
        this.entityData.set(END_X, (float) laserDirection.x);
        this.entityData.set(END_Y, (float) laserDirection.y);
        this.entityData.set(END_Z, (float) laserDirection.z);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public DanmakuColor getColor(){
        return DanmakuColor.getColor(this.entityData.get(COLOR));
    }

    public void setColor(DanmakuColor color){
        this.entityData.set(COLOR, color.ordinal());
    }

    public float getLength() {
        return this.entityData.get(LENGTH);
    }

    public void setLength(float length) {
        this.entityData.set(LENGTH, length);
    }

    public int getAge() {
        return this.entityData.get(AGE);
    }

    public void setAge(int age) {
        this.entityData.set(AGE, age);
    }

    public int getMaxAge() {
        return maxAge;
    }

    // 序列化/反序列化方法
    @Override
    protected void defineSynchedData() {
        this.entityData.define(START_X, 0.0f);
        this.entityData.define(START_Y, 0.0f);
        this.entityData.define(START_Z, 0.0f);
        this.entityData.define(END_X, 0.0f);
        this.entityData.define(END_Y, 0.0f);
        this.entityData.define(END_Z, 0.0f);
        this.entityData.define(AGE, 60);
        this.entityData.define(LENGTH, 1f);
        this.entityData.define(COLOR, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }
}