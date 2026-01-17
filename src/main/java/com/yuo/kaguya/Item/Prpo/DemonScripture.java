package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DemonScripture extends KaguyaPrpo {

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
            float chargeRatio = KaguyaUtils.getChargeRatio(getUseDuration(stack), i, 40);
            ItemStack itemByPlayer = KaguyaUtils.findItemByPlayer(player, Items.REDSTONE_BLOCK);
            if (chargeRatio > 0.1f){
                boolean hasExp = player.experienceLevel >= 5 || player.isCreative();
                boolean hasItem = !itemByPlayer.isEmpty() || player.isCreative();
                if (!hasExp){
                    player.displayClientMessage(Component.translatable("info.kaguya.mazin_kyoukan.error_exp"), true);
                    return;
                }
                if (!hasItem){
                    player.displayClientMessage(Component.translatable("info.kaguya.mazin_kyoukan.error_redstone"), true);
                    return;
                }
                player.giveExperienceLevels(-5);
                int time = (getUseDuration(stack) - i) * 5;
                if (!level.isClientSide){
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, time, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, time, 1));
                    MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, time, 0);
                    if (player.hasEffect(MobEffects.DAMAGE_RESISTANCE)){
                        player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                        player.addEffect(resistance);
                    }else player.addEffect(resistance);
                }
                if (!player.getAbilities().instabuild) {
                    itemByPlayer.shrink(1);
                }
                player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE);
                player.getCooldowns().addCooldown(this, 20);
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int i) {
        if (living instanceof Player player && !level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 0, 3));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 5, 0));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 0, 2));
        }
        super.onUseTick(level, living, stack, i);
    }
}
