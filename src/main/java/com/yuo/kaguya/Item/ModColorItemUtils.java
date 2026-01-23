package com.yuo.kaguya.Item;

import com.yuo.kaguya.Entity.DanmakuColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ModColorItemUtils {
    public static final String NBT_COLOR = "kaguya:gap_color";

    /**
     * 创建带有颜色的物品堆栈
     * @param color 颜色
     */
    public static ItemStack createColoredStack(Item item, DanmakuColor color) {
        ItemStack stack = new ItemStack(item);
        setColor(stack, color);
        return stack;
    }

    /**
     * 设置颜色到NBT
     */
    public static void setColor(ItemStack stack, DanmakuColor color) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_COLOR, color.name());
        stack.setTag(tag);
    }

    /**
     * 从NBT获取颜色
     */
    public static DanmakuColor getColor(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBT_COLOR)) {
            String color = tag.getString(NBT_COLOR);
            return DanmakuColor.getColor(color);
        }

        return null;
    }

    /**
     * 染色颜色获取
     * @param layer 纹理层
     * @return 颜色代码
     */
    public static int getColor(ItemStack stack, int layer) {
        if (hasColor(stack) && layer == 1){
            DanmakuColor color = getColor(stack);
            if (color != null){
                return color.getRgb();
            }
        }
        return -1;
    }

    /**
     * 是否有颜色tag
     */
    public static boolean hasColor(ItemStack stack) {
        return stack.getOrCreateTag().contains(NBT_COLOR);
    }

    /**
     * 颜色名称
     * @param component 原名称
     */
    public static Component getColorName(ItemStack stack, Component component) {
        if (ModColorItemUtils.hasColor(stack)) {
            DanmakuColor color = ModColorItemUtils.getColor(stack);
            if (color != null) {
                MutableComponent colorName = Component.translatable("info.kaguya.color." + color.getName()).append("-");
                return colorName.append(component);
            }
        }
        return component;
    }

    /**
     * 颜色文本
     */
    public static void appendColorText(ItemStack stack, List<Component> tooltip){
        if (ModColorItemUtils.hasColor(stack)) {
            DanmakuColor color = ModColorItemUtils.getColor(stack);
            tooltip.add(Component.translatable("info.kaguya.gap.color").append(Component.translatable("info.kaguya.color." + color.getName())));
        }
    }
}
