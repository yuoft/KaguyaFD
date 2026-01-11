package com.yuo.kaguya.Entity;

import net.minecraft.util.RandomSource;

import java.util.Random;

public enum DanmakuColor {
    // 各种颜色
    RED(0xff0000, "red"),
    ORANGE(0xff7f00, "orange"),
    YELLOW(0xfeff00, "yellow"),
    LIME(0x7fff00, "lime"),
    LIGHT_GREEN(0x00ff00, "light_green"),
    GREEN(0x00ff7f, "green"),
    CYAN(0x1dfeff, "cyan"),
    LIGHT_BLUE(0x007fff, "light_blue"),
    BLUE(0x0000ff, "blue"),
    PURPLE(0x7f00ff, "purple"),
    MAGENTA(0xff00fe, "magenta"),
    PINK(0xff007f, "pink"),
    GRAY(0x8c8c8c, "gray");

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
        return random(RandomSource.create());
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
            return RED;
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
}
