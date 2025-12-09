package com.yuo.kaguya.Menu;

import com.yuo.kaguya.Tile.DanmakuCraftTile;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ResultSlot extends Slot {
    private final Player player;

    public ResultSlot(Player player, Container container, int i, int x, int y) {
        super(container, i, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        if (container instanceof DanmakuCraftTile tile){
            tile.clearInputContent();
        }
    }
}
