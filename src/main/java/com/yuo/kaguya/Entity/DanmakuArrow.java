package com.yuo.kaguya.Entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class DanmakuArrow extends DanmakuBase {
    public static final EntityType<DanmakuArrow> TYPE = EntityType.Builder.<DanmakuArrow>of(DanmakuArrow::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_arrow");


    public DanmakuArrow(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DanmakuArrow(double x, double y, double z, Level level) {
        super(TYPE, x, y, z, level);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
    }

    public DanmakuArrow(Level level, LivingEntity living, DanmakuColor danmakuColor) {
        super(TYPE, level, living);
        this.danmakuType = DanmakuType.ARROW_SHOT;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
