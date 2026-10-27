package com.yuo.kaguya.Data;

import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import com.yuo.kaguya.Kaguya;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.Tags.Biomes;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModWorldGenProvider::bootstrap)
            .add(Registries.DAMAGE_TYPE, DanmakuDamageTypes::bootstrap);

    public ModWorldGenProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries, BUILDER, Set.of("minecraft", Kaguya.MOD_ID));
    }

    private static void bootstrap(BootstapContext<BiomeModifier> context) {
        // 获取 Biomes 的 HolderGetter
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);

        // ── 灵梦：山地群系 ──
        HolderSet<Biome> mountainBiomes = biomeGetter.getOrThrow(BiomeTags.IS_MOUNTAIN);
        context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, KaguyaUtils.fa("hakurei_reimu_spawn")),
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(mountainBiomes,
                        List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.HAKUREI_REIMU.get(), 2, 1, 1))));

        // ── 魔理沙：森林群系 ──
        HolderSet<Biome> forestBiomes = biomeGetter.getOrThrow(BiomeTags.IS_FOREST);
        context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, KaguyaUtils.fa("kirisame_marisa_spawn")),
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(forestBiomes,
                        List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.KIRISAME_MARISA.get(), 2, 1, 1))));

        // ── 露米娅：下界群系 ──
        HolderSet<Biome> netherBiomes = biomeGetter.getOrThrow(BiomeTags.IS_NETHER);
        context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, KaguyaUtils.fa("rumia_spawn")),
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(netherBiomes,
                        List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.RUMIA.get(), 2, 1, 1))));

        // ── 琪露诺：寒冷群系（使用 Forge 的 IS_COLD 标签）──
        HolderSet<Biome> coldBiomes = biomeGetter.getOrThrow(Biomes.IS_COLD);
        context.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, KaguyaUtils.fa("cirno_spawn")),
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(coldBiomes,
                        List.of(new MobSpawnSettings.SpawnerData(ModEntityTypes.CIRNO.get(), 2, 1, 1))));
    }
}