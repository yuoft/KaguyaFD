package com.yuo.kaguya.Item.Armor;

import com.yuo.kaguya.Client.Model.MarisaHatModel;
import com.yuo.kaguya.Client.Model.SuwakoHatModel;
import com.yuo.kaguya.Kaguya;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SuwakoHelmet extends ArmorItem {
    public SuwakoHelmet() {
        super(ModArmorMaterials.SUWAKO, Type.HELMET, new Properties().stacksTo(1).durability(456));
    }

    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return Kaguya.MOD_ID + ":textures/models/suwako_hat.png";
    }

    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @NotNull SuwakoHatModel getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemstack, EquipmentSlot armorSlot, HumanoidModel _deafult) {
                SuwakoHatModel model = armorSlot == EquipmentSlot.LEGS ?
                        new SuwakoHatModel(SuwakoHatModel.createLayer(new CubeDeformation(1.0F), 0.0F, true).getRoot().bake(64, 32)) :
                        new SuwakoHatModel(SuwakoHatModel.createLayer(new CubeDeformation(0.0F), 0.0F, false).getRoot().bake(64, 32));
                return model;
            }
        });
    }
}
