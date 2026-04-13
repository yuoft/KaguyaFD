package com.yuo.kaguya.Entity.Mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RangeAttackGoal extends Goal {
    private static final int MAX_WITH_IN_RANGE_TIME = 20;
    private final BaseMobEntity mob;
    private final double minDistance;
    private final double speedIn;
    private Path path;
    private int withInRangeTime;

    public RangeAttackGoal(BaseMobEntity entityFairy, double minDistance, double speedIn) {
        this.mob = entityFairy;
        this.minDistance = minDistance;
        this.speedIn = speedIn;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && target.isAlive()) {
            this.path = this.mob.getNavigation().createPath(target, 0);
            return this.path != null;
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedIn);
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && target.isAlive()) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distance = (double)this.mob.distanceTo(target);
            if (this.mob.getSensing().hasLineOfSight(target) && distance >= this.minDistance) {
                this.mob.getNavigation().moveTo(target, this.speedIn);
                this.withInRangeTime = 0;
            } else if (distance < this.minDistance) {
                this.mob.getNavigation().stop();
                ++this.withInRangeTime;
                Vec3 motion = this.mob.getDeltaMovement();
                this.mob.setDeltaMovement(motion.x, 0.0, motion.z);
                this.mob.setNoGravity(true);
                if (this.withInRangeTime > 20) {
                    float percent = (float)(distance / this.minDistance);
                    this.mob.performRangedAttack(target, 1.0F - percent);
                    this.withInRangeTime = 0;
                }
            } else {
                this.withInRangeTime = 0;
            }

        }
    }

    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && target.isAlive()) {
            boolean isPlayerAndCanNotBeAttacked = target instanceof Player && (target.isSpectator() || ((Player)target).isCreative());
            return !isPlayerAndCanNotBeAttacked;
        } else {
            return false;
        }
    }

    public void stop() {
        LivingEntity target = this.mob.getTarget();
        boolean isPlayerAndCanNotBeAttacked = target instanceof Player && (target.isSpectator() || ((Player)target).isCreative());
        if (isPlayerAndCanNotBeAttacked) {
            this.mob.setTarget((LivingEntity)null);
        }

        this.mob.getNavigation().stop();
        this.withInRangeTime = 0;
    }
}