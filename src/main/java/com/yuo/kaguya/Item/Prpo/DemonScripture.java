package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.DemonScriptureEntity;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * 魔人经卷
 */
public class DemonScripture extends KaguyaPrpo {

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);

        if (!level.isClientSide) {
            DemonScriptureEntity entity = new DemonScriptureEntity(level, player);
            level.addFreshEntity(entity);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
