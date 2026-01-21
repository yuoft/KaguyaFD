package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.*;
import com.yuo.kaguya.Item.KaguyaWeapon;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class WindStick extends KaguyaWeapon {
    public WindStick() {
        super(Tiers.DIAMOND, -4, new Properties().durability(50));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            int exp = player.experienceLevel;
            float damage = (float) Math.floor((exp + 2) / 5f);
            DanmakuShootHelper.shootDanmakuWind(level, player, 0.25f, 0.1f, DanmakuColor.GRAY, damage);
        } else {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (living instanceof Player player)
            player.getCooldowns().addCooldown(this, 20);
        if (!level.isClientSide()) {
            level.playSound(null, living.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int i) {
        if (living instanceof Player player) {
            float chargeRatio = KaguyaUtils.getChargeRatio(this.getUseDuration(stack), i, 160);
            if (chargeRatio >= 1.0f) {
                releaseUsing(stack, level, player, i);
                player.stopUsingItem();
            }
            int num = (int) Math.ceil(chargeRatio * 10);
            StringBuilder sb = new StringBuilder("----------");
            for (int j = 0; j < num; j++) {
                sb.setCharAt(j, '+');
            }
            //简单进度提升
            player.displayClientMessage(Component.nullToEmpty(sb.toString()), true);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}
