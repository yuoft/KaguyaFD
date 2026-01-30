package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.GapEntity;
import com.yuo.kaguya.Entity.KaguyaLevelSaveData;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModColorCraftItem;
import com.yuo.kaguya.Item.ModColorItemUtils;
import com.yuo.kaguya.Item.ModNoDataGenItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GapFoldingUmbrella extends KaguyaPrpo implements ModNoDataGenItem, ModColorCraftItem {

    public GapFoldingUmbrella() {
        super(new Properties().durability(30));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Level level = context.getLevel();
        BlockPos spawnPos = clickedPos.relative(clickedFace, 1);
        if (player == null) return InteractionResult.FAIL;
        if (SukimaGap.isSpawn(level, spawnPos) && !SukimaGap.hasGapEntity(level, spawnPos)) {
            GapEntity gap = new GapEntity(level, ModColorItemUtils.getColor(context.getItemInHand()), spawnPos, player.getYRot());
            if (!level.isClientSide) {
                gap.setTicking(true);
                level.addFreshEntity(gap);
                KaguyaLevelSaveData saveData = KaguyaLevelSaveData.get(level);
                saveData.addGapData(gap.getUUID(), spawnPos, gap.getColor(), level.dimension());
            }
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
