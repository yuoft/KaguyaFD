package com.yuo.kaguya.Item.Weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yuo.kaguya.Entity.*;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class WindStick extends SwordItem {
    public WindStick() {
        super(Tiers.DIAMOND, 0, -2.4f, new Properties().durability(50));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            int exp = player.experienceLevel;
            float damage = (float) Math.floor((exp + 2) / 5f);
            DanmakuShootHelper.shootDanmakuWind(level, player, 0.25f, 0.1f, DanmakuColor.GRAY, damage);
        } else {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        int useDuration = this.getUseDuration(stack) - timeLeft;
        float chargeRatio = Math.min((float) useDuration / 40, 1.0F);

        if (chargeRatio < 0.2f) return;

        // 固定生成距离（不随蓄力变化）
        float spawnDistance = 3.0F;

        // 计算生成位置
        Vec3 lookVec = living.getLookAngle().normalize();
        Vec3 eyePos = living.getEyePosition();
        Vec3 spawnPos = eyePos.add(lookVec.scale(spawnDistance));

        // 简单的防穿墙：射线检测
        HitResult hitResult = level.clip(new ClipContext(eyePos, spawnPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, living));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            // 如果视线被阻挡，在阻挡的方块面前生成
            spawnPos = hitResult.getLocation();
        }

        // 调整到地面
        BlockPos blockPos = BlockPos.containing(spawnPos);
        MiracleCircle miracleCircle = new MiracleCircle(level, 1, blockPos);
        miracleCircle.setYRot(living.getYRot() + 180.0F);
        level.addFreshEntity(miracleCircle);

        if (living instanceof Player player)
            player.getCooldowns().addCooldown(this, 20);
        if (!level.isClientSide()) {
            level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}
