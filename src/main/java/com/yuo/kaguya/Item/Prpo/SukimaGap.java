package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.SukimaGapEntity;
import com.yuo.kaguya.Entity.SukimaGapLevelSaveData;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 隙间
 */
public class SukimaGap extends KaguyaPrpo implements ModNoDataGenItem, ModColorCraftItem {

    public SukimaGap() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gapItem = player.getItemInHand(hand);
        if (!player.isCrouching()) return super.use(level, player, hand);

        Entity entity = KaguyaUtils.getHitEntity(level, player, 128);
        if (entity instanceof LivingEntity living) {
            Vec3 look = player.getLookAngle();
            double dx = look.x * 2.0;
            double dz = look.z * 2.0;

            // 计算传送位置：玩家当前位置 + 前方2格
            double tpX = player.getX() + dx;
            double tpZ = player.getZ() + dz;
            double tpY = player.getY();

            if (!level.isClientSide) {
                living.teleportTo(tpX, tpY, tpZ);
                living.hurt(DanmakuDamageTypes.danmaku(player), 1);
            }
        }else { //没看到实体
            Vec3 hitPos = KaguyaUtils.getHitVec(level, player, 128);
            Vec3 playerPos = player.position().add(0, 0.5,0);
            //在玩家位置和视线终点位置分别生成临时隙间
            spawnSukimaGap(level, player, hitPos, gapItem, true);
            spawnSukimaGap(level, player, playerPos, gapItem, true);
        }
        playSoundAndShrinkItem(player, level, gapItem);
        return InteractionResultHolder.success(gapItem);
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
        if (isSpawn(level, pos) && !hasGapEntity(level, pos)) {
            spawnSukimaGap(level, player, spawnPos, context.getItemInHand(), false);
            playSoundAndShrinkItem(player, level, context.getItemInHand());
        }
        return super.useOn(context);
    }

    /**
     * 生成隙间实体
     * 将实体数据添加到数据保存类
     * @param spawnPos 生成坐标
     * @param stack 隙间物品
     * @param isTicking 是否是临时隙间
     */
    public static void spawnSukimaGap(Level level, Player player, Vec3 spawnPos, ItemStack stack, boolean isTicking) {
        SukimaGapEntity gap = new SukimaGapEntity(level, ModColorItemUtils.getColor(stack), spawnPos, player.getYRot());
        gap.setTicking(isTicking);
        if (!level.isClientSide) {
            level.addFreshEntity(gap);
            SukimaGapLevelSaveData saveData = SukimaGapLevelSaveData.get(level);
            saveData.addGapData(gap.getUUID(), spawnPos, player.getYRot(), gap.getColor(), level.dimension(), gap.isTicking());
        }
    }

    /**
     * 播放隙间音效并消耗物品
     * @param stack 待消耗的物品
     */
    private static void playSoundAndShrinkItem(Player player, Level level, ItemStack stack){
        if (level.isClientSide){
            player.playSound(SoundEvents.ENDERMAN_TELEPORT);
        }
        if (!player.getAbilities().instabuild){
            stack.shrink(1);
        }
    }

    //是否可放置实体
    public static boolean isSpawn(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced();
    }

    /**
     * 当前坐标是否存在隙间
     */
    public static boolean hasGapEntity(Level level, BlockPos pos) {
        AABB aabb = new AABB(pos);
        List<SukimaGapEntity> entityList = level.getEntitiesOfClass(SukimaGapEntity.class, aabb);
        return !entityList.isEmpty();
    }

    @Override
    public Component getName(ItemStack stack) {
        return ModColorItemUtils.getColorName(stack, super.getName(stack));
    }
}
