package com.yuo.kaguya.Entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;

public class DanmakuButterfly extends DanmakuBase {
    public static final EntityType<DanmakuButterfly> TYPE = EntityType.Builder.<DanmakuButterfly>of(DanmakuButterfly::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_fly");

    private static final int MAX_TICKS_EXISTED = 2000;

    public DanmakuButterfly(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DanmakuButterfly(double x, double y, double z, Level level) {
        super(TYPE, x, y, z, level);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
    }

    public DanmakuButterfly(Level level, LivingEntity living, DanmakuType danmakuType, DanmakuColor danmakuColor) {
        super(TYPE, level, living);
        this.danmakuType = danmakuType;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
