package com.yuo.kaguya.Menu;

import com.yuo.kaguya.Item.DanmakuShotItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

public class DanmakuColorSlot extends Slot {
    public DanmakuColorSlot(Container container, int i, int x, int y) {
        super(container, i, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof DyeItem;
    }
}
