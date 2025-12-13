package com.yuo.kaguya.Item.Armor;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class MarisaHelmet extends ArmorItem {
    public MarisaHelmet() {
        super(ArmorMaterials.NETHERITE, Type.HELMET, new Properties().stacksTo(1).durability(648));
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.getDayTime() % 10 == 0) {
            AABB deflate = player.getBoundingBox().deflate(5);
            for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, deflate)) {
                entity.playerTouch(player);
            }
        }
    }
}
