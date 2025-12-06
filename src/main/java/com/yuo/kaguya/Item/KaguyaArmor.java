package com.yuo.kaguya.Item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class KaguyaArmor extends ArmorItem {
    public KaguyaArmor(ArmorMaterial material, Type type) {
        super(material, type, new Properties().stacksTo(1));
    }
}
