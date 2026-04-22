package com.yuo.kaguya;

import com.yuo.kaguya.Entity.BeamLaserEntity;
import com.yuo.kaguya.Entity.DanmakuColor;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("removal")
public class KaguyaUtils {
    public static ResourceLocation fa(String path) {
        return new ResourceLocation(Kaguya.MOD_ID, path);
    }

    public static ResourceLocation fa(String id, String path) {
        return new ResourceLocation(id, path);
    }

    public static ResourceLocation def(String path) {
        return new ResourceLocation(path);
    }

    public static ResourceLocation tayParse(String path) {
        try {
            return new ResourceLocation(path);
        } catch (ResourceLocationException var2) {
            return null;
        }
    }

    /**
     * 发射信标激光
     * @param isExplosion 是否爆炸
     */
    public static void spawnLaser(LivingEntity living, Level level, boolean isExplosion){
        // 计算视线方向
        Vec3 eyePosition = living.getEyePosition();
        Vec3 lookDirection = living.getViewVector(1.0F);
        Vec3 actualEnd = KaguyaUtils.getHitVec(eyePosition, lookDirection, level, living);
        double beamLength = eyePosition.distanceTo(actualEnd);
        if (beamLength < 1) {
            beamLength = 1;
        }
        if (!isExplosion && beamLength > 16){
            beamLength = 16;
        }

        DanmakuColor danmakuColor = DanmakuColor.random(level.random);

        try {
            BeamLaserEntity laser = new BeamLaserEntity(level, living, eyePosition, lookDirection, actualEnd, danmakuColor, beamLength);
            if (!isExplosion)
                laser.setExplosion(false);
            level.addFreshEntity(laser);
        } catch (Exception e) {
            System.err.println("Error creating laser beam: " + e.getMessage());
            e.printStackTrace();
        }

        level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);

    }

    /**
     * 获取固定距离玩家视线的方块坐标，有方块阻挡则在其前方
     */
    public static BlockPos getLookBlock(LivingEntity living, Level level) {
        // 固定生成距离（不随蓄力变化）
        float spawnDistance = 3.0F;

        // 计算生成位置
        Vec3 lookVec = living.getLookAngle().normalize();
        Vec3 eyePos = living.getEyePosition();
        Vec3 spawnPos = eyePos.add(lookVec.scale(spawnDistance));

        // 简单的防穿墙：射线检测
        HitResult hitResult = level.clip(new ClipContext(eyePos, spawnPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, living));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            // 如果视线被阻挡，在阻挡的方块面前生成
            spawnPos = hitResult.getLocation();
        }

        // 调整到地面
        return BlockPos.containing(spawnPos);
    }

    /**
     * 物品蓄力程度
     *
     * @param maxUseTime 物品最大蓄力时间
     * @param timeLeft   物品已蓄力时间
     * @param maxUse     最大有效使用时间
     */
    public static float getChargeRatio(int maxUseTime, int timeLeft, int maxUse) {
        int useDuration = maxUseTime - timeLeft;
        return Math.min((float) useDuration / maxUse, 1.0F);
    }

    /**
     * 物品蓄力程度
     *
     * @param maxUseTime 物品最大蓄力时间
     * @param timeLeft   物品已蓄力时间
     */
    public static float getChargeRatio(int maxUseTime, int timeLeft) {
        int useDuration = maxUseTime - timeLeft;
        return Math.min((float) useDuration / 40, 1.0F);
    }

    /**
     * 简单粒子激光
     */
    public static void spawnParticleLaser(Player player, Level level) {
        // 获取视线方向
        Vec3 startPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = getHitVec(startPos, lookVec, level, player);

        // 创建临时光束效果（使用MC原版效果）
        double num = Math.ceil(startPos.distanceToSqr(endPos));
        for (int i = 0; i < num; i++) {
            double progress = i / num;
            Vec3 pos = startPos.add(lookVec.scale(progress * startPos.distanceTo(endPos)));
            level.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 0, 0, 0);
            // 偶尔生成火花粒子
            if (level.random.nextFloat() < 0.1f) {
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }
    }

    /**
     * 获取玩家视线终点
     *
     * @param eyePosition   玩家视线位置
     * @param lookDirection 玩家视线方向
     */
    public static Vec3 getHitVec(Vec3 eyePosition, Vec3 lookDirection, Level level, LivingEntity living) {
        // 确保方向不为零向量
        if (lookDirection.lengthSqr() < 0.0001) {
            lookDirection = new Vec3(0, 1, 0); // 默认向上
        }
        // 计算射线终点（最大距离 128 格）
        double maxDistance = 128d;
        Vec3 endPosition = eyePosition.add(lookDirection.scale(maxDistance));

        // 进行方块碰撞检测
        HitResult hitResult = level.clip(new ClipContext(eyePosition, endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, living));

        // 确定光束实际终点
        Vec3 actualEnd;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            actualEnd = hitResult.getLocation();
        } else {
            actualEnd = endPosition;
        }

        return actualEnd;
    }

    /**
     * 获取玩家视线实体
     */
    public static Entity getHitEntity(Vec3 eyePosition, Vec3 lookDirection, Level level, Player player, double maxDistance) {
        // 获取所有可能被击中的实体
        List<Entity> entities = level.getEntities(player, player.getBoundingBox().expandTowards(lookDirection.scale(maxDistance)).inflate(1.0));

        Entity hitEntity = null;
        double closestDistance = maxDistance;

        for (Entity entity : entities) {
            // 跳过自己
            if (entity == player) continue;

            // 获取实体的边界框
            AABB entityBox = entity.getBoundingBox();

            // 检查射线是否与实体边界框相交
            Optional<Vec3> hitPos = entityBox.clip(eyePosition, eyePosition.add(lookDirection.scale(maxDistance)));

            if (hitPos.isPresent()) {
                double distance = eyePosition.distanceTo(hitPos.get());

                // 检查是否有方块遮挡（从眼睛到实体的路径上）
                Vec3 entityHitPoint = hitPos.get();
                HitResult blockHit = level.clip(new ClipContext(eyePosition, entityHitPoint, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

                // 如果没有方块遮挡，或者方块碰撞点在实体之后
                if (blockHit.getType() != HitResult.Type.BLOCK || eyePosition.distanceTo(blockHit.getLocation()) > distance - 0.1) {

                    // 更新最近的实体
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        hitEntity = entity;
                    }
                }
            }
        }

        return hitEntity;
    }

    /**
     * 获取玩家背包中的物品
     * @param player 玩家
     */
    public static ItemStack getPlayerBagItem(Player player, Item item){
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() == item){
            return mainHandItem;
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == item){
            return offhand;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item)
                return stack;
        }

        if (player.isCreative()) return new ItemStack(item, 64);
        return ItemStack.EMPTY;
    }
}
