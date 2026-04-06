package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DanmakuLaser extends DanmakuBase{
    public static final EntityType<DanmakuLaser> TYPE = EntityType.Builder.<DanmakuLaser>of(DanmakuLaser::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(6).updateInterval(10).noSave().build("danmaku_laser");
    protected int laserPierceNum; //穿透数
    private int maxLaserPierceNum; //最大穿透数
    private boolean isMainLaser; //是否为主激光
    private final List<DanmakuLaser0> orbitingLasers = new ArrayList<>();
    private double helixAngle = 0; // DNA螺旋角度
    private static final double HELIX_RADIUS = 1.2; // 螺旋半径
    private static final double HELIX_HEIGHT = 2.5; // 螺旋高度范围
    private static final double ROTATION_SPEED = 0.1; // 旋转速度
    private int totalLasers; //环绕激光数量

    public DanmakuLaser(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.danmakuType = DanmakuType.SHORT_LASER;
    }

    public DanmakuLaser(Level level, LivingEntity living, DanmakuType danmakuType, DanmakuColor danmakuColor) {
        super(TYPE, level, living);
        this.danmakuType = danmakuType;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setDanmakuPierce(true);
        this.setMaxLaserPierceNum(danmakuType.getIntSize() * 3);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();
        BlockPos pos = this.getOnPos();
        if (!level.isClientSide){
            if (laserPierceNum >= getMaxLaserPierceNum()){
                level.explode(this, pos.getX(), pos.getY(), pos.getZ(), 1.0f * getDanmakuType().getIntSize(), ExplosionInteraction.MOB);
                this.discard();
            }

            if (isMainLaser && orbitingLasers.isEmpty() && tickCount == 1){
                generateOrbitingLasers();
            }

            if (isMainLaser && !orbitingLasers.isEmpty()) {
                updateOrbitingLasers();
            }
        }
    }

    @Override
    protected void doDamageEffects(LivingEntity owner, LivingEntity target) {
        super.doDamageEffects(owner, target);
        if (target.isOnFire()) target.setSecondsOnFire(danmakuType.getIntSize());
        laserPierceNum++;

        if (target instanceof Creeper creeper){
            creeper.ignite();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        BlockPos pos = result.getBlockPos();
        Direction direction = result.getDirection();
        Level level = this.level();
        if (!level.isClientSide){
            BlockState state = level.getBlockState(pos);
            if (state.isFlammable(level, pos, direction) && this.getOwner() instanceof LivingEntity living){
                state.onCaughtFire(level, pos, direction, living); //点燃方块
            }
            level.explode(this, pos.getX(), pos.getY(), pos.getZ(), 1.0f * danmakuType.getIntSize(), ExplosionInteraction.TNT);
        }
        this.discard();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        // 确保主激光移除时清理环绕激光
        if (this.isMainLaser && orbitingLasers != null) {
            for (DanmakuLaser0 laser : orbitingLasers) {
                if (laser != null && laser.isAlive()) {
                    laser.discard();
                }
            }
            orbitingLasers.clear();
        }
    }

    public void setLaserPierceNum(int laserPierceNum) {
        this.laserPierceNum = Math.max(1, laserPierceNum);
    }

    public void setMainLaser(boolean mainLaser) {
        isMainLaser = mainLaser;
    }

    public void setMaxLaserPierceNum(int maxLaserPierceNum) {
        this.maxLaserPierceNum = Math.max(1, maxLaserPierceNum);
    }

    public int getMaxLaserPierceNum() {
        return maxLaserPierceNum;
    }

    public int getTotalLasers() {
        return totalLasers;
    }

    public void setTotalLasers(int totalLasers) {
        int i = Math.max(2, totalLasers);
        if (i % 2 != 0){
            i += 1;
        }
        this.totalLasers = i;
    }

    // 添加辅助方法：根据方向向量构建旋转矩阵的基向量
    private Vec3[] getBasisVectors(Vec3 direction) {
        Vec3 forward = direction.normalize();

        // 计算向上的参考向量（尽量使用世界Y轴，但如果forward接近垂直则需要特殊处理）
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right;

        // 如果forward与Y轴接近平行，使用Z轴作为参考
        if (Math.abs(forward.y) > 0.99) {
            right = new Vec3(1, 0, 0).cross(forward).normalize();
        } else {
            right = up.cross(forward).normalize();
        }

        // 重新计算真正的up向量
        Vec3 actualUp = forward.cross(right).normalize();

        return new Vec3[]{right, actualUp, forward};
    }

    // 修改 updateOrbitingLasers 方法
    private void updateOrbitingLasers() {
        helixAngle += ROTATION_SPEED;

        Vec3 centerPos = this.position();
        Vec3 moveDirection = this.getDeltaMovement();

        // 如果移动方向为零向量（静止），则使用默认的前进方向
        if (moveDirection.lengthSqr() < 0.0001) {
            moveDirection = new Vec3(0, 0, 1);
        }

        // 计算主激光的朝向角度（供环绕激光使用）
        double mainYaw = Math.toDegrees(Math.atan2(-moveDirection.x, moveDirection.z));
        double mainPitch = Math.toDegrees(Math.asin(moveDirection.y / moveDirection.length()));

        // 获取局部坐标系的基向量
        Vec3[] basis = getBasisVectors(moveDirection);
        Vec3 right = basis[0];
        Vec3 up = basis[1];
        Vec3 forward = basis[2];

        // 使用迭代器遍历，以便安全移除失效的激光
        Iterator<DanmakuLaser0> iterator = orbitingLasers.iterator();
        while (iterator.hasNext()) {
            DanmakuLaser0 laser = iterator.next();

            // 检查激光是否失效（被销毁、死亡或不在世界中）
            if (laser == null || !laser.isAlive() || laser.isRemoved()) {
                iterator.remove();
                continue;
            }

            // 获取存储的位置参数
            double t = laser.getOrbitT();
            double baseAngle = laser.getOrbitAngle();

            // 当前角度 = 基础角度 + 旋转角度
            double currentAngle = baseAngle + helixAngle;

            // 计算在垂直于移动方向的平面上的圆形位置
            double offsetX = HELIX_RADIUS * Math.cos(currentAngle);
            double offsetY = HELIX_RADIUS * Math.sin(currentAngle);

            // 沿移动方向的偏移
            double offsetZ = (t - 0.5) * HELIX_HEIGHT;

            // 将局部坐标转换到世界坐标
            Vec3 worldOffset = right.scale(offsetX)
                    .add(up.scale(offsetY))
                    .add(forward.scale(offsetZ));

            // 设置新位置
            Vec3 newPos = centerPos.add(worldOffset);
            laser.setPos(newPos.x, newPos.y, newPos.z);

            // 设置环绕激光的速度：跟随主激光移动 + 环绕切向速度
            Vec3 tangentialVelocity = right.scale(-Math.sin(currentAngle) * ROTATION_SPEED * HELIX_RADIUS)
                    .add(up.scale(Math.cos(currentAngle) * ROTATION_SPEED * HELIX_RADIUS));

            Vec3 followVelocity = this.getDeltaMovement();
            laser.setDeltaMovement(followVelocity.add(tangentialVelocity));

            // 设置环绕激光的朝向
            laser.setYRot((float) mainYaw);
            laser.setXRot((float) mainPitch);
            laser.yRotO = laser.getYRot();
            laser.xRotO = laser.getXRot();
        }
    }

    // 修改 generateOrbitingLasers 方法，根据移动方向生成初始位置
    private void generateOrbitingLasers() {
        int totalLasers = getTotalLasers();
        int lasersPerStrand = totalLasers / 2;

        Vec3 centerPos = this.position();
        Vec3 moveDirection = this.getDeltaMovement();
        if (moveDirection.lengthSqr() < 0.0001) {
            moveDirection = new Vec3(0, 0, 1);
        }

        Vec3[] basis = getBasisVectors(moveDirection);
        Vec3 right = basis[0];
        Vec3 up = basis[1];
        Vec3 forward = basis[2];

        for (int i = 0; i < lasersPerStrand; i++) {
            double t = (double) i / lasersPerStrand;

            // 两条链，角度相差180度
            double angle1 = 2 * Math.PI * t;
            double angle2 = 2 * Math.PI * t + Math.PI;

            // 创建两个激光
            DanmakuLaser0 laser1 = new DanmakuLaser0(level(), this);
            DanmakuLaser0 laser2 = new DanmakuLaser0(level(), this);

            orbitingLasers.add(laser1);
            orbitingLasers.add(laser2);

            laser1.setOrbitParameter(t, angle1);
            laser2.setOrbitParameter(t, angle2);

            // 计算初始位置
            double offsetZ1 = (t - 0.5) * HELIX_HEIGHT;
            double offsetZ2 = (t - 0.5) * HELIX_HEIGHT;

            Vec3 worldOffset1 = right.scale(HELIX_RADIUS * Math.cos(angle1))
                    .add(up.scale(HELIX_RADIUS * Math.sin(angle1)))
                    .add(forward.scale(offsetZ1));
            Vec3 worldOffset2 = right.scale(HELIX_RADIUS * Math.cos(angle2))
                    .add(up.scale(HELIX_RADIUS * Math.sin(angle2)))
                    .add(forward.scale(offsetZ2));

            laser1.setPos(centerPos.add(worldOffset1));
            laser2.setPos(centerPos.add(worldOffset2));

            // 添加到世界
            level().addFreshEntity(laser1);
            level().addFreshEntity(laser2);
        }
    }
}
