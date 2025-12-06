package com.yuo.kaguya.Item;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Entity.DanmakuType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DanmakuShotItem extends Item {
    public DanmakuShotItem() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isCrouching()) {
            if (stack.getItem() == ModItems.heartShot.get()){
                DanmakuShootHelper.shootDanmaku(world, player);
            }else if (stack.getItem() == ModItems.butterflyShot.get()){
                DanmakuShootHelper.shootDanmaku(world, player, 0.05f, 1.0f, DanmakuColor.GREEN, DanmakuType.BUTTER_FLY);
            }
//            player.getCooldowns().addCooldown(stack.getItem(), 30);
        }else {
            if (stack.getItem() == ModItems.heartShot.get()){
                DanmakuShootHelper.fanShapedShotDanmaku(world, player, 5);
            }
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return InteractionResultHolder.pass(stack);
    }

    public static int getColor(ItemStack stack, int i) {
        return i > 0 ?  -1 : DanmakuColor.RED.getRgb();
    }
}
