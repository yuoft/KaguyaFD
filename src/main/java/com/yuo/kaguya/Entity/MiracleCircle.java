package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

public class MiracleCircle extends Entity {
    public static final EntityType<MiracleCircle> TYPE = EntityType.Builder.<MiracleCircle>of(MiracleCircle::new, MobCategory.MISC)
            .sized(0.1F, 0.1f).clientTrackingRange(6).updateInterval(10).noSave().build("miracle_circle");
    private static final String NBT_TIME = "kaguya:miracle_circle_time";

    public MiracleCircle(EntityType<?> type, Level level) {
        super(type, level);
    }

    public MiracleCircle(Level level, float time, BlockPos pos) {
        super(TYPE, level);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount % 800 == 0)
            this.discard();
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }
}
