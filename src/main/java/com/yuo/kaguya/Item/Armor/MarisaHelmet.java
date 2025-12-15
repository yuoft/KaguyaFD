package com.yuo.kaguya.Item.Armor;

import com.yuo.kaguya.Client.Model.MarisaHatModel;
import com.yuo.kaguya.Kaguya;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MarisaHelmet extends ArmorItem {
    public MarisaHelmet() {
        super(ModArmorMaterials.MARISA, Type.HELMET, new Properties().stacksTo(1).durability(648));
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.getDayTime() % 10 == 0) {
            AABB deflate = player.getBoundingBox().deflate(5);
            for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, deflate)) {
                entity.playerTouch(player);
            }
        }
    }

    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return Kaguya.MOD_ID + ":textures/models/marisa_hat.png";
    }

    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @NotNull MarisaHatModel getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemstack, EquipmentSlot armorSlot, HumanoidModel _deafult) {
                MarisaHatModel model = armorSlot == EquipmentSlot.LEGS ?
                        new MarisaHatModel(MarisaHatModel.createLayer(new CubeDeformation(1.0F), 0.0F, true).getRoot().bake(64, 32)) :
                        new MarisaHatModel(MarisaHatModel.createLayer(new CubeDeformation(0.0F), 0.0F, false).getRoot().bake(64, 32));
                return model;
            }
        });
    }
}
