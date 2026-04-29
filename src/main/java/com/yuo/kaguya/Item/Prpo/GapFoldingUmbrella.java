package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.SukimaGapEntity;
import com.yuo.kaguya.Entity.SukimaGapLevelSaveData;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModColorCraftItem;
import com.yuo.kaguya.Item.ModColorItemUtils;
import com.yuo.kaguya.Item.ModNoDataGenItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 隙间的折叠伞
 */
public class GapFoldingUmbrella extends KaguyaPrpo implements ModNoDataGenItem, ModColorCraftItem {

    public GapFoldingUmbrella() {
        super(new Properties().durability(30));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()){
            ModColorItemUtils.setColor(stack, ModColorItemUtils.getColor(stack).next());
            player.swing(hand);
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Level level = context.getLevel();
        BlockPos pos = clickedPos.relative(clickedFace, 1);
        Vec3 spawnPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        if (player == null) return InteractionResult.FAIL;
        if (SukimaGap.isSpawn(level, pos) && !SukimaGap.hasGapEntity(level, pos)) {
            SukimaGap.spawnSukimaGap(level, player, spawnPos, context.getItemInHand(), true);
            if (level.isClientSide){
                player.playSound(SoundEvents.ENDERMAN_TELEPORT);
            }
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
            }
        }
        return super.useOn(context);
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
