package com.yuo.kaguya.Tile;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Item.*;
import com.yuo.kaguya.KaguyaUtils;
import com.yuo.kaguya.Menu.DanmakuCraftMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DanmakuCraftTile extends BaseContainerBlockEntity {
    //0-无 1-染料格 2-p点 3-基础弹幕 4-输出
    private final NonNullList<ItemStack> stacks = NonNullList.withSize(5, ItemStack.EMPTY);

    public DanmakuCraftTile(BlockPos pos, BlockState state) {
        super(ModTileTypes.DANMAKU_CRAFT.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DanmakuCraftTile danmakuCraftTile) {
        ItemStack base = danmakuCraftTile.getItem(3);
        if (base.isEmpty()) {
            return;
        }
        if (base.getItem() instanceof ModColorCraftItem) {
//            ItemStack dye = danmakuCraftTile.getItem(1);
//            if (!dye.isEmpty()) {
//                ItemStack copy = base.copy();
//                if (dye.isEmpty())
//                    copy = ItemStack.EMPTY;
//                DanmakuColor color = DanmakuColor.getColorFromDye(dye);
//                ModColorItemUtils.setColor(copy, color);
//                danmakuCraftTile.setItem(4, copy);
//            }else danmakuCraftTile.setItem(4, ItemStack.EMPTY);
        }else {
            if (!danmakuCraftTile.isInputEmpty()) {
                ItemStack copy = base.copy();
                ItemStack dye = danmakuCraftTile.getItem(1);
                ItemStack bigP = danmakuCraftTile.getItem(2);

                if (dye.isEmpty() && bigP.isEmpty())
                    copy = ItemStack.EMPTY;
                DanmakuColor color = DanmakuColor.getColorFromDye(dye);
                float damage = getPPtionDamage(bigP);
                DanmakuShotItem.setDanmakuColor(copy, color);
                DanmakuShotItem.setDanmakuDamage(copy, damage);
                danmakuCraftTile.setItem(4, copy);
            }else danmakuCraftTile.setItem(4, ItemStack.EMPTY);
        }
    }

    /**
     * 获取p点伤害
     */
    private static float getPPtionDamage(ItemStack bigP){
        if (bigP.getItem() == ModItems.bigPotion.get()){ //p点
            return 0.5f * bigP.getCount();
        }
        return 0;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.kaguya.danmaku_craft");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new DanmakuCraftMenu(i, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    /**
     * 判断材料格是否有物品
     */
    public boolean isInputEmpty(){
        for (int i = 0; i < 3; i++) {
            if (!stacks.get(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int i) {
        return stacks.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return ContainerHelper.removeItem(stacks, i, i1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(stacks, i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        stacks.set(i, itemStack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        stacks.clear();
    }

    /**
     * 清除输入和基材
     */
    public void clearInputContent(){
        for (int i = 0; i < 4; i++) {
            if (i == 2) stacks.set(i, ItemStack.EMPTY);
            else stacks.get(i).shrink(1);
        }
    }
}
