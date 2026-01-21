package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HisouSword extends KaguyaWeapon {
    private Entity entity;
    public HisouSword() {
        super(Tiers.NETHERITE, 5, new Properties().stacksTo(1).defaultDurability(200).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        if (entity != null) {
            components.add(Component.translatable("info.kaguya.hisou_sword").append(entity.getDisplayName()));
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity mob, LivingEntity player) {
        if (entity == null) {
            entity = mob;
        }else {
            mob.hurt(player.damageSources().playerAttack((Player) player), this.getDamage() * 2);
        }
        return super.hurtEnemy(stack, mob, player);
    }
}
