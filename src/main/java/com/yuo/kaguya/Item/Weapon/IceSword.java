package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.IceStatueEntity;
import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

public class IceSword extends KaguyaWeapon {
    public IceSword() {
        super(Tiers.DIAMOND, new Properties().durability(30));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity owner) {
        if (!(owner instanceof Player)) return super.hurtEnemy(stack, target, owner);
        IceStatueEntity iceStatue = IceStatueEntity.buildFrozenStatue(target);
        if (target instanceof Player) {
            target.hurt(DanmakuDamageTypes.danmaku(owner), Float.MAX_VALUE);
        }else {
            target.remove(RemovalReason.KILLED);
        }

        if (!owner.level().isClientSide){
            owner.playSound(SoundEvents.GLASS_BREAK);
            owner.level().addFreshEntity(iceStatue);
            return true;
        }

        return super.hurtEnemy(stack, target, owner);
    }
}
