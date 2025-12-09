package com.yuo.kaguya.Item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KaguyaMaterialItem extends Item {
    public KaguyaMaterialItem() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        Item item = stack.getItem();
        if (item == ModItems.extend.get()){
            components.add(Component.translatable("info.kaguya.extend"));
        }
    }
}
