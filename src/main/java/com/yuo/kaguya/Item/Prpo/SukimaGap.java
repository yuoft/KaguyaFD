package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.GapEntity;
import com.yuo.kaguya.Entity.KaguyaLevelSaveData;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModColorCraftItem;
import com.yuo.kaguya.Item.ModColorItemUtils;
import com.yuo.kaguya.Item.ModNoDataGenItem;
import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class SukimaGap extends KaguyaPrpo implements ModNoDataGenItem, ModColorCraftItem {

    public SukimaGap() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gapItem = player.getItemInHand(hand);
        Entity entity = KaguyaUtils.getHitEntity(player.getEyePosition(), player.getViewVector(1.0f), level, player, 128);
        if (entity instanceof LivingEntity living) {
            BlockPos pos = player.getOnPos();
            if (!level.isClientSide) {
                living.teleportTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                living.hurt(DanmakuDamageTypes.danmaku(null, player), 1);
            }
            if (level.isClientSide){
                player.playSound(SoundEvents.ENDERMAN_TELEPORT);
            }
            if (!player.getAbilities().instabuild){
                gapItem.shrink(1);
            }
            return InteractionResultHolder.success(gapItem);
        }

        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Level level = context.getLevel();
        BlockPos spawnPos = clickedPos.relative(clickedFace, 1);
        if (player == null) return InteractionResult.FAIL;
        if (isSpawn(level, spawnPos) && !hasGapEntity(level, spawnPos)) {
            GapEntity gap = new GapEntity(level, ModColorItemUtils.getColor(context.getItemInHand()), spawnPos, player.getYRot());
            if (!level.isClientSide) {
                level.addFreshEntity(gap);
                KaguyaLevelSaveData saveData = KaguyaLevelSaveData.get(level);
                saveData.addPos(gap.getUUID(), spawnPos);
            }
            if (level.isClientSide){
                player.playSound(SoundEvents.ENDERMAN_TELEPORT);
            }
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return super.useOn(context);
    }

    //是否可放置实体
    private static boolean isSpawn(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced();
    }

    /**
     * 当前坐标是否存在隙间
     */
    private static boolean hasGapEntity(Level level, BlockPos pos) {
        AABB aabb = new AABB(pos);
        List<GapEntity> entityList = level.getEntitiesOfClass(GapEntity.class, aabb);
        return !entityList.isEmpty();
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
