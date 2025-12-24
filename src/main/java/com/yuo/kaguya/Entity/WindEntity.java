package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindEntity extends DanmakuBase {
    public static final EntityType<WindEntity> TYPE = EntityType.Builder.<WindEntity>of(WindEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25f).clientTrackingRange(6).updateInterval(10).noSave().build("wind");

    public WindEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public WindEntity(Level level, LivingEntity living, DanmakuColor danmakuColor) {
        super(TYPE, level, living);
        this.danmakuType = DanmakuType.WIND;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setMaxTicksExisted(MAX_TICKS_EXISTED);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount % 2 == 0) moveEntity();
    }

    /**
     * 应用旋风效果
     */
    private void moveEntity() {
        float radius = 3.0F;

        // 获取范围内的实体
        AABB area = this.getBoundingBox().inflate(radius, radius / 2, radius);
        for (Entity living : this.level().getEntitiesOfClass(Entity.class, area, this::isLook)) {
            // 计算距离和方向
            Vec3 toEntity = living.position().subtract(this.position());
            double distance = toEntity.length();
            if (distance < 0.1) continue;

            // 归一化方向向量
            Vec3 direction = toEntity.normalize();
            float strength = 0.5f * (float)(distance / (radius * 2));
            Vec3 pullForce = direction.reverse().scale(strength);
            living.setDeltaMovement(living.getDeltaMovement().add(pullForce));

            // 向上提升
            if (distance < radius) {
                living.setDeltaMovement(living.getDeltaMovement().add(0, 0.1, 0));
            }
        }

        // 影响方块（可选）
        if (this.tickCount % 10 == 0) {
            fallMoveBlocks(radius / 2);
        }
    }

    /**
     * 排除实体
     */
    private boolean isLook(Entity entity){
        if (entity instanceof LivingEntity living){
            if (!living.isAlive() || living.isRemoved()) return false;
            if (living instanceof Player player){
                return !player.isCreative() && !player.isSpectator();
            }
            return true;
        }
        return entity instanceof FallingBlockEntity;
    }

    /**
     * 影响方块（拔起植物、树叶等）
     */
    private void fallMoveBlocks(float radius) {
        int centerX = (int) this.getX();
        int centerY = (int) this.getY();
        int centerZ = (int) this.getZ();

        for (int dx = -(int) radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -(int) radius; dz <= radius; dz++) {
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (distance > radius) continue;

                    BlockPos pos = new BlockPos(centerX + dx, centerY + dy, centerZ + dz);
                    BlockState state = this.level().getBlockState(pos);

                    // 可以拔起的方块：植物、树叶等
                    if (state.getBlock() instanceof BushBlock || state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS)) {
                        if (this.random.nextFloat() < 0.1F) {
//                            this.level().destroyBlock(pos, true, this);
                            FallingBlockEntity.fall(level(), pos, state);
                        }
                    }
                }
            }
        }
    }
}
