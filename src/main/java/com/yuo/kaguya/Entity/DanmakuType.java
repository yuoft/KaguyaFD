package com.yuo.kaguya.Entity;

import java.util.Random;

public enum DanmakuType {
    // 点圆弹,
    TINY_BALL(0.6d, 0,"tiny_ball"),
    // 小圆弹
    SMALL_BALL(0.3d, 1,"small_ball"),
    // 中圆弹
    MEDIUM_BALL(0.5d, 1,"medium_ball"),
    // 大圆弹
    BIG_BALL(0.5d, 3,"big_ball"),
    // 环圆弹
    RING_BALL(0.6d, 2,"ring_ball"),
    // 蝴蝶
    BUTTER_FLY(0.6d, 0,"button_fly"),;

    private final double size;
    private final int id; //纹理位置序数
    private final String name;

    /**
     * 弹幕类型枚举
     *
     * @param size 弹幕渲染放大缩小倍数
     */
    DanmakuType(double size,int id, String name) {
        this.size = size;
        this.id = id;
        this.name = name;
    }

    public static DanmakuType getType(int index) {
        if (index < 0 || index >= values().length) {
            return TINY_BALL;
        }
        return values()[index];
    }

    public static DanmakuType random(Random random) {
        return getType(random.nextInt(getLength()));
    }

    public static int getLength() {
        return values().length;
    }

    public double getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static DanmakuType getType(String name) {
        for (DanmakuType type : DanmakuType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return random(new Random());
    }
}
