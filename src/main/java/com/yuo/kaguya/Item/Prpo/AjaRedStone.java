package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.BeamLaserEntity;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class AjaRedStone extends KaguyaPrpo {

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int i) {
        if (living instanceof Player player) {
            float chargeRatio = KaguyaUtils.getChargeRatio(getUseDuration(stack), i, 150);
            int lightValue = level.getBrightness(LightLayer.BLOCK, BlockPos.containing(player.getEyePosition()));
            if (lightValue <= 0) { //亮度大于0
                if (level.isClientSide) {
                    player.displayClientMessage(Component.translatable("info.kaguya.aja_red_stone.no_light"), true);
                }
                return;
            }

            if (chargeRatio > 0.1f) {
                float damage = Math.min(30, lightValue * chargeRatio * 2);
                Vec3 eyePosition = player.getEyePosition();
                Vec3 lookDirection = player.getViewVector(1.0F);
                Vec3 actualEnd = KaguyaUtils.getHitVec(eyePosition, lookDirection, level, player);

                BeamLaserEntity beamLaser = new BeamLaserEntity(level, player, eyePosition, lookDirection, actualEnd, DanmakuColor.WHITE, getLengthForRenderDistance());
                beamLaser.setDamage(damage);
                beamLaser.setExplosion(false); //不爆炸
                level.addFreshEntity(beamLaser);

                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        super.releaseUsing(stack, level, living, i);
    }

    /**
     * 根据设置的渲染距离计算激光长度 16 < dis < 512
     */
    public static int getLengthForRenderDistance() {
        Minecraft mc = Minecraft.getInstance();
        return Math.min(512, Math.max(16, mc.options.renderDistance().get() * 16));
    }
}
