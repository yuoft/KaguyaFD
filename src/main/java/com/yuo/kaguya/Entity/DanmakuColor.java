package com.yuo.kaguya.Entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;

public enum DanmakuColor {
    // 各种颜色
    WHITE(0xffffff, "white"),
    ORANGE(0xff7f00, "orange"), //橙色
    MAGENTA(0xff00fe, "magenta"), //品红色
    LIGHT_BLUE(0x007fff, "light_blue"), //淡蓝色
    YELLOW(0xfeff00, "yellow"),
    LIME(0x7fff00, "lime"), //淡绿色
    PINK(0xff007f, "pink"), //粉色
    GRAY(0x8c8c8c, "gray"), //灰色
    LIGHT_GRAY(0xd3c9d3, "light_gray"), //淡灰色
    CYAN(0x1dfeff, "cyan"), //青绿色
    PURPLE(0x7f00ff, "purple"), //紫色
    BLUE(0x0000ff, "blue"),
    BROWN(0x8b5d13, "brown"), //棕色
    GREEN(0x00ff7f, "green"),
    RED(0xff0000, "red"),
    BLACK(0x000000, "black");
//    LIGHT_GREEN(0x00ff00, "light_green");
//    GOLD(0xffca18, "gold");
//    DARK_RED(0x88001b, "dark_red");
//    LIGHT_YELLOW(0xfdeca6, "light_yellow");
//    LIGHT_GREEN1(0xc4ff0e, "light_green1");
//    WATER_GREEN(0x8cfffb, "water_green");
//    LIGHT_GREEN(0x00a8f3, "light_green");
//    LIGHT_GREEN(0x00ff00, "light_green");
//    LIGHT_GREEN(0x00ff00, "light_green");

    private final String name;
    private final int rgb;
    private final int red;
    private final int green;
    private final int blue;
    private final float floatRed;
    private final float floatGreen;
    private final float floatBlue;

    DanmakuColor(int color, String name) {
        this.name = name;
        this.rgb = color;
        this.red = color >> 16 & 255;
        this.green = color >> 8 & 255;
        this.blue = color & 255;
        this.floatRed = this.red / 255f;
        this.floatGreen = this.green / 255f;
        this.floatBlue = this.blue / 255f;
    }

    public String getName() {
        return name;
    }

    public static DanmakuColor getColor(String colorName) {
        for (DanmakuColor color : DanmakuColor.values()) {
            if (color.getName().equals(colorName)) {
                return color;
            }
        }
        return null;
    }

    public static DanmakuColor getColor(String colorName, boolean flag) {
        for (DanmakuColor color : DanmakuColor.values()) {
            if (color.getName().equals(colorName)) {
                return color;
            }
        }
        return flag ? null : random(RandomSource.create());
    }

    public static DanmakuColor getColor(int index) {
        if (index < 0 || index >= values().length) {
            return WHITE;
        }
        return values()[index];
    }

    public static DanmakuColor random(RandomSource random) {
        return getColor(random.nextInt(getLength()));
    }

    public static int getLength() {
        return values().length;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public float getFloatRed() {
        return floatRed;
    }

    public float getFloatGreen() {
        return floatGreen;
    }

    public float getFloatBlue() {
        return floatBlue;
    }

    public float[] getFloatRgb(){
        float[] rgb = new float[4];
        rgb[0] = this.floatRed;
        rgb[1] = this.floatGreen;
        rgb[2] = this.floatBlue;
        rgb[3] = 1f;
        return rgb;
    }

    public int getRgb() {
        return rgb;
    }

    /**
     * 从染料物品获取弹幕颜色
     */
    public static DanmakuColor getColorFromDye(DyeItem dyeItem) {
        DyeColor dyeColor = dyeItem.getDyeColor();
        return DanmakuColor.getColor(dyeColor.getName());
    }

    /**
     * 从弹幕颜色获取染料物品
     */
    public static DyeItem getDyeItemFromColor(DanmakuColor danmakuColor) {
        return DyeItem.byColor(DyeColor.byName(danmakuColor.getName(), DyeColor.CYAN));
    }
}
