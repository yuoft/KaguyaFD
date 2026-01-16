package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
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
            if (chargeRatio > 0.1f && player.experienceLevel >= 5 && !itemByPlayer.isEmpty()){
                player.giveExperienceLevels(-5);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, i * 5));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1, i * 5));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 1, i * 5));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1, i * 5));
                itemByPlayer.shrink(1);
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int i) {
        if (living instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3, 0));
        }
        super.onUseTick(level, living, stack, i);
    }
}
