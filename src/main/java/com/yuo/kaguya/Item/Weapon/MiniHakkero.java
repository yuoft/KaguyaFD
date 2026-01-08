package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.BeamLaserEntity;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MiniHakkero extends KaguyaPrpo {
    public MiniHakkero() {
        super();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            try {
                // 计算视线方向
                Vec3 eyePosition = player.getEyePosition();
                Vec3 lookDirection = player.getViewVector(1.0F);

                // 确保方向不为零向量
                if (lookDirection.lengthSqr() < 0.0001) {
                    lookDirection = new Vec3(0, 1, 0); // 默认向上
                }

                // 计算射线终点（最大距离 64 格）
                double maxDistance = 64.0;
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

                // 计算光束长度
                double beamLength = eyePosition.distanceTo(actualEnd);

                // 确保长度有效
                if (beamLength < 0.1) {
                    beamLength = 0.1;
                }

                // 创建光束实体
                BeamLaserEntity laser = new BeamLaserEntity(level, player, eyePosition, lookDirection, beamLength);
                level.addFreshEntity(laser);

                // 播放音效
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);

                // 消耗耐久或设置冷却
                if (!player.getAbilities().instabuild) {
                    itemstack.hurtAndBreak(1, player, (p) -> {
                        p.broadcastBreakEvent(hand);
                    });
                }
                player.getCooldowns().addCooldown(this, 20); // 1秒冷却
            } catch (Exception e) {
                // 捕获任何异常，防止崩溃
                System.err.println("Error creating laser beam: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return InteractionResultHolder.success(itemstack);
    }
}
