package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class YuyukoFan extends KaguyaWeapon {

    public YuyukoFan() {
        super(Tiers.DIAMOND, new Properties().defaultDurability(233).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        DanmakuShootHelper.shootFanShapedDanmakuFly(level, player, 11, DanmakuColor.random(level.random));
        player.getCooldowns().addCooldown(this, 10);
        return super.use(level, player, hand);
    }
}
