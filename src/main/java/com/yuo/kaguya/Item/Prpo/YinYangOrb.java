package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class YinYangOrb extends KaguyaPrpo {

    public YinYangOrb() {
        super(new Properties().stacksTo(1).durability(150));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int i) {
        if (living instanceof Player player) {
            float chargeRatio = KaguyaUtils.getChargeRatio(getUseDuration(stack), i, 40);
            if (chargeRatio > 0.1f){
                Entity entity = KaguyaUtils.getHitEntity(player.getEyePosition(), player.getViewVector(1.0f), level, player, 64);
                if (entity instanceof LivingEntity le) {
                    BlockPos pos = le.getOnPos();
                    player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (!player.getAbilities().instabuild){
                        stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(player.getUsedItemHand()));
                    }
                    player.playSound(SoundEvents.ENDERMAN_TELEPORT);
                    player.getCooldowns().addCooldown(this, 20);
                }
            }
        }
    }
}
