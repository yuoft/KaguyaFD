package com.yuo.kaguya.Item.Weapon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class Onbashira extends SwordItem {
    public Onbashira() {
        super(Tiers.DIAMOND, 7, -2.8f, new Properties().durability(59));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean b) {
        super.inventoryTick(stack, level, entity, i, b);
        if (!level.isClientSide()) {
            if (entity instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));

                ItemStack handItem = player.getMainHandItem();
                if (handItem.getItem() instanceof Onbashira) {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 0));
                }
            }
        }
    }
}
