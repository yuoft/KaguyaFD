package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GapFoldingUmbrella extends KaguyaPrpo {
    private static final String NBT_COLOR = "kaguya_gap_color";

    public GapFoldingUmbrella() {
        super(new Properties().durability(30));
    }

    @Override
    public ItemStack getDefaultInstance() {
        return createColoredStack(DyeColor.GRAY);
    }

    /**
     * 创建带有颜色的物品堆栈
     * @param color 颜色
     */
    public static ItemStack createColoredStack(DyeColor color) {
        ItemStack stack = new ItemStack(ModItems.gapFoldingUmbrella.get());
        setColor(stack, color);
        return stack;
    }

    /**
     * 设置颜色到NBT
     */
    public static void setColor(ItemStack stack, DyeColor color) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_COLOR, color.name());
        stack.setTag(tag);
    }

    /**
     * 从NBT获取颜色
     */
    public static DyeColor getColor(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBT_COLOR)) {
            String color = tag.getString(NBT_COLOR);
            return DyeColor.byName(color, DyeColor.GRAY);
        }
        return DyeColor.GRAY;
    }

    public static int getColor(ItemStack stack, int layer) {
        if (layer == 1) {
            return getColor(stack).getTextColor();
        }
        return -1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (stack.getOrCreateTag().contains(NBT_COLOR)) {
            DyeColor color = getColor(stack);
            tooltip.add(Component.translatable("info.kaguya.gap_folding_umbrella.color").append(Component.translatable("info.kaguya.color." + color.getName())));
        }
    }
}
