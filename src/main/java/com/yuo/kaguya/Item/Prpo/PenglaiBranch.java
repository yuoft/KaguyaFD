package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 蓬莱玉枝
 */
public class PenglaiBranch extends KaguyaPrpo {
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        RandomSource random = level.random;
        boolean flag = random.nextBoolean();
        if (player.isCrouching()){
            if (flag){
                DanmakuShootHelper.shootRandomXZCircleDanmaku(level, player, 20);
            }else DanmakuShootHelper.shootXZCircleDanmaku(level, player, 20, DanmakuShootHelper.VAL_DEF, DanmakuShootHelper.INA_DEF, DanmakuType.random(random));
            player.getCooldowns().addCooldown(this, 20);
        }else {
            if (flag){
                DanmakuShootHelper.shootRandomConeDanmaku(level, player, 10, 20f);
            }else DanmakuShootHelper.shootConeDanmaku(level, player, 10, 20f, DanmakuShootHelper.VAL_DEF, DanmakuShootHelper.INA_DEF, DanmakuType.random(random));
            player.getCooldowns().addCooldown(this, 5);
        }
        return super.use(level, player, hand);
    }
}
