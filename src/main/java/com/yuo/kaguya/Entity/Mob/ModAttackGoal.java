package com.yuo.kaguya.Entity.Mob;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ModAttackGoal extends MeleeAttackGoal {
    private final BaseMobEntity reimu;
    private int raiseArmTicks;

    public ModAttackGoal(BaseMobEntity reimu, double v, boolean b) {
        super(reimu, v, b);
        this.reimu = reimu;
    }

    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    public void stop() {
        super.stop();
        this.reimu.setAggressive(false);
    }

    public void tick() {
        super.tick();
        ++this.raiseArmTicks;
        if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
            this.reimu.setAggressive(true);
        } else {
            this.reimu.setAggressive(false);
        }

    }
}