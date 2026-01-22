package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GapFoldingUmbrella extends KaguyaPrpo {

    public GapFoldingUmbrella() {
        super(new Properties().durability(30));
    }

    public static int getColor(ItemStack stack, int layer) {
        if (SukimaGap.hasColor(stack) && layer == 1){
            return SukimaGap.getColor(stack).getTextColor();
        }
        return -1;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (SukimaGap.hasColor(stack)) {
            DyeColor color = SukimaGap.getColor(stack);
            if (color != null) {
                MutableComponent colorName = Component.translatable("info.kaguya.color." + color.getName()).append("-");
                return colorName.append(super.getName(stack));
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (SukimaGap.hasColor(stack)) {
            DyeColor color = SukimaGap.getColor(stack);
            tooltip.add(Component.translatable("info.kaguya.gap.color").append(Component.translatable("info.kaguya.color." + color.getName())));
        }
    }
}
