package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GapEntity extends Entity {
    public static final EntityType<GapEntity> TYPE = EntityType.Builder.<GapEntity>of(GapEntity::new, MobCategory.MISC)
            .clientTrackingRange(6).updateInterval(10).fireImmune().build("gap");
    private static final int maxTick = 800;
    private int coolTime; //传送冷却

    public GapEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public GapEntity(Level level, BlockPos pos, float pitch) {
        super(TYPE, level);
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.setYRot(pitch);
    }

    @Override
    public void playerTouch(Player player) {
        if (!isTpCool()){
            KaguyaLevelSaveData.tpEntity(player, this, level());
        }
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();

        List<LivingEntity> entityList = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0f), e -> !(e instanceof Player) && e.isAlive());
        if (!entityList.isEmpty() && !isTpCool()) {
            for (LivingEntity living : entityList) {
                KaguyaLevelSaveData.tpEntity(living, this, level);
            }

        }

        if (!level.isClientSide) {
//            if (this.tickCount >= maxTick) this.removeData();

            if (this.coolTime > 0) this.coolTime--;
        }
    }

    /**
     * 移除数据并删除实体
     */
    public void removeData(){
        KaguyaLevelSaveData saveData = KaguyaLevelSaveData.get(level());
        saveData.removePos(uuid);
        this.discard();
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return true; // 设置为无敌
    }

    @Override
    public boolean isPushable() {
        return true; // 设置为不可推动
    }

    @Override
    public boolean shouldBeSaved() {
        return true; // 实体将被保存到世界文件中
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return new AABB(0.0, 0.0, 0, 1.0, 1.0, 1).move(this.position());
    }

    @Override
    protected AABB getBoundingBoxForPose(Pose p_20218_) {
        return super.getBoundingBoxForPose(p_20218_);
    }

    @Override
    protected AABB makeBoundingBox() {
        Direction direction = this.getDirection();
        AABB aabb = super.makeBoundingBox();
        switch (direction) {
            case NORTH, SOUTH: aabb = new AABB(-0.5, 0.0, -0.1, 0.5, 1.0, 0.1).move(this.position());break;
            case EAST, WEST: aabb = new AABB(-0.1, 0.0, -0.5, 0.1, 1.0, 0.5).move(this.position());break;
            default: break;
        }
        return aabb;
    }

    public int getCoolTime() {
        return coolTime;
    }

    public void setCoolTime(int coolTime) {
        this.coolTime = coolTime;
    }

    public boolean isTpCool(){
        return coolTime > 0;
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
