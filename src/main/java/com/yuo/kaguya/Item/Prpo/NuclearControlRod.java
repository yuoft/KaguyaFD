package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.BigOrbEntity;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Item.KaguyaWeapon;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;

public class NuclearControlRod extends KaguyaWeapon {
    private static final String NBT_ORB_ID = "big_rod_entity_id";

    public NuclearControlRod() {
        super(Tiers.IRON, 0, new Properties().durability(36));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 开始使用物品时创建实体
        if (!level.isClientSide) {
            BigOrbEntity bigOrb = new BigOrbEntity(level, player, 0.1f);

            // 设置实体的位置（在玩家面前）
            Vec3 lookVec = player.getLookAngle();
            Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(0.5));
            bigOrb.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            level.addFreshEntity(bigOrb);

            // 将实体与玩家绑定，用于后续释放
            player.getPersistentData().putInt(NBT_ORB_ID, bigOrb.getId());
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide && living instanceof Player player) {
            // 获取之前创建的实体
            int entityId = player.getPersistentData().getInt(NBT_ORB_ID);
            if (entityId != 0) {
                BigOrbEntity bigOrb = (BigOrbEntity) level.getEntity(entityId);
                if (bigOrb != null) {
                    // 持续更新实体的位置，跟随玩家视角
                    Vec3 lookVec = player.getLookAngle();
                    Vec3 targetPos = player.getEyePosition().add(lookVec.scale(0.5));
                    bigOrb.setPos(targetPos.x, targetPos.y, targetPos.z);

                    // 设置实体的旋转角度，朝向玩家视角方向
                    bigOrb.setYRot(player.getYRot());
                    bigOrb.setXRot(player.getXRot());

                    // 更新蓄力进度（用于视觉表现）
                    float chargeRatio = KaguyaUtils.getChargeRatio(this.getUseDuration(stack), remainingUseDuration);
                    bigOrb.setSize(chargeRatio);
                }
            }
        }
        super.onUseTick(level, living, stack, remainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            float chargeRatio = KaguyaUtils.getChargeRatio(this.getUseDuration(stack), timeLeft);

            // 获取之前创建的实体
            int entityId = player.getPersistentData().getInt(NBT_ORB_ID);
            player.getPersistentData().remove(NBT_ORB_ID);

            if (!level.isClientSide) {
                BigOrbEntity bigOrb = (BigOrbEntity) level.getEntity(entityId);

                if (bigOrb != null) {
                    // 或者：移除实体并发射弹幕
                    bigOrb.discard();
                    DanmakuShootHelper.shootDanmakuBigOrb(level, player, DanmakuShootHelper.VAL_DEF / 4, DanmakuShootHelper.INA_DEF, chargeRatio);
                }
                player.getCooldowns().addCooldown(this, 20);
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity living, int count) {
        // 如果玩家取消使用（比如切换物品），需要清理实体
        Level level = living.level();

        if (!level.isClientSide && living instanceof Player player) {
            int entityId = player.getPersistentData().getInt(NBT_ORB_ID);
            if (entityId != 0) {
                BigOrbEntity bigOrb = (BigOrbEntity) level.getEntity(entityId);
                if (bigOrb != null) {
                    BlockPos pos = bigOrb.getOnPos();
                    level.explode(bigOrb, pos.getX(), pos.getY(), pos.getZ(), bigOrb.getSize() * 10f, ExplosionInteraction.MOB);
                    bigOrb.discard();
                }
                player.getPersistentData().remove(NBT_ORB_ID);
            }
        }
        super.onStopUsing(stack, living, count);
    }
}
