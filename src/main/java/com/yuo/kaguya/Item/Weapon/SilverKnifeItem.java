package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.SilverKnife;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SilverKnifeItem extends SwordItem {
    public SilverKnifeItem() {
        super(Tiers.IRON, 1, -2.4f, new Properties().stacksTo(64));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 36000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int i) {
        if (living instanceof Player player) {
            int useDuration = this.getUseDuration(stack) - i;
            if (useDuration >= 10) {
                if (!level.isClientSide) {
                    SilverKnife silverKnife = new SilverKnife(getType(stack), player, level);
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
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(stack);
        } else if (EnchantmentHelper.getRiptide(stack) > 0 && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(stack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    private static EntityType<SilverKnife> getType(ItemStack stack){
        Item item = stack.getItem();
        if (item == ModItems.silverKnifeRed.get()) return SilverKnife.TYPE_RED;
        else if (item == ModItems.silverKnifeGreen.get()) return SilverKnife.TYPE_GREEN;
        else if (item == ModItems.silverKnifeBlue.get()) return SilverKnife.TYPE_BLUE;
        else return SilverKnife.TYPE_WHITE;
    }
}
