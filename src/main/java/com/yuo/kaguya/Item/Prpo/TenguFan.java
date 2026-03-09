package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TenguFan extends KaguyaPrpo {

    public TenguFan() {
        super(new Properties().durability(40));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            DanmakuShootHelper.shootDanmakuWindPush(level, player, 0.35f, 0.1f, DanmakuColor.ORANGE, 1.0f);
            player.getCooldowns().addCooldown(this, 20);
            player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE);

            if (!player.getAbilities().instabuild) {
                player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
        }
        return super.use(level, player, hand);
    }

}
