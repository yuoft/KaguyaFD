package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DanmakuLaser0 extends DanmakuLaser{
    public static final EntityType<DanmakuLaser0> TYPE = EntityType.Builder.<DanmakuLaser0>of(DanmakuLaser0::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_laser0");
    // 添加字段
    private double orbitT = 0; // 螺旋位置参数 (0-1)
    private double orbitAngle = 0; // 初始角度

    public DanmakuLaser0(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.danmakuType = DanmakuType.SHORT_LASER;
    }

    public DanmakuLaser0(Level level, DanmakuLaser laser) {
        super(TYPE, level);
        this.danmakuType = DanmakuType.SHORT_LASER;
        this.danmakuColor = laser.getColor();
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setDanmakuPierce(true);
        this.setOwner(laser.getOwner());
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public int getMaxLaserPierceNum() {
        return 1;
    }

    // 添加方法
    public void setOrbitParameter(double t, double angle) {
        this.orbitT = t;
        this.orbitAngle = angle;
    }

    public double getOrbitT() {
        return orbitT;
    }

    public double getOrbitAngle() {
        return orbitAngle;
    }
}
