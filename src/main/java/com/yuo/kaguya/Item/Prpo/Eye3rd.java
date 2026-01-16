package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Eye3rd extends KaguyaPrpo {
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            Entity entity = KaguyaUtils.getHitEntity(player.getEyePosition(), player.getViewVector(1.0f), level, player, 60);
            if (entity instanceof LivingEntity living) {
                Component displayName = living.getDisplayName();
                float maxHealth = living.getMaxHealth();
                StringBuilder sb = new StringBuilder("Entity:" + displayName.getString() + "/MaxHealth:" + maxHealth);
                if (living instanceof TamableAnimal tamableAnimal){
                    Component ownerName = tamableAnimal.getOwner().getDisplayName();
                    sb.append("/Owner:").append(ownerName.getString());
                }
                player.displayClientMessage(Component.nullToEmpty(sb.toString()), true);
            }
        }
        return super.use(level, player, hand);
    }
}
