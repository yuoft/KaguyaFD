package com.yuo.kaguya.Item.Armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.yuo.kaguya.Kaguya;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class KappaHelmet extends ArmorItem {
    private static final UUID SWIM_SPEED_UUID = UUID.fromString("21d46f21-1076-10c4-ebd7-b5026c206d87");

    public KappaHelmet() {
        super(ArmorMaterials.LEATHER, Type.HELMET, new Properties().stacksTo(1).durability(168));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = this.getDefaultAttributeModifiers(slot);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(multimap);
        if (slot == EquipmentSlot.HEAD) {
            builder.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(SWIM_SPEED_UUID, Kaguya.MOD_ID + ":kappa", 0.5, Operation.MULTIPLY_BASE));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isRaining() && stack.getDamageValue() > 0 && level.getDayTime() % 20 == 0){
            if (player.isInWaterOrRain())
                stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }

        if (player.isEyeInFluid(FluidTags.WATER) && level.getDayTime() % 20 == 0) {
            player.setAirSupply(Math.min(player.getAirSupply() + 10, 300));
        }
    }
}
