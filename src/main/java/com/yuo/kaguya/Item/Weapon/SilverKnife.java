package com.yuo.kaguya.Item.Weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.yuo.kaguya.Entity.SilverKnifeEntity;
import com.yuo.kaguya.Item.ModColorItemUtils;
import com.yuo.kaguya.Item.ModNoDataGen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.List;

public class SilverKnife extends Item implements ModNoDataGen {
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public SilverKnife() {
        super(new Properties().stacksTo(64));
        this.attackDamage = 1.5f;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.0f, Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int i) {
        if (living instanceof Player player) {
            int useDuration = this.getUseDuration(stack) - i;
            if (useDuration >= 10) {
                if (!level.isClientSide) {
                    SilverKnifeEntity silverKnife = new SilverKnifeEntity(player, level, stack, ModColorItemUtils.getColor(stack));
                    silverKnife.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                    if (player.getAbilities().instabuild) {
                        silverKnife.pickup = Pickup.CREATIVE_ONLY;
                    }

                    level.addFreshEntity(silverKnife);
                    level.playSound((Player) null, silverKnife, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (EnchantmentHelper.getRiptide(stack) > 0 && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(stack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return ModColorItemUtils.getColorName(stack, super.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ModColorItemUtils.appendColorText(stack, tooltip);
    }
}
