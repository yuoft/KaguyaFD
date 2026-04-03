package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DanmakuLaser extends DanmakuBase{
    public static final EntityType<DanmakuLaser> TYPE = EntityType.Builder.<DanmakuLaser>of(DanmakuLaser::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_laser");
    private int laserPierceNum; //穿透数
    private int maxLaserPierceNum; //最大穿透数
    private boolean isMainLaser; //是否为主激光

    public DanmakuLaser(EntityType<? extends ThrowableProjectile> entityType, Level level) {
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
        this.setDanmakuPierce(true);
        this.setMaxLaserPierceNum(danmakuType.getIntSize() * 3);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();
        BlockPos pos = this.getOnPos();
        if (!level.isClientSide){
            if (laserPierceNum >= getMaxLaserPierceNum()){
                level.explode(this, pos.getX(), pos.getY(), pos.getZ(), 1.0f, ExplosionInteraction.MOB);
                this.discard();
            }
        }
    }

    @Override
    protected void doDamageEffects(LivingEntity owner, LivingEntity target) {
        super.doDamageEffects(owner, target);
        if (target.isOnFire()) target.setSecondsOnFire(danmakuType.getIntSize());
        laserPierceNum++;

        if (target instanceof Creeper creeper){
            creeper.ignite();
        }
    }

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

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        // 确保主激光移除时清理环绕激光
       if (this.isMainLaser){
//           for (DanmakuLaser laser : lasers) {
//               if (laser != null && laser.isAlive()) {
//                   laser.discard();
//               }
//           }
       }
    }

    public void setLaserPierceNum(int laserPierceNum) {
        this.laserPierceNum = Math.max(1, laserPierceNum);
    }

    public void setMainLaser(boolean mainLaser) {
        isMainLaser = mainLaser;
    }

    public void setMaxLaserPierceNum(int maxLaserPierceNum) {
        this.maxLaserPierceNum = Math.max(1, maxLaserPierceNum);
    }

    public int getMaxLaserPierceNum() {
        return maxLaserPierceNum;
    }

}
