package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.KaguyaWeapon;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HakureiReimuStick extends KaguyaWeapon {
    public HakureiReimuStick() {
        super(Tiers.DIAMOND, 0, new Properties().durability(89));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            float chargeRatio = KaguyaUtils.getChargeRatio(this.getUseDuration(stack), timeLeft, 40);

            if (!level.isClientSide) {
                DanmakuShootHelper.shootDanmakuOrb(level, player, DanmakuShootHelper.VAL_DEF / 4, DanmakuShootHelper.INA_DEF, chargeRatio, 5);

                player.getCooldowns().addCooldown(this, 20);
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}
