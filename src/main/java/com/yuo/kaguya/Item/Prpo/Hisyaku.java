package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModNoDataGenItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Hisyaku extends KaguyaPrpo implements ModNoDataGenItem {
    private static final String NBT_WATER = "kaguya:water";
    public static final String NBT_WATER_NUM = "kaguya:water_number";

    public Hisyaku() {
        super(new Properties().stacksTo(1).durability(60));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBT_WATER)) {
            boolean b = tag.getBoolean(NBT_WATER);
            if (b) components.add(Component.translatable("info.kaguya.hisyaku.true"));
            else components.add(Component.translatable("info.kaguya.hisyaku.false"));
        }
        if (getWaterNumber(stack) > 0) {
            int num = tag.getInt(NBT_WATER_NUM);
            components.add(Component.translatable("info.kaguya.hisyaku.water_num", num));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player.isCrouching()) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains(NBT_WATER)) {
                tag.putBoolean(NBT_WATER, true);
            }else tag.putBoolean(NBT_WATER, !tag.getBoolean(NBT_WATER));
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            ItemStack stack = context.getItemInHand();
            Player player = context.getPlayer();
            if (player == null) return super.useOn(context);
            Direction clickedFace = context.getClickedFace();
            BlockPos pos = clickedPos.relative(clickedFace);

            if (isWater(stack)){ //吸水
                FluidState state = level.getFluidState(pos);
                if (!state.isEmpty() && state.getType() == Fluids.WATER){
                    addWater(stack, 1);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    player.playSound(SoundEvents.BUCKET_FILL);
                    return InteractionResult.SUCCESS;
                }
            }else { //放水
                int waterNumber = getWaterNumber(stack);
                if (waterNumber > 0){
                    addWater(stack, -1);
                    level.setBlock(pos, Fluids.WATER.defaultFluidState().createLegacyBlock(), 3);
                    if (!player.getAbilities().instabuild){
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                    }
                    player.playSound(SoundEvents.BUCKET_EMPTY);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(context);
    }

    private static boolean isWater(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_WATER);
    }

    private static int getWaterNumber(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_WATER_NUM);
    }

    private static void addWater(ItemStack stack, int num) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(NBT_WATER_NUM, tag.getInt(NBT_WATER_NUM) + num);
    }
}
