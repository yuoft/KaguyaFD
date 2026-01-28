package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class DanmakuBase extends ThrowableItemProjectile {
    public static final EntityType<DanmakuBase> TYPE = EntityType.Builder.<DanmakuBase>of(DanmakuBase::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("danmaku");

    protected int MAX_TICKS_EXISTED; //生命周期
    protected static final int DEF_MAX_TICKS_EXISTED = 200; //生命周期
    protected DanmakuType danmakuType;
    protected DanmakuColor danmakuColor;
    protected boolean isDanmakuPierce; //是否在击中实体后销毁
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
        super.onHitEntity(result); // 调用父类方法

        Entity entity = result.getEntity();
        if (entity == this || entity == this.getOwner()) {
            return;
        }

        if (entity instanceof LivingEntity target) {
            DamageSource damageSource;
            if (this.getOwner() instanceof LivingEntity owner){
                damageSource = DanmakuDamageTypes.danmaku(owner, target);
            }else {
                damageSource = this.damageSources().generic();
            }

            if (this.getOwner() instanceof LivingEntity owner) {
                // 设置最后攻击者
                target.setLastHurtByMob(owner);
                // 应用伤害
                if (target.hurt(damageSource, getDamage())) {
                    // 触发玩家攻击成功事件（如果攻击者是玩家）
                    if (target != owner && target instanceof Player && owner instanceof ServerPlayer && !this.isSilent()) {
                        ((ServerPlayer)owner).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                    }

                    if (!level().isClientSide) {
                        this.knockbackTarget(owner, target);
                        this.doDamageEffects(owner, target);
                        if (!isDanmakuPierce()) this.discard();
                    }
                }
            }
        }
    }

    /**
     * 弹幕击退
     */
    private void knockbackTarget(LivingEntity owner, LivingEntity target) {
        double d0 = Math.max(0.0, 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(0.2 * d0);
        if (vec3.lengthSqr() > 0.0) {
            target.push(vec3.x, 0.1, vec3.z);
        }
    }

    /**
     * 触发效果（如火焰、附加buff等）
     */
    protected void doDamageEffects(LivingEntity owner, LivingEntity target) {

    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= getMAX_TICKS_EXISTED()) {
            this.discard();
        }
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        double inflate = (this.getDanmakuType().getSize() - 0.2d) / 2;
        return new AABB(-0.125, 0.0, -0.125, 0.125, 0.25, 0.125).inflate(inflate).move(this.position());
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
        this.setDamage(type.getDamage());
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

    public boolean isDanmakuPierce() {
        return isDanmakuPierce;
    }

    public void setDanmakuPierce(boolean danmakuPierce) {
        isDanmakuPierce = danmakuPierce;
    }
}
