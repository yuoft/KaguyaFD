package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModColorItemUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GapFoldingUmbrella extends KaguyaPrpo{

    public GapFoldingUmbrella() {
        super(new Properties().durability(30));
    }

    @Override
    public Component getName(ItemStack stack) {
       return ModColorItemUtils.getColorName(stack, super.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ModColorItemUtils.appendColorText(stack, tooltip);
    }
}
