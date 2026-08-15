package com.yuo.kaguya.World.MobGen;

import com.mojang.serialization.Codec;
import com.yuo.kaguya.Entity.ModEntityTypes;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.Tags.Biomes;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class RumiaSpawnModifier implements BiomeModifier {

    public static final Codec<RumiaSpawnModifier> CODEC = Codec.unit(RumiaSpawnModifier::new);

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            if (biome.is(BiomeTags.IS_NETHER)) {
                builder.getMobSpawnSettings().addSpawn(MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(ModEntityTypes.RUMIA.get(), 2, 0, 1));
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}