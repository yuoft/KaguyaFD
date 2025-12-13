package com.yuo.kaguya.Item.Armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;

public class SuwakoHelmet extends ArmorItem {
    public SuwakoHelmet() {
        super(ArmorMaterials.DIAMOND, Type.HELMET, new Properties().stacksTo(1).durability(456));
    }
}
