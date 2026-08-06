package com.yuo.kaguya.Entity.Mob;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class Cirno extends BaseMobEntity{
    public Cirno(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Frog.class, false));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        // 执行原攻击逻辑
        boolean attacked = super.doHurtTarget(target);

        // 若攻击成功，且目标为青蛙，且目标已死亡（刚被本次攻击杀死）
        if (attacked && target instanceof Frog && ((Frog) target).isDeadOrDying()) {
            this.spawnAtLocation(ModItems.frozenFrog.get());
        }

        return attacked;
    }
}
