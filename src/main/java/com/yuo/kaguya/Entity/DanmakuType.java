package com.yuo.kaguya.Entity;

import java.util.Random;

public enum DanmakuType {
    // 点圆弹,
    TINY_BALL(0.6d, 0,0.5f,"tiny_ball"),
    // 小圆弹
    SMALL_BALL(0.3d, 1,1.0f,"small_ball"),
    // 中圆弹
    MEDIUM_BALL(0.5d, 1,1.5f,"medium_ball"),
    // 大圆弹
    BIG_BALL(0.5d, 3,2.0f,"big_ball"),
    // 环圆弹
    RING_BALL(0.6d, 2,2.5f,"ring_ball"),
    // 蝴蝶
    BUTTER_FLY(0.6d, -1,5f,"button_fly"),
//    SILVER_KNIFE(0.6d, -1,2.5f,"silver_knife"),
    ;

    private final double size;
    private final int resId; //纹理位置序数 -1--单独渲染
    private final float damage;
    private final String name;

    /**
     * 弹幕类型枚举
     *
     * @param size 弹幕渲染放大缩小倍数
     */
    DanmakuType(double size,int resId, float damage, String name) {
        this.size = size;
        this.resId = resId;
        this.damage = damage;
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

    public int getResId() {
        return resId;
    }

    public float getDamage() {
        return damage;
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
