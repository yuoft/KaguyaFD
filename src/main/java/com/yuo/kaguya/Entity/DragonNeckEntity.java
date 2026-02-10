package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class DragonNeckEntity extends ThrowableItemProjectile {
    public static final EntityType<DragonNeckEntity> TYPE = EntityType.Builder.<DragonNeckEntity>of(DragonNeckEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("dragon_neck");

    public DragonNeckEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public DragonNeckEntity(Level level, LivingEntity living) {
        super(TYPE, living, level);
        this.setOwner(living);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.ryuuTama.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.MISS) {
            return;
        }
        Vec3 vec3 = result.getLocation();
        BlockPos pos = BlockPos.containing(vec3);
        Level level = this.level();

        if (this.getOwner() instanceof Player player){
            addColorLightBolt(vec3, level, player);

            if (!level.isClientSide) {
                DanmakuShootHelper.shootDanmakuHemisphereUniform(level, player, 50, DanmakuShootHelper.VAL_DEF, DanmakuShootHelper.INA_DEF,
                        DanmakuColor.random(level.random), DanmakuType.MEDIUM_BALL, pos, false);
            }

            addBeamLaser(pos, level, player);

        }
        this.discard();

    }

    /**
     * 四方水平4格
     * @param vec3 中心落点
     */
    private static void addColorLightBolt(Vec3 vec3, Level level, Player player) {
        Vec3[] spawnPositions = {
                vec3.relative(Direction.EAST, 4),
                vec3.relative(Direction.WEST, 4),
                vec3.relative(Direction.NORTH, 4),
                vec3.relative(Direction.SOUTH, 4),
                vec3.relative(Direction.EAST, 4).relative(Direction.NORTH, 4),
                vec3.relative(Direction.EAST, 4).relative(Direction.SOUTH, 4),
                vec3.relative(Direction.WEST, 4).relative(Direction.NORTH, 4),
                vec3.relative(Direction.WEST, 4).relative(Direction.SOUTH, 4)
        };

        for (Vec3 pos : spawnPositions) {
            LightningBolt colorLightBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            colorLightBolt.moveTo(pos);
            colorLightBolt.setCause(player instanceof ServerPlayer ? (ServerPlayer) player : null);
            colorLightBolt.setDamage(5f);
            colorLightBolt.setCustomName(Component.literal("kaguya:color_light_bolt"));
            level.addFreshEntity(colorLightBolt);
        }
    }

    /**
     * 四方水平8格，垂直16处往中心发射激光
     * @param pos 激光落点
     */
    private static void addBeamLaser(BlockPos pos, Level level, Player player){
        Vec3 targetCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        // 四个方向：东、南、西、北
        BlockPos[] spawnPositions = {
                pos.east(8).above(16),   // 东方
                pos.south(8).above(16),  // 南方
                pos.west(8).above(16),   // 西方
                pos.north(8).above(16)   // 北方
        };

        for (BlockPos spawnPos : spawnPositions) {
            // 发射位置（眼睛位置）
            Vec3 eyePosition = new Vec3(
                    spawnPos.getX() + 0.5,
                    spawnPos.getY() + 0.5,
                    spawnPos.getZ() + 0.5
            );

            // 计算看向目标的方向
            Vec3 lookDirection = targetCenter.subtract(eyePosition).normalize();

            BeamLaserEntity laser = new BeamLaserEntity(level, player, eyePosition, lookDirection, new Vec3(pos.getX(), pos.getY(), pos.getZ()),
                    DanmakuColor.random(level.random), 32);
            laser.setExplosion(false);
            laser.setMaxAge(60);
            level.addFreshEntity(laser);
        }
    }
}
