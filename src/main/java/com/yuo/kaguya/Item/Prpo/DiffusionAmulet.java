package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Effect.ModEffects;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DiffusionAmulet extends KaguyaPrpo {
    public DiffusionAmulet() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild)
            stack.shrink(1);
        player.playSound(SoundEvents.GENERIC_DRINK);
        player.addEffect(new MobEffectInstance(ModEffects.diffusion.get(), 20 * 60, Mth.randomBetweenInclusive(level.random, 0, 4)));
        player.getCooldowns().addCooldown(this, 20);
        return super.use(level, player, hand);
    }
}
