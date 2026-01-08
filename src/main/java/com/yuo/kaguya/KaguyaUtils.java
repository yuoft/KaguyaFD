package com.yuo.kaguya;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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
    public static void spawnParticleLaser(Player player, Level level, Item item){
        // 获取视线方向
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.getEyePosition();

        // 射线检测
        HitResult hitResult = player.pick(64.0, 0, false);
        Vec3 endPos = hitResult.getLocation();

        // 创建临时光束效果（使用MC原版效果）
        for (int i = 0; i < 10; i++) {
            double progress = i / 10.0;
            Vec3 pos = startPos.add(lookVec.scale(progress * startPos.distanceTo(endPos)));

            // 生成粒子效果
            level.addParticle(ParticleTypes.END_ROD,
                    pos.x, pos.y, pos.z,
                    0, 0, 0);
        }

        // 对路径上的实体造成伤害
        AABB beamBox = new AABB(startPos, endPos).inflate(0.5);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, beamBox)) {
            if (entity != player) {
                entity.hurt(level.damageSources().magic(), 5.0f);
                entity.setSecondsOnFire(3);
            }
        }

        // 冷却
        player.getCooldowns().addCooldown(item, 20);
    }
}
