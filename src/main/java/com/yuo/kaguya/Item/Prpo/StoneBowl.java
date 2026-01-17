package com.yuo.kaguya.Item.Prpo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;

import java.util.function.Consumer;

public class StoneBowl extends PickaxeItem {
    public StoneBowl() {
        super(Tiers.DIAMOND, 1, -2.8f, new Properties().stacksTo(1).durability(Integer.MAX_VALUE));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 6.0f;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity living, LivingEntity livingEntity) {
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return TierSortingRegistry.isCorrectTierForDrops(this.getTier(), state);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return true;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }
}
