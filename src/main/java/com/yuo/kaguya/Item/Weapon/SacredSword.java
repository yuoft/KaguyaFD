package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SacredSword extends KaguyaWeapon {
    public SacredSword() {
        super(Tiers.DIAMOND, 3, new Properties().durability(250));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player.isCrouching()) {
            ItemStack stack = player.getItemInHand(hand);
            AABB deflate = player.getBoundingBox().deflate(10);
            for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, deflate)) {
                living.setGlowingTag(true);
                living.setCustomName(living.getDisplayName());
            }

            level.playSound((Player) null, player.getOnPos(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            stack.hurtAndBreak(5, player, (p) -> p.broadcastBreakEvent(hand));
            player.getCooldowns().addCooldown(this, 20);
        }
        return super.use(level, player, hand);
    }
}
