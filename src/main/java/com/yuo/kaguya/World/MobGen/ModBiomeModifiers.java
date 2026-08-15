package com.yuo.kaguya.World.MobGen;

import com.mojang.serialization.Codec;
import com.yuo.kaguya.Kaguya;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Kaguya.MOD_ID);

    public static final RegistryObject<Codec<? extends BiomeModifier>> HAKUREI_REIMU_SPAWN = BIOME_MODIFIER_SERIALIZERS.register("hakurei_reimu_spawn",
            () -> HakureiReimuSpawnModifier.CODEC);
    public static final RegistryObject<Codec<? extends BiomeModifier>> KIRISAME_MARISA_SPAWN = BIOME_MODIFIER_SERIALIZERS.register("kirisame_marisa_spawn",
            () -> KirisameMarisaSpawnModifier.CODEC);
    public static final RegistryObject<Codec<? extends BiomeModifier>> RUMIA_SPAWN = BIOME_MODIFIER_SERIALIZERS.register("rumia_spawn",
            () -> RumiaSpawnModifier.CODEC);
    public static final RegistryObject<Codec<? extends BiomeModifier>> CIRNO_SPAWN = BIOME_MODIFIER_SERIALIZERS.register("cirno_spawn",
            () -> CirnoSpawnModifier.CODEC);
    public static final RegistryObject<Codec<? extends BiomeModifier>> KOCHIYA_SANAE_SPAWN = BIOME_MODIFIER_SERIALIZERS.register("kochiya_sanae_spawn",
            () -> KochiyaSanaeSpawnModifier.CODEC);
    public static final RegistryObject<Codec<? extends BiomeModifier>> MORICHIKA_RINNOSUKE_SPAWN = BIOME_MODIFIER_SERIALIZERS.register("morichika_rinnosuke_spawn",
            () -> MorichikaRinnosukeSpawnModifier.CODEC);
}
