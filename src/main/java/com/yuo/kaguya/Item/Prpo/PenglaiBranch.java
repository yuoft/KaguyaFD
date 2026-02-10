package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PenglaiBranch extends KaguyaPrpo {
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        RandomSource random = level.random;
        boolean flag = random.nextBoolean();
        if (player.isCrouching()){
            if (flag){
                DanmakuShootHelper.shootRandomFanShapeDanmaku(level, player);
            }else DanmakuShootHelper.shootFanShapedDanmaku(level, player, 11, DanmakuType.random(random), DanmakuColor.random(random));
            player.getCooldowns().addCooldown(this, 20);
        }else {
            if (flag){
                DanmakuShootHelper.shootRandomDanmaku(level, player);
            }else DanmakuShootHelper.shootDanmaku(level, player, DanmakuShootHelper.VAL_DEF, DanmakuShootHelper.INA_DEF, DanmakuColor.random(random), DanmakuType.random(random));
            player.getCooldowns().addCooldown(this, 5);
        }
        return super.use(level, player, hand);
    }
}
