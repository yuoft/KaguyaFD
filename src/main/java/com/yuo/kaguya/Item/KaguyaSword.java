package com.yuo.kaguya.Item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class KaguyaSword extends SwordItem {
    public KaguyaSword(Tier tier) {
        super(tier, -4, -2.4f, new Properties().stacksTo(1));
    }

    public KaguyaSword(Tier tier, Properties properties) {
        super(tier, -4, -2.4f, properties);
    }

    public KaguyaSword(Tier tier, int damage, Properties properties) {
        super(tier, damage, -2.4f, properties);
    }

}
