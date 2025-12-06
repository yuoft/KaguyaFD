package com.yuo.kaguya.Data;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
            this.add(entry.get(), "lang");
        }

    }
}
