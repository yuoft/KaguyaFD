package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Eye3rd extends KaguyaPrpo {
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Entity entity = KaguyaUtils.getHitEntity(player.getEyePosition(), player.getViewVector(1.0f), level, player, 60);
        if (entity instanceof LivingEntity living && !player.isCrouching()) {
            Component displayName = living.getDisplayName();
            float maxHealth = living.getMaxHealth();
            MutableComponent entityName = Component.translatable("info.kaguya.third_eye_0.entity", displayName);
            MutableComponent maxHealthValue = Component.translatable("info.kaguya.third_eye_0.max_health", maxHealth);
            entityName.append(maxHealthValue);
            if (living instanceof TamableAnimal tamableAnimal){
                LivingEntity owner = tamableAnimal.getOwner();
                if (owner != null){
                    MutableComponent ownerName = Component.translatable("info.kaguya.third_eye_0.owner", owner.getDisplayName());
                    entityName.append(ownerName);
                }
            }
            if (!level.isClientSide) {
                living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0));
            }
            player.getCooldowns().addCooldown(this, 10);
            player.displayClientMessage(entityName, true);
        }
        return InteractionResultHolder.pass(stack);
    }
}
