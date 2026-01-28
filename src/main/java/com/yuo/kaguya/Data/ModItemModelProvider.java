package com.yuo.kaguya.Data;

import com.yuo.kaguya.Item.DanmakuShotItem;
import com.yuo.kaguya.Item.KaguyaMaterialItem;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.ModNoDataGenItem;
import com.yuo.kaguya.Item.Prpo.StoneBowl;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof ModNoDataGenItem || item instanceof BlockItem){
                continue;
            }

            if (item instanceof DanmakuShotItem){
                if (item == ModItems.longLaser.get() || item == ModItems.middleLaser.get() || item == ModItems.shortLaser.get()){
                    this.laserItemModel(getRes(item));
                }else this.shotItemModel(getRes(item));
            }else if (item instanceof TieredItem || item instanceof ArmorItem){
                if (item instanceof StoneBowl) this.basicItem(item);
                else this.weaponItemModel(getRes(item));
            }else if (item instanceof KaguyaMaterialItem){
                this.materialItemModel(getRes(item));
            }else this.basicItem(item);
        }

    }

    public ResourceLocation getRes(Item item){
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
    }

    public void shotItemModel(ResourceLocation item){
        this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", KaguyaUtils.fa("item/shot/" + item.getPath()))
                .texture("layer1", KaguyaUtils.fa("item/shot/" + item.getPath() + "_2"));
    }

    public void laserItemModel(ResourceLocation item){
        this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", KaguyaUtils.fa("item/shot/" + item.getPath()));
    }

    public void handItemModel(ResourceLocation item){
        this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", KaguyaUtils.fa("item/" + item.getPath()));
    }

    public void weaponItemModel(ResourceLocation item){
        this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", KaguyaUtils.fa("item/weapon/" + item.getPath()));
    }

    public void materialItemModel(ResourceLocation item){
        this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", KaguyaUtils.fa("item/material/" + item.getPath()));
    }
}
