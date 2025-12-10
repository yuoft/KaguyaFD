package com.yuo.kaguya.Menu;

import com.yuo.kaguya.Item.DanmakuShotItem;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DanmakuPPotionSlot extends Slot {
    public DanmakuPPotionSlot(Container container, int i, int x, int y) {
        super(container, i, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() == ModItems.bigPotion.get();
    }
}
