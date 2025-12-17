package com.yuo.kaguya.Data;

import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import com.yuo.kaguya.Kaguya;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Kaguya.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEvent {

    @SubscribeEvent
    public static void addLoot(GatherDataEvent event){
        boolean b = event.includeServer();
        boolean b0 = event.includeClient();
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        generator.addProvider(b, new ModDataRecipes(output));
//        generator.addProvider(true, new ModLanguageProvider(output, Kaguya.MOD_ID, "en_us"));
        generator.addProvider(b0, new ModItemModelProvider(output, Kaguya.MOD_ID, fileHelper));
        generator.addProvider(b0, new ModBlockStateProvider(output, Kaguya.MOD_ID, fileHelper));
        CompletableFuture<HolderLookup.Provider> future = event.getLookupProvider();
        generator.addProvider(b, new DamageTypeInit(generator.getPackOutput(), future));
    }

    public static class DamageTypeInit extends DatapackBuiltinEntriesProvider {

        public DamageTypeInit(PackOutput output, CompletableFuture<Provider> future) {
            super(output, future, DanmakuDamageTypes.DAMAGE_BUILDER, Set.of("minecraft", Kaguya.MOD_ID));
        }
    }
}
