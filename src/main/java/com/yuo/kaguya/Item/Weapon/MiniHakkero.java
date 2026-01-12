package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.BeamLaserEntity;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MiniHakkero extends KaguyaPrpo {
    public MiniHakkero() {
        super(new Properties().stacksTo(1).durability(99));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!player.isCrouching()){
            int damageValue = itemstack.getDamageValue();
            if (damageValue >= 99) return InteractionResultHolder.fail(itemstack);
            // 计算视线方向
            Vec3 eyePosition = player.getEyePosition();
            Vec3 lookDirection = player.getViewVector(1.0F);
            Vec3 actualEnd = KaguyaUtils.getHitVec(eyePosition, lookDirection, level, player);
            double beamLength = eyePosition.distanceTo(actualEnd);
            if (beamLength < 1) {
                beamLength = 1;
            }

            DanmakuColor danmakuColor = DanmakuColor.random(level.random);

            try {
                BeamLaserEntity laser = new BeamLaserEntity(level, player, eyePosition, lookDirection, actualEnd, danmakuColor, beamLength);
                level.addFreshEntity(laser);
            } catch (Exception e) {
                System.err.println("Error creating laser beam: " + e.getMessage());
                e.printStackTrace();
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                itemstack.hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(hand);
                });
            }
            player.getCooldowns().addCooldown(this, 20); // 1秒冷却
            return InteractionResultHolder.success(itemstack);
        }else {
            if (itemstack.getDamageValue() > 0){
                ItemStack fireCharge = KaguyaUtils.findItemByPlayer(player, Items.FIRE_CHARGE);
                if (!fireCharge.isEmpty()) {
                    if (fireCharge.getCount() >= 32){
                        if (!player.getAbilities().instabuild) fireCharge.shrink(32);
                        itemstack.setDamageValue(itemstack.getDamageValue() - 1);
                        player.playSound(SoundEvents.FIRECHARGE_USE);
                    }
                }
                return InteractionResultHolder.pass(itemstack);
            }else {
                if (level.isClientSide) player.displayClientMessage(Component.translatable("info.kaguya.mini_hakkero.no_damage"), true);
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }
}
