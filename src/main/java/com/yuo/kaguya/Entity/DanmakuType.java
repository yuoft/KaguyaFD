package com.yuo.kaguya.Entity;

import net.minecraft.util.RandomSource;

public enum DanmakuType {

    TINY_BALL(0.2d, 0.5f, false, "tiny_ball"), // 点圆弹,
    SMALL_BALL(0.3d, 1.0f,false, "small_ball"), // 小圆弹
    MEDIUM_BALL(0.4d, 1.5f,false, "medium_ball"), // 中圆弹
    LIGHT_MEDIUM_BALL(0.4d, 1.5f,false, "light_medium_ball"), // 中光弹
    BIG_BALL(0.6d, 2.0f,false, "big_ball"), // 大圆弹
    LIGHT_BIG_BALL(0.6d, 2.0f,false, "light_big_ball"), // 大光弹
    RING_BALL(0.4d, 2.0f,false, "ring_ball"), // 环圆弹
    CRYSTAL(0.2d, 1.0f,false, "crystal"), // 结晶弹
    SCALE(0.4d, 1.75f,false, "scale"), // 鳞弹
    STAR(0.3d, 1.5f,false, "star"), // 星弹
    SMALL_STAR(0.2d, 1.0f,false, "small_star"), // 小星弹
    RICE(0.2d, 0.5f,false, "rice"), // 粒弹
    OVAL(0.4d, 2.0f,false, "oval"), // 椭圆弹
    TALISMAN(0.3d, 2.5f,false, "talisman"), // 牌弹
    KUMAI(0.4d, 2.0f,false, "kunai"), // 苦无弹
    HEART(0.5d, 2.0f,false, "heart"), // 心弹
    BUTTER_FLY(0.25d, 5f,true, "button_fly"), // 蝶弹
    ARROW_SHOT(0.2d, 2.5f,true, "arrow_shot"), // 箭弹
    BEAM_LASER(0.25, 5f,true, "beam_laser"), // 光束激光
    LONG_LASER(3, 4.5f,true, "long_laser"), // 长激光
    MIDDLE_LASER(2, 3f,true, "middle_laser"), // 激光
    SHORT_LASER(1, 1.5f,true, "short_laser"), // 短激光
    WIND(0.25d, 3f,true, "wind"), // 风弹
    YINYANG_ORB(0.5, 3f,true, "yinyang_orb"), // 阴阳玉
    ;

    private final double size; //弹幕尺寸/碰撞箱 或 其他用途
    private final float damage; //基础伤害
    private final boolean isAloneRender; //是否单独渲染 非通用弹幕
    private final String name;

    /**
     * 弹幕类型枚举
     *
     * @param size 弹幕渲染放大缩小倍数
     */
    DanmakuType(double size, float damage, boolean isAloneRender, String name) {
        this.size = size;
        this.damage = damage;
        this.isAloneRender = isAloneRender;
        this.name = name;
    }

    public static DanmakuType getType(int index) {
        if (index < 0 || index >= values().length) {
            return SMALL_BALL;
        }
        return values()[index];
    }

    /**
     * 随机普通弹幕
     */
    public static DanmakuType random(RandomSource random) {
        DanmakuType type = getType(random.nextInt(getLength()));
        return type.isAloneRender ? SMALL_BALL : type;
    }

    public static int getLength() {
        return values().length;
    }

    public double getSize() {
        return size;
    }
    public int getIntSize() {
        return (int) size;
    }

    public String getName() {
        return name;
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
        return SMALL_BALL;
    }
}
