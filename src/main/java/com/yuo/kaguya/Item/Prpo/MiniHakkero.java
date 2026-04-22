package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Event.ModEventHandler;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
                ItemStack fireCharge = KaguyaUtils.getPlayerBagItem(player, Items.FIRE_CHARGE);
                if (!level.isClientSide && !fireCharge.isEmpty() && fireCharge.getCount() >= 32) {
                    player.playSound(SoundEvents.FIRECHARGE_USE);
                    ServerLevel serverLevel = (ServerLevel) level;
                    MinecraftServer server = serverLevel.getServer();
                    server.tell(new TickTask(server.getTickCount() + 1, () -> delayedRepair(player, hand)));
                    return InteractionResultHolder.consume(itemstack);
                }
                return InteractionResultHolder.pass(itemstack);
            }else {
                if (level.isClientSide) player.displayClientMessage(Component.translatable("info.kaguya.mini_hakkero.no_damage"), true);
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }

    private void delayedRepair(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack fireCharge = KaguyaUtils.getPlayerBagItem(player, Items.FIRE_CHARGE);
        if (!stack.isEmpty() && !fireCharge.isEmpty() && fireCharge.getCount() >= 32 && stack.getDamageValue() > 0) {
            if (player.isCreative())
                stack.setDamageValue(0);
            else stack.setDamageValue(stack.getDamageValue() - 1);
            if (!player.getAbilities().instabuild)
                fireCharge.shrink(32);
            player.displayClientMessage(Component.translatable("info.kaguya.remorse_rod.fix"), true);
        }
    }
}
