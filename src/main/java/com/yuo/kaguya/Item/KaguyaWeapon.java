package com.yuo.kaguya.Item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KaguyaWeapon extends SwordItem {
    public KaguyaWeapon(Tier tier) {
        super(tier, -4, -2.4f, new Properties().stacksTo(1));
    }

    public KaguyaWeapon(Tier tier, int damage) {
        super(tier, damage, -2.4f, new Properties().stacksTo(1));
    }

    public KaguyaWeapon(Tier tier, int damage, Properties properties) {
        super(tier, damage, -2.4f, properties);
    }

    public KaguyaWeapon(Tier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        Item item = stack.getItem();
        if (item == ModItems.deathScythe.get()){
            components.add(Component.translatable("text.kaguya.death_scythe.1"));
        }
        if (item == ModItems.hisouSword.get()){
            components.add(Component.translatable("text.kaguya.hisou_sword.1"));
        }
        if (item == ModItems.yuyukoOugi.get()){
            components.add(Component.translatable("text.kaguya.yuyuko_ougi.1"));
        }
    }
}
