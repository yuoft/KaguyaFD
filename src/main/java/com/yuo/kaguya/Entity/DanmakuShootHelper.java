package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

/**
 * 弹幕发射工具类
 * 各种类型弹幕的不同发射方式
 */
public class DanmakuShootHelper {
    public static final float VAL_DEF = 1.0f; //默认发射初速度
    public static final float INA_DEF = .1f; //默认发射偏移量
    public static final float ZERO = .0f;
    public static final DanmakuColor COLOR_DEF = DanmakuColor.GREEN;
    public static final DanmakuType TYPE_DEF = DanmakuType.TINY_BALL;
    public static final DanmakuType FLY_DEF = DanmakuType.BUTTER_FLY;
    public static final int MAX_SIZE = 100; //单次发射的最大弹幕数量

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

    /**
     * 直线发射弹幕--旋风
     * @param living 发射实体
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     * @param damage 攻击伤害
     */
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
     * 随机发射单独渲染弹幕
     * @param level 世界
     * @param living 发射实体
     */
    public static void shootRandomDanmaku(Level level, LivingEntity living){
        RandomSource random = level.getRandom();
        int i = Mth.randomBetweenInclusive(random, 0, 3);
        DanmakuColor danmakuColor = DanmakuColor.random(random);
        if (i == 0) {
            shootDanmakuFly(level, living, VAL_DEF / 4, INA_DEF, danmakuColor);
        }else if (i == 1) {
            shootDanmakuArrow(level, living, VAL_DEF, INA_DEF, danmakuColor);
        }else if (i == 2) {
            shootDanmakuWind(level, living, VAL_DEF / 2, INA_DEF, danmakuColor);
        }else {
            shootDanmakuLaser(level,living, VAL_DEF, INA_DEF, getRandomLaserType(random), danmakuColor);
        }
    }

    /**
     * 随机发射扇形单独渲染弹幕
     */
    public static void shootRandomFanShapeDanmaku(Level level, LivingEntity living){
        RandomSource random = level.getRandom();
        int i = Mth.randomBetweenInclusive(random, 0, 3);
        DanmakuColor danmakuColor = DanmakuColor.random(random);
        int size = 11;

        if (i == 0) {
            shootFanShapedDanmakuFly(level, living, size, danmakuColor);
        }else if (i == 1) {
            shootFanShapedDanmakuArrow(level, living, size, danmakuColor);
        }else if (i == 2) {
            shootFanShapedDanmakuWind(level, living, size, danmakuColor);
        }else {
            shootFanShapedDanmakuLaser(level, living, size, getRandomLaserType(random), danmakuColor);
        }
    }

    /**
     * 获取随机激光类型
     */
    private static DanmakuType getRandomLaserType(RandomSource random) {
        int typeId = Mth.randomBetweenInclusive(random, 0, 2);
        DanmakuType type;
        if (typeId == 0) {
            type = DanmakuType.SHORT_LASER;
        }else if (typeId == 1) {
            type = DanmakuType.MIDDLE_LASER;
        }else {
            type = DanmakuType.LONG_LASER;
        }
        return type;
    }

    /**
     * 水平扇形发射弹幕-- 圆形
     * @param size 数量
     */
    public static void shootFanShapedDanmaku(Level level, LivingEntity living, int size, DanmakuType type, DanmakuColor color){
        for(int i = 0; i < size; i++){
            DanmakuBase shot = new DanmakuBase(level, living, type, color);
            shootFanShapedDanmakuOne(level, living, shot, VAL_DEF,getFanShapedAngle(i, size));
        }
    }

    /**
     * 水平扇形发射弹幕-- 蝶形
     * @param size 数量
     */
    public static void shootFanShapedDanmakuFly(Level level, LivingEntity living, int size, DanmakuColor color){
        for(int i = 0; i < size; i++){
            DanmakuButterfly shot = new DanmakuButterfly(level, living, color);
            shootFanShapedDanmakuOne(level, living, shot, VAL_DEF / 4, getFanShapedAngle(i, size));
        }
    }

