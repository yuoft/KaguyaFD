package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeathScythe extends SwordItem {
    // 配置参数
    private static final double BASE_RANGE = 20.0D;
    private static final double MAX_RANGE = 40.0D;
    private static final double BASE_STRENGTH = 1.0D;
    private static final double MAX_STRENGTH = 3.0D;
    private static final int MAX_CHARGE_TIME = 40; // 2秒最大蓄力

    public DeathScythe() {
        super(Tiers.IRON, 3, -2.6f, new Properties().durability(444));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            float chargeRatio = KaguyaUtils.getChargeRatio(this.getUseDuration(stack), timeLeft, MAX_CHARGE_TIME);
            boolean isPushing = player.isShiftKeyDown();

            if (!level.isClientSide) {
                moveTarget(level, player, isPushing, chargeRatio);

                // 设置冷却（蓄力越久冷却越长）
                int cooldown = (int) (20 + chargeRatio * 40);
                player.getCooldowns().addCooldown(this, cooldown);

                int damageValue = (int) Math.ceil(chargeRatio * 10);
                stack.hurtAndBreak(damageValue, player, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));

                // 播放音效（音调随蓄力变化）
                float pitch = isPushing ? 0.8F + chargeRatio * 0.4F : 1.2F - chargeRatio * 0.4F;
                player.playSound(SoundEvents.ENDER_DRAGON_FLAP, 1.0F, pitch);
            }
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000; // 最大使用时间
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW; // 使用弓的蓄力动画
    }

    /**
     * 蓄力版引力/斥力效果
     */
    private void moveTarget(Level level, Player player, boolean isPushing, float chargeRatio) {
        // 计算蓄力后的参数
        double range = BASE_RANGE + (MAX_RANGE - BASE_RANGE) * chargeRatio;
        double strength = BASE_STRENGTH + (MAX_STRENGTH - BASE_STRENGTH) * chargeRatio;

        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.getEyePosition();
        Vec3 endPos = startPos.add(lookVec.scale(range));

        AABB area = new AABB(startPos, endPos).inflate(2.0 + chargeRatio * 3.0);

        List<Entity> entities = level.getEntities(player, area,
                entity -> isValidTarget(entity, player));

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                moveTargetOne(player, living, isPushing, strength, chargeRatio);

                // 蓄力满时附加效果
                if (chargeRatio >= 0.95F) {
                    applyFullChargeEffect(living, isPushing);
                }
            }
        }

        // 生成蓄力粒子
        spawnChargedParticles(level, player, isPushing, chargeRatio);
    }

    /**
     * 检查实体是否是有效目标
     */
    private boolean isValidTarget(Entity entity, Player player) {
        if (entity == player || !(entity instanceof LivingEntity)) return false;

        if (entity instanceof Player targetPlayer) {
            if (targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                return false;
            }
        }

        // 检查视线是否被阻挡（玩家能看到目标）
        return player.hasLineOfSight(entity);
    }

    /**
     * 蓄力版施力
     */
    private void moveTargetOne(Player player, LivingEntity target, boolean isPushing, double strength, float chargeRatio) {
        Vec3 playerPos = player.getEyePosition();
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);

        Vec3 direction;
        if (isPushing) { //是否潜行
            direction = targetPos.subtract(playerPos).normalize();
        } else {
            direction = playerPos.subtract(targetPos).normalize();
        }

        // 蓄力越久，垂直分量越大
        double verticalAdjust = 0.3 + chargeRatio * 0.25;
        direction = direction.add(0, verticalAdjust, 0).normalize();

        // 添加旋转效果（蓄力时）
        if (chargeRatio > 0.5) {
            double rotationStrength = (chargeRatio - 0.5) * 2.0;
            Vec3 rotation = direction.cross(new Vec3(0, 1, 0)).scale(rotationStrength * 0.1);
            target.setDeltaMovement(target.getDeltaMovement().add(rotation));
        }

        Vec3 force = direction.scale(strength);
        target.setDeltaMovement(target.getDeltaMovement().add(force));

        // 击退保护取消
        target.hurtTime = 0;
        target.invulnerableTime = 0;

        // 播放击中音效
        player.level().playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, target.getSoundSource(), 0.7F, 0.8F + chargeRatio * 0.4F);
    }

    /**
     * 满蓄力附加效果
     */
    private void applyFullChargeEffect(LivingEntity target, boolean isPushing) {
        // 推离满蓄力：击晕效果
        if (isPushing) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        } else {// 拉近满蓄力：虚弱效果
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 1));
        }
    }

    /**
     * 生成蓄力粒子
     */
    private void spawnChargedParticles(Level level, Player player,
                                       boolean isPushing, float chargeRatio) {
        if (!level.isClientSide) {
            Vec3 lookVec = player.getLookAngle();
            Vec3 startPos = player.getEyePosition();

            int particleCount = (int) (20 + chargeRatio * 50);

            for (int i = 0; i < particleCount; i++) {
                double progress = player.getRandom().nextDouble() * chargeRatio;
                Vec3 pos = startPos.add(lookVec.scale(progress * 10));

                double offsetX = (player.getRandom().nextDouble() - 0.5) * 2.0 * (1.0 - progress);
                double offsetY = (player.getRandom().nextDouble() - 0.5) * 2.0 * (1.0 - progress);
                double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2.0 * (1.0 - progress);

                level.addParticle(
                        isPushing ? ParticleTypes.DRAGON_BREATH : ParticleTypes.ENCHANT,
                        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                        1, 0, 0.05 + chargeRatio * 0.1);
            }
        }
    }

}