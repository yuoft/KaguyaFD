package com.yuo.kaguya.Entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 弹幕发射工具类
 * 各种类型弹幕的不同发射方式
 */
public class DanmakuShootHelper {
    private static final float VAL_DEF = 1.0f; //默认发射初速度
    private static final float INA_DEF = .1f; //默认发射偏移量
    private static final float ZERO = .0f;
    private static final DanmakuColor COLOR_DEF = DanmakuColor.GREEN;
    private static final DanmakuType TYPE_DEF = DanmakuType.TINY_BALL;
    private static final DanmakuType FLY_DEF = DanmakuType.BUTTER_FLY;
    private static final int MAX_SIZE = 100; //单次发射的最大弹幕数量

    /**
     * 直线发射弹幕--圆形
     * @param living 发射实体
     */
    public static void shootDanmaku(Level level, LivingEntity living){
        shootDanmaku(level, living, VAL_DEF, INA_DEF, COLOR_DEF, TYPE_DEF);
    }

    /**
     * 直线发射弹幕--圆形
     * @param living 发射实体
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     * @param type 类型
     */
    public static void shootDanmaku(Level level, LivingEntity living, float vel, float ina, DanmakuColor color, DanmakuType type){
        DanmakuBase shot = new DanmakuBase(level, living, type, color);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), ZERO, vel, ina);
        addEntityAndSound(level, living, shot);
    }

    /**
     * 直线发射弹幕--蝶形
     * @param living 发射实体
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     */
    public static void shootDanmakuFly(Level level, LivingEntity living, float vel, float ina, DanmakuColor color){
        DanmakuButterfly shot = new DanmakuButterfly(level, living, color);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), ZERO, vel, ina);
        addEntityAndSound(level, living, shot);
    }

    /**
     * 直线发射弹幕--箭形
     * @param living 发射实体
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     */
    public static void shootDanmakuArrow(Level level, LivingEntity living, float vel, float ina, DanmakuColor color){
        DanmakuArrow shot = new DanmakuArrow(level, living, color);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), ZERO, vel, ina);
        addEntityAndSound(level, living, shot);
    }

    /**
     * 直线发射弹幕--旋风
     * @param living 发射实体
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     */
    public static void shootDanmakuWind(Level level, LivingEntity living, float vel, float ina, DanmakuColor color){
        WindEntity shot = new WindEntity(level, living, color);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), ZERO, vel, ina);
        addEntityAndSound(level, living, shot);
    }

    public static void shootDanmakuWind(Level level, LivingEntity living, float vel, float ina, DanmakuColor color, float damage){
        WindEntity shot = new WindEntity(level, living, color);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), ZERO, vel, ina);
        shot.setDamage(damage);
        addEntityAndSound(level, living, shot);
    }

    /**
     *
     * 直线发射弹幕--激光
     * @param living 发射实体
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     */
    public static void shootDanmakuLaser(Level level, LivingEntity living, float vel, float ina, DanmakuType type, DanmakuColor color){
        DanmakuLaser shot = new DanmakuLaser(level, living, type, color);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), ZERO, vel * 2, ina);
        addEntityAndSound(level, living, shot);
    }

    /**
     * 水平扇形发射弹幕-- 圆形
     * @param size 数量
     */
    public static void fanShapedShotDanmaku(Level level, LivingEntity living, int size, DanmakuType type, DanmakuColor color){
        for(int i = 0; i < size; i++){
            DanmakuBase shot = new DanmakuBase(level, living, type, color);
            fanShapedShotDanmakuOne(level, living, shot, getFanShapedAngle(i, size));
        }
    }

    public static void fanShapedShotDanmakuFly(Level level, LivingEntity living, int size, DanmakuColor color){
        for(int i = 0; i < size; i++){
            DanmakuButterfly shot = new DanmakuButterfly(level, living, color);
            fanShapedShotDanmakuOne(level, living, shot, getFanShapedAngle(i, size));
        }
    }

    /**
     * 水平扇形单次发射弹幕-- 圆形
     * @param angle 角度
     */
    public static void fanShapedShotDanmakuOne(Level level, LivingEntity living, DanmakuBase danmaku, float angle){
        Vec3 upVector = living.getUpVector(1.0F);
        Quaternionf quaternion = (new Quaternionf()).setAngleAxis(angle * 0.017453292F, upVector.x, upVector.y, upVector.z);
        Vec3 vector3d = living.getViewVector(1.0F);
        Vector3f vec = vector3d.toVector3f().rotate(quaternion);
        danmaku.shoot(vec.x(), vec.y(), vec.z(), 0.25f, INA_DEF);
        addEntityAndSound(level, living, danmaku);
    }

    /**
     * 添加实体和音效
     * @param shot 要发射的实体
     */
    private static void addEntityAndSound(Level level, LivingEntity living, DanmakuBase shot){
        level.addFreshEntity(shot);
        level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.SNOWBALL_THROW, living.getSoundSource(), 1.0f, 0.8f);
    }

    /**
     * 获取扇形角度 120°角
     * @param i 序数 0:0 1:-1 2:1 3:-2 4:2 偶数则去0项
     * @param size 总数
     * @return 角度
     */
    private static float getFanShapedAngle(int i, int size){
        int m = i % 2 == 0 ? i / 2  + (size % 2 == 0 ? 1 : 0) : -(i / 2 + 1);
        return (60f / size) * m;
    }
}
