package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.GapEntity;
import com.yuo.kaguya.Entity.KaguyaLevelSaveData;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class SukimaGap extends KaguyaPrpo {
    public static final String NBT_COLOR = "kaguya:gap_color";

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
            GapEntity gap = new GapEntity(level, null, spawnPos, player.getYRot());
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

    /**
     * 创建带有颜色的物品堆栈
     * @param color 颜色
     */
    public static ItemStack createColoredStack(Item item, DyeColor color) {
        ItemStack stack = new ItemStack(item);
        setColor(stack, color);
        return stack;
    }

    /**
     * 设置颜色到NBT
     */
    public static void setColor(ItemStack stack, DyeColor color) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_COLOR, color.name());
        stack.setTag(tag);
    }

    /**
     * 从NBT获取颜色
     */
    public static DyeColor getColor(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBT_COLOR)) {
            String color = tag.getString(NBT_COLOR);
            return DyeColor.byName(color, DyeColor.GRAY);
        }
        return null;
    }

    public static int getColor(ItemStack stack, int layer) {
        if (hasColor(stack) && layer == 1) {
            return getColor(stack).getTextColor();
        }
        return -1;
    }

    /**
     * 是否有颜色tag
     */
    public static boolean hasColor(ItemStack stack) {
        return stack.getOrCreateTag().contains(NBT_COLOR);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (hasColor(stack)) {
            DyeColor color = getColor(stack);
            if (color != null) {
                MutableComponent colorName = Component.translatable("info.kaguya.color." + color.getName()).append("-");
                return colorName.append(super.getName(stack));
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (hasColor(stack)) {
            DyeColor color = getColor(stack);
            tooltip.add(Component.translatable("info.kaguya.gap.color").append(Component.translatable("info.kaguya.color." + color.getName())));
        }
    }
}
