package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Effect.ModEffects;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 万宝锤
 */
public class WanbaoHammer extends KaguyaPrpo {
    public WanbaoHammer() {
        super(new Properties().durability(100));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.isCrouching()){
                player.addEffect(new MobEffectInstance(ModEffects.expand.get(), 20 * 60, 1));
                stack.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(hand));
            }else {
                player.addEffect(new MobEffectInstance(ModEffects.expand.get(), 20 * 90, 0));
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }

            player.getCooldowns().addCooldown(this, 20);
        }
        player.playSound(SoundEvents.PLAYER_LEVELUP);
        return super.use(level, player, hand);
    }
}