    /**
     * 水平扇形发射弹幕-- 剑形
     * @param size 数量
     */
    public static void shootFanShapedDanmakuArrow(Level level, LivingEntity living, int size, DanmakuColor color){
        for(int i = 0; i < size; i++){
            DanmakuArrow shot = new DanmakuArrow(level, living, color);
            shootFanShapedDanmakuOne(level, living, shot, VAL_DEF, getFanShapedAngle(i, size));
        }
    }

    /**
     * 水平扇形发射弹幕-- 旋风
     * @param size 数量
     */
    public static void shootFanShapedDanmakuWind(Level level, LivingEntity living, int size, DanmakuColor color){
        for(int i = 0; i < size; i++){
            WindEntity shot = new WindEntity(level, living, color);
            shootFanShapedDanmakuOne(level, living, shot, VAL_DEF / 2, getFanShapedAngle(i, size));
        }
    }

    /**
     * 水平扇形发射弹幕-- 激光
     * @param size 数量
     */
    public static void shootFanShapedDanmakuLaser(Level level, LivingEntity living, int size, DanmakuType type, DanmakuColor color){
        for(int i = 0; i < size; i++){
            DanmakuLaser shot = new DanmakuLaser(level, living, type, color);
            shootFanShapedDanmakuOne(level, living, shot, VAL_DEF * 2, getFanShapedAngle(i, size));
        }
    }

