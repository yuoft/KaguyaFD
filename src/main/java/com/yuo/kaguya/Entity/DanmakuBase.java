package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class DanmakuBase extends ThrowableItemProjectile {
    public static final EntityType<DanmakuBase> TYPE = EntityType.Builder.<DanmakuBase>of(DanmakuBase::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("danmaku");

    protected int MAX_TICKS_EXISTED; //生命周期
    protected static final int DEF_MAX_TICKS_EXISTED = 200; //生命周期
    protected DanmakuType danmakuType;
    protected DanmakuColor danmakuColor;
    protected static final EntityDataAccessor<Integer> DANMAKU_TYPE = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.INT); //弹幕类型--圆，方，星。。。
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.INT); //弹幕颜色
    protected static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.FLOAT); //弹幕攻击伤害
    protected static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.FLOAT); //重力

    public DanmakuBase(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        if (danmakuType == null)
            this.danmakuType = DanmakuType.TINY_BALL;
    }

    public DanmakuBase(EntityType<? extends ThrowableItemProjectile> entityType, Level level, LivingEntity living) {
        super(entityType, living, level);
        if (danmakuType == null)
            this.danmakuType = DanmakuType.TINY_BALL;
    }

    public DanmakuBase(Level level, LivingEntity living, DanmakuType danmakuType, DanmakuColor danmakuColor) {
        this(TYPE, level, living);
        this.danmakuType = danmakuType;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setDamage(this.danmakuType.getDamage());
        this.setGravityVelocity(0);
        this.setMaxTicksExisted(DEF_MAX_TICKS_EXISTED);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling();
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
        this.entityData.define(DANMAKU_TYPE, DanmakuType.TINY_BALL.ordinal());
        this.entityData.define(COLOR, DanmakuColor.GREEN.ordinal());
        this.entityData.define(DAMAGE, DanmakuType.TINY_BALL.getDamage());
        this.entityData.define(GRAVITY, 0f); //默认无重力
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity target && this.getOwner() instanceof LivingEntity living) {
            if (level().isClientSide) return;
            if (target.hurt(DanmakuDamageTypes.danmaku(living, target), this.entityData.get(DAMAGE))){
                Vec3 vec3 = this.getDeltaMovement().scale(0.1);
                living.knockback(vec3.x, vec3.y, vec3.z);
                this.discard();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % getMAX_TICKS_EXISTED() == 0) {
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

    public void setMaxTicksExisted(int ticksExisted) {
        MAX_TICKS_EXISTED = ticksExisted;
    }

    public int getMAX_TICKS_EXISTED() {
        return MAX_TICKS_EXISTED == 0 ? DEF_MAX_TICKS_EXISTED : MAX_TICKS_EXISTED;
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
