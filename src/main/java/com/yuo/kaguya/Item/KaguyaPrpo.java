package com.yuo.kaguya.Item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KaguyaPrpo extends Item {
    public KaguyaPrpo() {
        super(new Properties().stacksTo(1));
    }

    public KaguyaPrpo(Properties properties) {
        super(properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        Item item = stack.getItem();
        if (item == ModItems.sukimaGap.get()){
            components.add(Component.translatable("text.kaguya.sukima_gap.1"));
        }
        if (item == ModItems.thirdEye.get()){
            components.add(Component.translatable("text.kaguya.third_eye.1"));
        }
        if (item == ModItems.kabenuke.get()){
            components.add(Component.translatable("text.kaguya.kabenuke.1"));
        }
        if (item == ModItems.hotokeHachi.get()){
            components.add(Component.translatable("text.kaguya.hotoke_hachi.1"));
        }
        if (item == ModItems.mazinKyoukan.get()){
            components.add(Component.translatable("text.kaguya.mazin_kyoukan.1"));
        }
    }
}
