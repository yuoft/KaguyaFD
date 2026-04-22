package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Event.ModEventHandler;
import com.yuo.kaguya.Event.NetWorkHandler;
import com.yuo.kaguya.Item.KaguyaWeapon;
import com.yuo.kaguya.Item.ModNoDataGenItem;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RemorseRod extends KaguyaWeapon implements ModNoDataGenItem {
    public RemorseRod() {
        super(Tiers.STONE, new Properties().durability(1));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity living, LivingEntity livingEntity) {
        int damageValue = stack.getDamageValue();
        if (damageValue == 0 && livingEntity instanceof Player player){
            float health = living.getHealth();
            float v = 0.75f * living.getMaxHealth();
            if (health > v){
                living.setHealth(v);
                if (!player.getAbilities().instabuild)
                    stack.setDamageValue(1);
            }else {
                living.hurt(player.damageSources().playerAttack(player), this.getDamage());
            }
            return true;
        }

        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int damageValue = stack.getDamageValue();
        if (damageValue > 0){
            ItemStack bagItem = KaguyaUtils.getPlayerBagItem(player, Items.INK_SAC);
            if (!level.isClientSide && !bagItem.isEmpty()) {
                // 将修复任务推迟到下一 tick
                ServerLevel serverLevel = (ServerLevel) level;
                MinecraftServer server = serverLevel.getServer();
                server.tell(new TickTask(server.getTickCount() + 1, () -> delayedRepair(player, hand)));
                return InteractionResultHolder.consume(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    private void delayedRepair(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack inkSac = KaguyaUtils.getPlayerBagItem(player, Items.INK_SAC);
        if (!stack.isEmpty() && !inkSac.isEmpty() && stack.getDamageValue() > 0) {
            stack.setDamageValue(0);
            if (!player.getAbilities().instabuild) {
                inkSac.shrink(1);
            }
            player.displayClientMessage(Component.translatable("info.kaguya.remorse_rod.fix"), true);
        }
    }
}
