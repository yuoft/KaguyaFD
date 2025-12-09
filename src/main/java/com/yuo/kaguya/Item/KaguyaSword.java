package com.yuo.kaguya.Item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class KaguyaSword extends SwordItem {
    public KaguyaSword(Tier tier) {
        super(tier, -4, -2.4f, new Properties().stacksTo(1));
    }

}
