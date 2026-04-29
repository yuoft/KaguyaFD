package com.yuo.kaguya.Item;

import com.yuo.kaguya.Entity.Danmaku.DanmakuColor;
import com.yuo.kaguya.Entity.Danmaku.DanmakuShootHelper;
import com.yuo.kaguya.Entity.Danmaku.DanmakuType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.yuo.kaguya.Entity.Danmaku.DanmakuColor.*;

public class DanmakuShotItem extends Item {
    private static final float VAL_DEF = 0.5f; //默认发射初速度
    private static final float INA_DEF = .1f; //默认发射偏移量
    public static final String NBT_DANMAKU_COLOR = "DanmakuColor";
    public static final String NBT_DANMAKU_DAMAGE = "DanmakuDamage";
    private final DanmakuType danmakuType;
    private DanmakuColor danmakuColor;

    public DanmakuShotItem(DanmakuType type) {
        super(new Properties().stacksTo(64));
        this.danmakuType = type;
        this.danmakuColor = GRAY;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = new CompoundTag();
        tag.putString(NBT_DANMAKU_COLOR, danmakuColor.getName());
        tag.putFloat(NBT_DANMAKU_DAMAGE, danmakuType.getDamage());
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        String color = tag.getString(NBT_DANMAKU_COLOR);
        if (!color.isEmpty()){
            float damage = tag.getFloat(NBT_DANMAKU_DAMAGE);
            components.add(Component.translatable("info.kaguya.danmaku_shot_item.color").append(Component.translatable("info.kaguya.color." + color)));
            components.add(Component.translatable("info.kaguya.danmaku_shot_item.damage", damage));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide())  return InteractionResultHolder.fail(stack);
        String color = stack.getOrCreateTag().getString(NBT_DANMAKU_COLOR);
        DanmakuType danmakuType = this.danmakuType;
        DanmakuColor danmakuColor = DanmakuColor.getColor(color);
        switch (danmakuType) {
            case BUTTER_FLY -> DanmakuShootHelper.shootDanmakuFly(world, player, VAL_DEF / 2, INA_DEF, danmakuColor);
            case ARROW_SHOT -> DanmakuShootHelper.shootDanmakuArrow(world, player, VAL_DEF, INA_DEF, danmakuColor);
            case LONG_LASER, MIDDLE_LASER, SHORT_LASER ->
                    DanmakuShootHelper.shootDanmakuLaser(world, player, VAL_DEF, INA_DEF, danmakuType, danmakuColor);
            default -> DanmakuShootHelper.shootDanmaku(world, player, VAL_DEF, INA_DEF, danmakuColor, danmakuType);
        }

        player.getCooldowns().addCooldown(this, 10);
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        CompoundTag tag = stack.getOrCreateTag();
        String color = tag.getString(NBT_DANMAKU_COLOR);
        if (color.isEmpty()){
            tag.putString(NBT_DANMAKU_COLOR, GRAY.getName());
            tag.putFloat(NBT_DANMAKU_DAMAGE, danmakuType.getDamage());
            stack.setTag(tag);
        }
    }

    public DanmakuType getDanmakuType() {
        return danmakuType;
    }

    public DanmakuColor getDanmakuColor() {
        return danmakuColor;
    }

    public static int getColor(ItemStack stack, int i) {
        String color = stack.getOrCreateTag().getString(NBT_DANMAKU_COLOR);
        if (!color.isEmpty()){
            return i > 0 ? -1 : DanmakuColor.getColor(color).getRgb();
        }
        return i > 0 ?  -1 : GRAY.getRgb();
    }

    public static void setDanmakuColor(ItemStack stack, DanmakuColor color) {
        if (color == null) return;
        ((DanmakuShotItem) stack.getItem()).danmakuColor = color;
        stack.getOrCreateTag().putString(NBT_DANMAKU_COLOR, color.getName());
    }

    public static void setDanmakuDamage(ItemStack stack, float damage) {
        if (damage == 0) return;
        CompoundTag tag = stack.getOrCreateTag();
        float oldDamage = tag.getFloat(NBT_DANMAKU_DAMAGE);
        tag.putFloat(NBT_DANMAKU_DAMAGE, oldDamage + damage);
        stack.setTag(tag);
    }
}
