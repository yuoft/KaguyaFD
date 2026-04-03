package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class Gungnir extends KaguyaWeapon {
    public Gungnir() {
        super(Tiers.NETHERITE, 0, new Properties().durability(95));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        int expLv = player.experienceLevel;
        if (expLv > 3){
            player.experienceLevel -= 3;
            //神枪 中间主体 周围旋转小激光
            DanmakuShootHelper.shootDanmakuGungnir(level, player, DanmakuShootHelper.VAL_DEF / 2, DanmakuShootHelper.INA_DEF);
        }else player.displayClientMessage(Component.nullToEmpty("No 3 expLevel!"), true);
        return super.use(level, player, hand);
    }
}
