package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Roukanken extends SwordItem {
    private static final float BASE_DAMAGE = 5.0F;      // 基础伤害
    private static final float BASE_DISTANCE = 5.0F;    // 基础冲刺距离
    private static final float BASE_SPEED = 1.5F;       // 基础冲刺速度
    private static final float HIT_WIDTH = 2.0F;        // 命中宽度

    public Roukanken() {
        super(Tiers.IRON, 3, -2.4f, new Properties().durability(251));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            return state.is(BlockTags.SWORD_EFFICIENT) ? 6.5F : 6.0F;
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (level.isClientSide) {
            // 客户端：播放开始蓄力音效
            level.playSound(player, player.blockPosition(), SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 0.5F, 0.8F);
        }

        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            float chargeRatio = KaguyaUtils.getChargeRatio(this.getUseDuration(stack), timeLeft, 40);
            if (chargeRatio >= 0.2f) {
                // 执行冲锋
                performCharge(level, player, chargeRatio);
                player.getCooldowns().addCooldown(this, 20);

                // 消耗耐久度
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
            }

            // 客户端效果
            if (level.isClientSide) {
                playReleaseEffects(level, player, chargeRatio);
            }
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    /**
     * 执行冲锋
     */
    private void performCharge(Level level, Player player, float chargeRatio) {
        // 计算冲锋参数
        float distance = BASE_DISTANCE + 10f * chargeRatio;
        float speed = BASE_SPEED + 3f * chargeRatio;
        float damage = BASE_DAMAGE + 10f * chargeRatio;

        // 获取冲锋方向（玩家视线方向）
        Vec3 lookVec = player.getLookAngle().normalize();

        // 设置玩家速度
        Vec3 velocity = lookVec.scale(speed);
        player.setDeltaMovement(velocity);
        player.fallDistance = 0;

        // 应用冲锋效果
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 2, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 1, false, false));

        // 检查路径上的实体
        checkPathForEntities(level, player, lookVec, distance, damage, chargeRatio);

        spawnChargeParticles(level, player, lookVec, distance, chargeRatio);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.0F, 0.7F + chargeRatio * 0.6F);
    }

    /**
     * 检查冲锋路径上的实体
     */
    private void checkPathForEntities(Level level, Player player, Vec3 direction, float maxDistance, float damage, float chargeRatio) {
        Vec3 startPos = player.position().add(0, player.getBbHeight() * 0.5, 0);
        Vec3 endPos = startPos.add(direction.scale(maxDistance));

        AABB pathBox = new AABB(startPos, endPos).inflate(HIT_WIDTH, player.getBbHeight() * 0.5, HIT_WIDTH);
        List<Entity> entities = level.getEntities(player, pathBox, entity -> canHitEntity(entity, player));

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity target) {
                // 计算命中位置
                Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                Vec3 pathToTarget = targetPos.subtract(startPos);

                // 检查是否在路径方向上（点积判断）
                double dot = direction.dot(pathToTarget.normalize());
                if (dot > 0.8) { // 在正前方
                    hitEntity(player, target, damage, chargeRatio, direction);
                }
            }
        }
    }

    /**
     * 击中实体
     */
    private void hitEntity(Player player, LivingEntity target, float damage, float chargeRatio, Vec3 direction) {
        // 造成伤害
        DamageSource damageSource = player.damageSources().playerAttack(player);
        target.hurt(damageSource, damage);

        // 击退效果
        Vec3 knockback = direction.scale(0.5 + chargeRatio * 0.5);
        target.setDeltaMovement(target.getDeltaMovement().add(knockback));
        target.hurtMarked = true;

        // 附加效果（蓄力越满效果越强）
        if (chargeRatio > 0.7F) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (40 * chargeRatio), (int) (chargeRatio * 2), false, false));
        }

        if (chargeRatio > 0.9F) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int) (60 * chargeRatio), 1, false, false));
        }

        spawnHitParticles(player.level(), target);
        player.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 0.8F + chargeRatio * 0.4F);
    }

    /**
     * 检查是否可以击中实体
     */
    private boolean canHitEntity(Entity entity, Player player) {
        if (entity == player) return false;
        if (!(entity instanceof LivingEntity living)) return false;
        // 排除死亡或无效的生物
        if (!living.isAlive() || living.isRemoved()) return false;
        // 排除创造模式玩家和旁观者
        if (entity instanceof Player targetPlayer) {
            return !targetPlayer.isCreative() && !targetPlayer.isSpectator();
        }

        return true;
    }

    /**
     * 生成冲锋粒子效果
     */
    private void spawnChargeParticles(Level level, Player player, Vec3 direction, float distance, float chargeRatio) {
        if (!level.isClientSide) return;

        Vec3 startPos = player.position().add(0, 0.5, 0);

        // 沿着路径生成粒子
        int particleCount = (int) (20 + chargeRatio * 30);
        for (int i = 0; i < particleCount; i++) {
            float progress = (float) i / particleCount;
            Vec3 pos = startPos.add(direction.scale(progress * distance));

            // 添加随机偏移
            double offsetX = (player.getRandom().nextDouble() - 0.5) * HIT_WIDTH;
            double offsetY = (player.getRandom().nextDouble() - 0.5) * 1.0;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * HIT_WIDTH;

            // 冲锋轨迹粒子
            level.addParticle(ParticleTypes.SWEEP_ATTACK, pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ, 1, 0, 0);

            // 能量粒子
            if (chargeRatio > 0.5F) {
                level.addParticle(ParticleTypes.ENCHANT, pos.x, pos.y + 0.5, pos.z, 1, 0, 0.1);
            }
        }
    }

    /**
     * 生成击中粒子效果
     */
    private void spawnHitParticles(Level level, LivingEntity target) {
        if (!level.isClientSide) return;

        Vec3 pos = target.position().add(0, target.getBbHeight() * 0.5, 0);

        for (int i = 0; i < 10; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * target.getBbWidth();
            double offsetY = level.random.nextDouble() * target.getBbHeight();
            double offsetZ = (level.random.nextDouble() - 0.5) * target.getBbWidth();

            level.addParticle(ParticleTypes.CRIT, pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ, 1, 0, 0.2);
        }
    }

    /**
     * 播放释放效果（客户端）
     */
    private void playReleaseEffects(Level level, Player player, float chargeRatio) {
        if (chargeRatio >= 0.2f) {
            // 冲锋音效
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_3, SoundSource.PLAYERS, 1.0F, 0.8F + chargeRatio * 0.4F, false);

            // 屏幕震动效果
            if (chargeRatio > 0.8F) {
                player.hurtMarked = true;
            }
        } else {
            // 蓄力不足音效
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.5F, 1.0F, false);
        }
    }
}
