package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.world.item.Tiers;

public class Laevateinn extends KaguyaWeapon {
    public Laevateinn() {
        super(Tiers.DIAMOND, 0, new Properties().durability(95));
    }
}
