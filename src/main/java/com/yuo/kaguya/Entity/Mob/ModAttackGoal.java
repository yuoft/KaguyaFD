package com.yuo.kaguya.Entity.Mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;

public class ModAttackGoal extends MeleeAttackGoal {
    private final BaseMobEntity mob;
    private final double maxDistance; // 最大近战距离
    private int raiseArmTicks;

    public ModAttackGoal(BaseMobEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double maxDistance) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        // 只有距离 < maxDistance 时才使用近战攻击
        double distance = this.mob.distanceTo(target);
        return distance < this.maxDistance;
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

        // 只有距离 < maxDistance + 2 时继续近战（给一点缓冲）
        double distance = this.mob.distanceTo(target);
        return distance < this.maxDistance + 2;
    }

    @Override
    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.raiseArmTicks;
        if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
            this.mob.setAggressive(true);
        } else {
            this.mob.setAggressive(false);
        }
    }
}