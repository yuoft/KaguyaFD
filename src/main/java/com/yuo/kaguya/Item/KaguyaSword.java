package com.yuo.kaguya.Item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class KaguyaSword extends SwordItem {
    public KaguyaSword(Tier tier) {
        super(tier, 0, -2.4f, new Properties().stacksTo(1));
    }
}
