package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.CrossEntity;
import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

/**
 * 莱瓦汀
 */
public class LaevateinnSword extends KaguyaWeapon {
    public LaevateinnSword() {
        super(Tiers.DIAMOND, 0, new Properties().durability(95));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isCrouching()){ //上下 十字架
            // 创建挥砍实体：伤害，挥砍幅度，挥砍角度，时间（1.5s动画）
            CrossEntity slash = new CrossEntity(level, player, 7.0f, 60, 90, 60);
            level.addFreshEntity(slash);
        }else { //左右
            CrossEntity slash = new CrossEntity(level, player, 7.0f, 60, 0, 60);
            level.addFreshEntity(slash);
        }

        player.getCooldowns().addCooldown(this, 100);
        return super.use(level, player, hand);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity mob, LivingEntity player) {
        mob.setSecondsOnFire(2);
        return super.hurtEnemy(stack, mob, player);
    }
}
