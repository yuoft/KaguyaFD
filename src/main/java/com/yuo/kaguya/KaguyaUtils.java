package com.yuo.kaguya;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class KaguyaUtils {
    public static ResourceLocation fa(String path){
        return new ResourceLocation(Kaguya.MOD_ID, path);
    }

    public static ResourceLocation def(String path){
        return new ResourceLocation(path);
    }

    /**
     * 获取固定距离玩家视线的方块坐标，有方块阻挡则在其前方
     */
    public static BlockPos getLookBlock(LivingEntity living, Level level){
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
     * @param maxUseTime 物品最大蓄力时间
     * @param timeLeft 物品已蓄力时间
     * @param maxUse 最大有效使用时间
     */
    public static float getChargeRatio(int maxUseTime, int timeLeft, int maxUse ){
        int useDuration = maxUseTime - timeLeft;
        return Math.min((float) useDuration / maxUse, 1.0F);
    }

    /**
     * 简单粒子激光
     */
    public static void spawnParticleLaser(Player player, Level level){
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
     * @param eyePosition 玩家视线位置
     * @param lookDirection 玩家视线方向
     */
    public static Vec3 getHitVec(Vec3 eyePosition, Vec3 lookDirection, Level level, Player player){
        // 确保方向不为零向量
        if (lookDirection.lengthSqr() < 0.0001) {
            lookDirection = new Vec3(0, 1, 0); // 默认向上
        }
        // 计算射线终点（最大距离 128 格）
        double maxDistance = 128d;
        Vec3 endPosition = eyePosition.add(lookDirection.scale(maxDistance));

        // 进行方块碰撞检测
        HitResult hitResult = level.clip(new ClipContext(eyePosition, endPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

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
     * 寻找此玩家的某个物品
     */
    public static ItemStack findItemByPlayer(Player player, Item item){
        int slotMatchingItem = player.getInventory().findSlotMatchingItem(new ItemStack(item));
        if (slotMatchingItem > 0 && slotMatchingItem < 41) {
            return player.getInventory().getItem(slotMatchingItem);
        }
        return ItemStack.EMPTY;
    }
}
