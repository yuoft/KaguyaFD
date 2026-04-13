package com.yuo.kaguya.Entity.Mob;

import com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RangeAttackGoal extends Goal {
    private static final int ATTACK_COOLDOWN = 20; // 攻击冷却
    private final BaseMobEntity mob;
    private final double minDistance;
    private final double speedIn;
    private int attackCooldown;

    public RangeAttackGoal(BaseMobEntity entityFairy, double minDistance, double speedIn) {
        this.mob = entityFairy;
        this.minDistance = minDistance;
        this.speedIn = speedIn;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        // 只有距离 >= minDistance 时才使用远程攻击
        double distance = this.mob.distanceTo(target);
        return distance >= this.minDistance && this.mob.getSensing().hasLineOfSight(target);
    }

    @Override
    public void start() {
        this.attackCooldown = 0;
        this.mob.setAggressive(true);
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) return;

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distance = this.mob.distanceTo(target);

        // 保持距离：如果距离小于最小距离，后退
        if (distance < this.minDistance - 1) {
            // 后退
            this.mob.getNavigation().stop();
            // 可以添加后退逻辑
        } else if (distance > this.minDistance + 2) {
            // 追击
            this.mob.getNavigation().moveTo(target, this.speedIn);
        } else {
            // 在合适距离内，停止移动
            this.mob.getNavigation().stop();
        }

        // 攻击冷却
        if (this.attackCooldown <= 0) {
            if (this.mob.getSensing().hasLineOfSight(target)) {
                float percent = (float)(distance / this.minDistance);
                this.mob.performRangedAttack(target, 1.0F - percent);
                this.attackCooldown = ATTACK_COOLDOWN;
            }
        } else {
            this.attackCooldown--;
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        // 检查目标是否有效
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.isSpectator() || player.isCreative()) {
                return false;
            }
        }

        // 只有距离 >= minDistance 时继续远程攻击
        double distance = this.mob.distanceTo(target);
        return distance >= this.minDistance - 2; // 给一点缓冲，避免频繁切换
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.mob.setAggressive(false);
        this.attackCooldown = 0;
    }
}