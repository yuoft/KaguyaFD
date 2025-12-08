package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.DanmakuShootHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class YuyukoFan extends SwordItem {

    public YuyukoFan() {
        super(Tiers.DIAMOND, 3, -2.4f, new Properties().stacksTo(1).defaultDurability(233).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        DanmakuShootHelper.fanShapedShotDanmakuFly(level, player, 11);
        return super.use(level, player, hand);
    }
}
