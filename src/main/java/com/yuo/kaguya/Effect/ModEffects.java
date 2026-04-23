package com.yuo.kaguya.Effect;

import com.yuo.kaguya.Kaguya;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Kaguya.MOD_ID);

    public static final Supplier<MobEffect> diffusion = EFFECTS.register("diffusion", DiffusionEffect::new);
    public static final Supplier<MobEffect> homing = EFFECTS.register("homing", HomingEffect::new);
}
