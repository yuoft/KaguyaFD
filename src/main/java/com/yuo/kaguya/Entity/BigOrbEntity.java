package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BigOrbEntity extends DanmakuBase{
    public static final EntityType<BigOrbEntity> TYPE = EntityType.Builder.<BigOrbEntity>of(BigOrbEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1f).clientTrackingRange(6).updateInterval(10).noSave().build("big_orb");
    protected static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(BigOrbEntity.class, EntityDataSerializers.FLOAT); //弹幕大小

    public BigOrbEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BigOrbEntity(Level level, LivingEntity living, float size) {
        super(TYPE, level, living);
        this.danmakuType = DanmakuType.YINYANG_ORB;
        this.danmakuColor = DanmakuColor.random(this.level().random);
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setDanmakuPierce(true);
        this.setDamage(size * 2 * danmakuType.getDamage());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        Level level = this.level();
        if (level.isClientSide) return;
        level.explode(this, pos.getX(), pos.getY(), pos.getZ(), getSize() * 10f, ExplosionInteraction.MOB);
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Vec3 vec3 = result.getLocation();
        Level level = this.level();
        if (level.isClientSide) return;
        level.explode(this, vec3.x(), vec3.y(), vec3.z(), getSize() * 10f, ExplosionInteraction.MOB);
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE, 0.1f);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        float size = this.getSize();
        size = Math.max(0.1f, Math.min(1.0f, size));

        // 计算半宽和半高（使用尺寸值）
        float halfWidth = size / 2.0f;
        float height = size; // 让高度等于尺寸，保持比例

        // 创建以当前位置为中心的AABB
        // 调整Y轴偏移，使底部在实体中心下方一半高度处
        Vec3 pos = this.position();
        double minX = pos.x - halfWidth;
        double minY = pos.y - height / 2.0; // 中心在实体位置
        double minZ = pos.z - halfWidth;
        double maxX = pos.x + halfWidth;
        double maxY = pos.y + height / 2.0;
        double maxZ = pos.z + halfWidth;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * @param size >0.1f <1f
     */
    public void setSize(float size) {
        float i = Math.min(1, Math.max(0.1f, size));
        this.entityData.set(SIZE, i);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }
}
