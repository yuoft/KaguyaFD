package com.yuo.kaguya.Item.Weapon;

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

        }else { //左右
            
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity mob, LivingEntity player) {
        mob.setSecondsOnFire(2);
        return super.hurtEnemy(stack, mob, player);
    }
}
