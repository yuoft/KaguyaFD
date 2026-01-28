package com.yuo.kaguya.Menu;

import com.yuo.kaguya.Item.DanmakuShotItem;
import com.yuo.kaguya.Item.ModColorCraftItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DanmakuSlot extends Slot {
    public DanmakuSlot(Container container, int i, int x, int y) {
        super(container, i, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof DanmakuShotItem || stack.getItem() instanceof ModColorCraftItem;
    }
}
