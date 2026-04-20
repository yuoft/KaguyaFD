package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MiniHakkero extends KaguyaPrpo {
    public MiniHakkero() {
        super(new Properties().durability(99));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!player.isCrouching()){
            int damageValue = itemstack.getDamageValue();
            if (damageValue >= 99) return InteractionResultHolder.fail(itemstack);
            KaguyaUtils.spawnLaser(player, level, true);
            if (!player.getAbilities().instabuild) {
                itemstack.hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(hand);
                });
            }
            player.getCooldowns().addCooldown(this, 20); // 1秒冷却
            return InteractionResultHolder.success(itemstack);
        }else {
            if (itemstack.getDamageValue() > 0){ //耐久恢复
                ItemStack fireCharge = KaguyaUtils.findItemByPlayer(player, Items.FIRE_CHARGE);
                if (!fireCharge.isEmpty()) {
                    if (fireCharge.getCount() >= 32){
                        if (!player.getAbilities().instabuild) fireCharge.shrink(32);
                        itemstack.setDamageValue(itemstack.getDamageValue() - 1);
                        player.playSound(SoundEvents.FIRECHARGE_USE);
                        return InteractionResultHolder.success(itemstack);
                    }
                }
                return InteractionResultHolder.pass(itemstack);
            }else {
                if (level.isClientSide) player.displayClientMessage(Component.translatable("info.kaguya.mini_hakkero.no_damage"), true);
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }
}
