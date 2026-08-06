package com.yuo.kaguya.World.MobGen;

import com.mojang.serialization.Codec;
import com.yuo.kaguya.Entity.ModEntityTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.Tags.Biomes;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class CirnoSpawnModifier implements BiomeModifier {

    // 你的模组中用于序列化的 Codec，稍后需要注册
    public static final Codec<CirnoSpawnModifier> CODEC = Codec.unit(CirnoSpawnModifier::new);

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // 只在特定阶段（Phase.ADD）添加生物生成，避免与其他修改冲突
        if (phase == Phase.ADD) {
            // 使用标签判断是否为寒冷群系
            if (biome.is(Biomes.IS_COLD)) {
                builder.getMobSpawnSettings().addSpawn(MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(ModEntityTypes.CIRNO.get(), 2, 0, 1));
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}