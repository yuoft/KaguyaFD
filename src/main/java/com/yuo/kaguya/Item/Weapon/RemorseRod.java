package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Event.ModEventHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class RemorseRod extends SwordItem {
    public RemorseRod() {
        super(Tiers.STONE, 0, -2.4f, new Properties().durability(1));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity living, LivingEntity livingEntity) {
        int damageValue = stack.getDamageValue();
        if (damageValue == 0 && livingEntity instanceof Player player){
            living.setHealth(0.75f * living.getMaxHealth());
            if (!player.getAbilities().instabuild)
                stack.setDamageValue(1);
            return true;
        }

        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()){
            ItemStack stack = player.getItemInHand(hand);
            int damageValue = stack.getDamageValue();
            if (damageValue > 0){
                ItemStack bagItem = ModEventHandler.getPlayerBagItem(player, Items.INK_SAC);
                if (bagItem != null){
                    stack.setDamageValue(0);
                    if (!player.getAbilities().instabuild){
                        bagItem.shrink(1);
                    }
                    player.sendSystemMessage(Component.keybind("info.kaguya.remorse_rod.fix"));
                }
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }
}
