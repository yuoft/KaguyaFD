package com.yuo.kaguya.Item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

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
}
