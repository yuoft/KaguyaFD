package com.yuo.kaguya.Entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 弹幕发射工具类
 * 各种类型弹幕的不同发射方式
 */
public class DanmakuShootHelper {
    private static final float VAL_DEF = 1.0f;
    private static final float INA_DEF = 1.0f;
    private static final DanmakuColor COLOR_DEF = DanmakuColor.GREEN;
    private static final DanmakuType TYPE_DEF = DanmakuType.PELLET;

    /**
     * 直线发射弹幕--圆形
     * @param living 发射实体
     */
    public static void shootDanmaku(Level level, LivingEntity living){
        shootDanmaku(level, living, VAL_DEF, INA_DEF, COLOR_DEF, TYPE_DEF);
    }

    /**
     * 直线发射弹幕--蝶形
     */
    public static void shootDanmakuButterfly(Level level, LivingEntity living){
        shootDanmakuButterfly(level, living, VAL_DEF, INA_DEF, COLOR_DEF);
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
        DanmakuBase shot = new DanmakuBase(level, living);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, vel, ina);
        shot.setDanmakuType(type);
        shot.setColor(color);
        addEntityAndSound(level, living, shot);
    }

    /**
     * 直线发射弹幕--蝶形
     * @param living 发射实体
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     */
    public static void shootDanmakuButterfly(Level level, LivingEntity living, float vel, float ina, DanmakuColor color){
        DanmakuButterfly shot = new DanmakuButterfly(level, living);
        shot.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, vel, ina);
        shot.setColor(color);
        addEntityAndSound(level, living, shot);
    }

    /**
     * 水平扇形发射弹幕-- 圆形
     * @param size 数量
     */
    public static void fanShapedShotDanmaku(Level level, LivingEntity living, int size){
        for(int i = 0; i < size; i++){
            fanShapedShotDanmakuOne(level, living, getFanShapedAngle(i, size,size % 2 == 0));
        }
    }

    /**
     * 水平扇形单次发射弹幕-- 圆形
     * @param angle 角度
     */
    public static void fanShapedShotDanmakuOne(Level level, LivingEntity living, float angle){
        DanmakuBase shot = new DanmakuBase(level, living);
        shot.setDanmakuType(TYPE_DEF);
        shot.setColor(COLOR_DEF);
        Vec3 upVector = living.getUpVector(1.0F);
        Quaternionf quaternion = (new Quaternionf()).setAngleAxis(angle * 0.017453292F, upVector.x, upVector.y, upVector.z);
        Vec3 vector3d = living.getViewVector(1.0F);
        Vector3f vec = vector3d.toVector3f().rotate(quaternion);
        shot.shoot(vec.x(), vec.y(), vec.z(), VAL_DEF, INA_DEF);
        addEntityAndSound(level, living, shot);
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
     * 获取水平扇形角度
     * @param i 序数
     * @param size 总数
     * @param flag 是否偏向左边
     * @return 角度
     */
    private static float getFanShapedAngle(int i, int size, boolean flag){
        return flag ?  -(45f - i * 4.5f): (i - size) * 4.5f;
    }
}
