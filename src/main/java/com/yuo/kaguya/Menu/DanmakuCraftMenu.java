package com.yuo.kaguya.Menu;

import com.yuo.kaguya.Tile.DanmakuCraftTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DanmakuCraftMenu extends AbstractContainerMenu {
    private final DanmakuCraftTile tile;
    private final Player player;
    private final Level level;

    public DanmakuCraftMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, (Container)inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public DanmakuCraftMenu(int id, Inventory inventory, Container tile){
        super(ModMenuTypes.DANMAKU_CRAFT.get(), id);
        this.tile = (DanmakuCraftTile) tile;
        this.player = inventory.player;
        this.level = inventory.player.level();

        //升级材料
        this.addSlot(new Slot(tile, 0, 88, 17));
        this.addSlot(new DanmakuColorSlot(tile, 1, 88, 35));
        this.addSlot(new DanmakuPPotionSlot(tile, 2, 88, 53));

        this.addSlot(new DanmakuSlot(tile, 3, 110, 17)); //待升级弹幕
        this.addSlot(new ResultSlot(player, tile, 4, 146, 35)); //输出

        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + k * 9 + 9, 48 + j * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 48 + k * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
