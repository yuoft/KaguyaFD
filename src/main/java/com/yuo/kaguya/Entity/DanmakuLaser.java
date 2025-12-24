package com.yuo.kaguya.Entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;

public class DanmakuLaser extends DanmakuBase{
    public static final EntityType<DanmakuLaser> TYPE = EntityType.Builder.<DanmakuLaser>of(DanmakuLaser::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_laser");

    public DanmakuLaser(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DanmakuLaser(Level level, LivingEntity living, DanmakuColor danmakuColor) {
        super(TYPE, level, living);
        this.danmakuType = DanmakuType.LASER;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
    }
}
