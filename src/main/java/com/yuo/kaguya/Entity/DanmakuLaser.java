package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DanmakuLaser extends DanmakuBase{
    public static final EntityType<DanmakuLaser> TYPE = EntityType.Builder.<DanmakuLaser>of(DanmakuLaser::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_laser");
    private int laserNum; //穿透数

    public DanmakuLaser(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.danmakuType = DanmakuType.SHORT_LASER;
    }

    public DanmakuLaser(Level level, LivingEntity living, DanmakuType danmakuType, DanmakuColor danmakuColor) {
        super(TYPE, level, living);
        this.danmakuType = danmakuType;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
        this.setDanmakuPierce(true);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();
        BlockPos pos = this.getOnPos();
        if (!level.isClientSide){
            if (laserNum >= danmakuType.getIntSize() * 3){
                level.explode(this, pos.getX(), pos.getY(), pos.getZ(), 1.0f, ExplosionInteraction.MOB);
                this.discard();
            }
        }
    }

    @Override
    protected void doDamageEffects(LivingEntity owner, LivingEntity target) {
        super.doDamageEffects(owner, target);
        if (target.isOnFire()) target.setSecondsOnFire(3);
        laserNum++;

        if (target instanceof Creeper creeper){
            creeper.ignite();
        }
    }
//
//    @Override
//    protected void onHitEntity(EntityHitResult result) {
//        Entity entity = result.getEntity();
//        if (entity instanceof LivingEntity target) {
//
//            if (this.getOwner() instanceof LivingEntity living) {
//                if (target.hurt(DanmakuDamageTypes.danmaku(living, target), this.entityData.get(DAMAGE))){
//                    Vec3 vec3 = this.getDeltaMovement().scale(0.1);
//                    living.knockback(vec3.x, vec3.y, vec3.z);
//
//                }
//            }
//        }
//    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        Direction direction = result.getDirection();
        Level level = this.level();
        if (!level.isClientSide){
            BlockState state = level.getBlockState(pos);
            if (state.isFlammable(level, pos, direction) && this.getOwner() instanceof LivingEntity living){
                state.onCaughtFire(level, pos, direction, living); //点燃方块
            }
            level.explode(this, pos.getX(), pos.getY(), pos.getZ(), 1.0f * danmakuType.getIntSize(), ExplosionInteraction.MOB);
        }
        this.discard();
        super.onHitBlock(result);
    }
}
