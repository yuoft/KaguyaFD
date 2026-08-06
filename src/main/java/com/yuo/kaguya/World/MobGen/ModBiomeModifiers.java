package com.yuo.kaguya.World.MobGen;

import com.mojang.serialization.Codec;
import com.yuo.kaguya.Kaguya;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Kaguya.MOD_ID);

    public static final RegistryObject<Codec<? extends BiomeModifier>> CIRNO_SPAWN_MODIFIER =
            BIOME_MODIFIER_SERIALIZERS.register("cirno_spawn", () -> CirnoSpawnModifier.CODEC);
}
