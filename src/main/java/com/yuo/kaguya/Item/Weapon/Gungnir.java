package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.world.item.Tiers;

public class Gungnir extends KaguyaWeapon {
    public Gungnir() {
        super(Tiers.NETHERITE, 0, new Properties().durability(95));
    }

}
