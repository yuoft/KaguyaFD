package com.yuo.kaguya.Item.Armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FireRatRobe extends ArmorItem {
    public FireRatRobe() {
        super(ArmorMaterials.IRON, Type.CHESTPLATE, new Properties().stacksTo(1).durability(232).fireResistant());
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!player.isCreative() && player.getRemainingFireTicks() > 0) {
            player.extinguishFire();
            stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.HEAD));
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity living, LivingEntity player) {
        living.setSecondsOnFire(10);
        return super.hurtEnemy(stack, living, player);
    }
}
