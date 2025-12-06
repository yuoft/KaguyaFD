package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class DanmakuBase extends ThrowableItemProjectile {
    public static final EntityType<DanmakuBase> TYPE = EntityType.Builder.<DanmakuBase>of(DanmakuBase::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("danmaku");

    protected static final int MAX_TICKS_EXISTED = 200; //生命周期
    protected static final EntityDataAccessor<Integer> DANMAKU_TYPE = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.INT); //弹幕类型--圆，方，星。。。
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.INT); //弹幕颜色
    protected static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.FLOAT); //弹幕攻击伤害
    protected static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.FLOAT); //重力

    public DanmakuBase(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DanmakuBase(EntityType<? extends ThrowableItemProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    public DanmakuBase(EntityType<? extends ThrowableItemProjectile> entityType, Level level, LivingEntity living) {
        super(entityType, living, level);
    }

    public DanmakuBase(double x, double y, double z, Level level) {
        this(TYPE, x, y, z, level);
    }

    public DanmakuBase(Level level, LivingEntity living) {
        this(TYPE, level, living);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.heartShot.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DANMAKU_TYPE, DanmakuType.PELLET.ordinal());
        this.entityData.define(COLOR, DanmakuColor.RED.ordinal());
        this.entityData.define(DAMAGE, 1.0f);
        this.entityData.define(GRAVITY, 0f); //默认无重力
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity living) {
            living.hurt(this.damageSources().generic(), this.entityData.get(DAMAGE));
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % MAX_TICKS_EXISTED == 0) {
            this.discard();
        }
    }

    //重力
    @Override
    protected float getGravity() {
        return this.entityData.get(GRAVITY);
    }

    public DanmakuBase setGravityVelocity(float gravity) {
        this.entityData.set(GRAVITY, gravity);
        return this;
    }

    public DanmakuType getDanmakuType() {
        return DanmakuType.getType(this.entityData.get(DANMAKU_TYPE));
    }

    public DanmakuBase setDanmakuType(DanmakuType type) {
        this.entityData.set(DANMAKU_TYPE, type.ordinal());
        return this;
    }

    public DanmakuColor getColor() {
        return DanmakuColor.getColor(this.entityData.get(COLOR));
    }

    public DanmakuBase setColor(DanmakuColor color) {
        this.entityData.set(COLOR, color.ordinal());
        return this;
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public DanmakuBase setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
        return this;
    }
}
