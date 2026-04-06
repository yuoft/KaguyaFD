package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Item.KaguyaWeapon;
import com.yuo.kaguya.Item.ModNoDataGenItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class YuukaParasol extends KaguyaWeapon implements ModNoDataGenItem {
    private static final String NBT_OPEN = "kaguya_parasol_open";

    public YuukaParasol() {
        super(Tiers.DIAMOND, new Properties().durability(60));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        RandomSource random = level.random;
        if (player.isShiftKeyDown()) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains(NBT_OPEN)) {
                tag.putBoolean(NBT_OPEN, !tag.getBoolean(NBT_OPEN));
            }else tag.putBoolean(NBT_OPEN, true);
            return InteractionResultHolder.success(stack);
        }else {
            DanmakuShootHelper.shootDanmakuFlower(level, player, DanmakuShootHelper.VAL_DEF / 2, DanmakuShootHelper.INA_DEF, DanmakuColor.random(random), DanmakuType.random(random));
        }
        return super.use(level, player, hand);
    }


    /**
     * 幽香阳伞是否打开
     */
    public static boolean isOpen(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains(NBT_OPEN) && tag.getBoolean(NBT_OPEN);
    }
}
