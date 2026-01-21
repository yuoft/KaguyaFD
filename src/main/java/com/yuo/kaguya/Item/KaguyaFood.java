package com.yuo.kaguya.Item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KaguyaFood extends Item {
    public KaguyaFood(FoodProperties foodProperties) {
        super(new Properties().stacksTo(64).food(foodProperties));
    }

    public static FoodProperties heavenlyPeach = new FoodProperties.Builder().nutrition(10).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 15, 2), 1.0f).build();
    public static FoodProperties ibukihyou = new FoodProperties.Builder().nutrition(1).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 10, 0), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 20 * 10, 0), 1.0f).build();
    public static FoodProperties koyasugai = new FoodProperties.Builder().nutrition(10).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 1), 1.0f).build();

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        Item item = stack.getItem();
        if (item == ModItems.koyasugai.get()){
            components.add(Component.translatable("text.kaguya.koyasugai.1"));
        }
    }
}
