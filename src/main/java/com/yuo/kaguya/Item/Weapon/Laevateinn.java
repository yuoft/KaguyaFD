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

public class Laevateinn extends KaguyaWeapon {
    public Laevateinn() {
        super(Tiers.DIAMOND, 0, new Properties().durability(95));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isCrouching()){ //上下 十字架
            CrossEntity verticalCross = new CrossEntity(level, player, CrossEntity.RotationType.VERTICAL, 2.5, 0.15);
            verticalCross.setHeightOffset(2.5);
            level.addFreshEntity(verticalCross);
        }else { //左右
            CrossEntity horizontalCross = new CrossEntity(level, player, CrossEntity.RotationType.HORIZONTAL, 3.0, 0.1);
            horizontalCross.setHeightOffset(1.0); // 设置在玩家腰部高度
            level.addFreshEntity(horizontalCross);
        }

        player.getCooldowns().addCooldown(this, 200);
        return super.use(level, player, hand);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity mob, LivingEntity player) {
        mob.setSecondsOnFire(2);
        return super.hurtEnemy(stack, mob, player);
    }
}