    /**
     * 水平扇形单次发射弹幕-- 圆形
     * @param angle 角度
     */
    public static void shootFanShapedDanmakuOne(Level level, LivingEntity living, DanmakuBase danmaku,float val, float angle){
        Vec3 upVector = living.getUpVector(1.0F);
        Quaternionf quaternion = (new Quaternionf()).setAngleAxis(angle * 0.017453292F, upVector.x, upVector.y, upVector.z);
        Vec3 vector3d = living.getViewVector(1.0F);
        Vector3f vec = vector3d.toVector3f().rotate(quaternion);
        danmaku.shoot(vec.x(), vec.y(), vec.z(), val, INA_DEF);
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

    // 添加半球发射常量
    private static final float HEMISPHERE_MAX_VERTICAL_ANGLE = 90.0f; // 垂直最大角度（90度，半球）
    private static final float HEMISPHERE_MAX_HORIZONTAL_ANGLE = 180.0f; // 水平最大角度（180度）

    /**
     * 半球状发射弹幕--圆形
     * @param level 世界
     * @param living 发射实体
     * @param count 弹幕数量
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     * @param type 弹幕类型
     * @param pos 圆心坐标
     * @param upward 是否向下发射
     */
    public static void shootDanmakuHemisphere(Level level, LivingEntity living, int count, float vel, float ina, DanmakuColor color, DanmakuType type, BlockPos pos, boolean upward) {
        if (count <= 0 || count > MAX_SIZE) {
            count = Math.min(MAX_SIZE, Math.max(1, count));
        }

        for (int i = 0; i < count; i++) {
            DanmakuBase danmaku = new DanmakuBase(level, living, type, color);
            shootDanmakuHemisphereOne(level, living, danmaku, vel, ina, pos, upward);
        }
    }

    /**
     * 半球状均匀发射弹幕--圆形（均匀分布）
     * @param level 世界
     * @param living 发射实体
     * @param count 弹幕数量
     * @param vel 速度
     * @param ina 误差
     * @param color 颜色
     * @param type 弹幕类型
     * @param pos 圆心坐标
     * @param upward 是否向下发射
     */
    public static void shootDanmakuHemisphereUniform(Level level, LivingEntity living, int count, float vel, float ina, DanmakuColor color, DanmakuType type, BlockPos pos, boolean upward) {
        if (count <= 0 || count > MAX_SIZE) {
            count = Math.min(MAX_SIZE, Math.max(1, count));
        }

        Vec3 centerPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        // 使用斐波那契球面采样实现均匀分布
        for (int i = 0; i < count; i++) {
            float[] angles = getHemisphereUniformAngle(i, count, upward);
            float pitch = angles[0];
            float yaw = angles[1];

            DanmakuBase danmaku = new DanmakuBase(level, living, type, color);
            Vec3 direction = calculateDirectionFromAngles(pitch, yaw);
            danmaku.setPos(centerPos);
            danmaku.shoot(direction.x, direction.y, direction.z, vel, ina);
            addEntityAndSound(level, living, danmaku);
        }
    }

    /**
     * 半球状发射单发弹幕
     * @param level 世界
     * @param living 发射实体
     * @param danmaku 弹幕实体
     * @param vel 速度
     * @param ina 误差
     * @param upward 是否向下发射
     */
    public static void shootDanmakuHemisphereOne(Level level, LivingEntity living, DanmakuBase danmaku, float vel, float ina, BlockPos pos, boolean upward) {
        // 生成半球随机角度
        float[] angles = getHemisphereRandomAngle(upward);
        float pitch = angles[0];  // 俯仰角
        float yaw = angles[1];    // 偏航角

        // 创建方向向量
        Vec3 direction = calculateDirectionFromAngles(pitch, yaw);
        danmaku.setPos(pos.getX(), pos.getY(), pos.getZ());

        // 发射弹幕
        danmaku.shoot(direction.x, direction.y, direction.z, vel, ina);
        addEntityAndSound(level, living, danmaku);
    }

    /**
     * 获取半球均匀分布角度（使用斐波那契球面采样）
     * @param index 当前索引
     * @param total 总数
     * @param upward 是否向上
     * @return [pitch, yaw] 角度数组
     */
    private static float[] getHemisphereUniformAngle(int index, int total, boolean upward) {
        // 黄金角度
        float goldenAngle = (float) (Math.PI * (3.0 - Math.sqrt(5.0)));

        // 斐波那契球面采样（适用于半球）
        float y = 1.0f - ((float) index / (total - 1)) * (upward ? 0.5f : 1.0f); // 限制在半球
        float radius = (float) Math.sqrt(1.0f - y * y);

        float theta = goldenAngle * index;

        float x = (float) (radius * Math.cos(theta));
        float z = (float) (radius * Math.sin(theta));

        // 转换为角度
        float pitch = (float) Math.toDegrees(Math.asin(y));
        float yaw = (float) Math.toDegrees(Math.atan2(z, x));

        // 调整方向
        if (!upward) {
            pitch = -pitch;
        }

        return new float[]{pitch, yaw};
    }

    /**
     * 获取半球随机角度
     * @param upward 是否向上
     * @return [pitch, yaw] 角度数组
     */
    private static float[] getHemisphereRandomAngle(boolean upward) {
        Random random = new Random();

        // 随机水平角度（0-360度）
        float yaw = random.nextFloat() * 360.0f;

        // 随机垂直角度（0-90度）
        float verticalAngle = random.nextFloat() * HEMISPHERE_MAX_VERTICAL_ANGLE;

        // 如果是向下发射，垂直角度取负
        float pitch = upward ? -verticalAngle : verticalAngle;

        return new float[]{pitch, yaw};
    }

    /**
     * 根据角度计算方向向量
     * @param pitch 俯仰角（度）
     * @param yaw 偏航角（度）
     * @return 方向向量
     */
    private static Vec3 calculateDirectionFromAngles(float pitch, float yaw) {
        // 将角度转换为弧度
        float pitchRad = (float) Math.toRadians(pitch);
        float yawRad = (float) Math.toRadians(yaw);

        // 计算方向向量
        float x = (float) (-Math.sin(yawRad) * Math.cos(pitchRad));
        float y = (float) -Math.sin(pitchRad);
        float z = (float) (Math.cos(yawRad) * Math.cos(pitchRad));

        return new Vec3(x, y, z);
    }

}
