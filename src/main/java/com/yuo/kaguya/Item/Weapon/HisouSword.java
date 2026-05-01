package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 绯想之剑
 */
public class HisouSword extends KaguyaWeapon {
    private Entity targetEntity;  // 用于记录第一次攻击的实体（原逻辑保留）

    public HisouSword() {
        super(Tiers.NETHERITE, 5, new Properties().defaultDurability(200).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        if (targetEntity != null) {
            components.add(Component.translatable("info.kaguya.hisou_sword").append(targetEntity.getDisplayName()));
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity mob, LivingEntity player) {
        if (targetEntity == null) {
            targetEntity = mob;
        } else {
            mob.hurt(player.damageSources().playerAttack((Player) player), this.getDamage() * 2);
        }
        return super.hurtEnemy(stack, mob, player);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.experienceLevel >= 5 || player.isCreative()) {
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    player.giveExperienceLevels(-5);
                    stack.hurtAndBreak(5, player, (p) -> p.broadcastBreakEvent(hand));
                }
                performShockwave(level, player);
                player.getCooldowns().addCooldown(this, 200);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    /**
     * 执行扇形冲击波
     * @param level   世界
     * @param shooter 施法者（玩家）
     */
    private void performShockwave(Level level, LivingEntity shooter) {
        // 参数配置
        double maxDistance = 32.0;          // 最大作用距离（格）
        double startRadius = 0.5;           // 起始半径（格）
        double endRadius = startRadius + maxDistance * 0.5;             // 末端半径（格）
        float damage = 12.0f;               // 对实体的直接伤害
        float knockback = 1.5f;             // 击退力度
        int explosionCount = 8;             // 额外随机爆炸数量
        float explosionPower = 2.5f;        // 爆炸威力
        boolean causeFire = false;          // 是否引发火焰

        Vec3 origin = shooter.getEyePosition();
        Vec3 direction = shooter.getLookAngle();

        // 用于存储所有位于扇形内的方块位置
        List<BlockPos> affectedPositions = new ArrayList<>();

        // 1. 破坏扇形区域内的所有方块，并收集位置
        AABB rangeBox = new AABB(origin, origin).inflate(maxDistance);
        BlockPos.betweenClosedStream(rangeBox).forEach(pos -> {
            Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            double dist = blockCenter.distanceTo(origin);
            if (dist > maxDistance) return;

            Vec3 toBlock = blockCenter.subtract(origin).normalize();
            double dot = direction.dot(toBlock);
            double angle = Math.acos(dot);
            double currentRadius = startRadius + (endRadius - startRadius) * (dist / maxDistance);
            double maxAngle = Math.atan2(currentRadius, dist);

            if (angle <= maxAngle) {
                BlockState state = level.getBlockState(pos);
                float hardness = state.getDestroySpeed(level, pos);
                // 硬度 >= 50 的方块不破坏（如黑曜石），也不加入爆炸点列表
                if (hardness >= 50.0F || hardness < 0) return;
                affectedPositions.add(pos.immutable());
                // 破坏方块，生成掉落物
                level.destroyBlock(pos, true);
                // 爆炸粒子效果
                level.addParticle(ParticleTypes.EXPLOSION,
                        blockCenter.x, blockCenter.y, blockCenter.z, 0, 0, 0);
                for (int i = 0; i < 3; i++) {
                    level.addParticle(ParticleTypes.POOF,
                            blockCenter.x + (level.random.nextDouble() - 0.5) * 0.5,
                            blockCenter.y + (level.random.nextDouble() - 0.5) * 0.5,
                            blockCenter.z + (level.random.nextDouble() - 0.5) * 0.5,
                            0, 0, 0);
                }
            }
        });

        // 2. 在已被破坏的方块位置中随机生成真实爆炸（不破坏方块，但产生冲击波和伤害）
        if (!affectedPositions.isEmpty()) {
            Collections.shuffle(affectedPositions);
            int take = Math.min(explosionCount, affectedPositions.size());
            for (int i = 0; i < take; i++) {
                BlockPos boomPos = affectedPositions.get(i);
                level.explode(shooter, boomPos.getX() + 0.5, boomPos.getY() + 0.5, boomPos.getZ() + 0.5,
                        explosionPower, causeFire, Level.ExplosionInteraction.TNT);
            }
        }

        // 3. 对扇形内的实体造成直接伤害与击退（独立于爆炸）
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, rangeBox,
                e -> e != shooter && e.isAlive());
        for (Entity target : entities) {
            Vec3 targetCenter = target.position().add(0, target.getBbHeight() / 2, 0);
            double dist = targetCenter.distanceTo(origin);
            if (dist > maxDistance) continue;

            Vec3 toTarget = targetCenter.subtract(origin).normalize();
            double dot = direction.dot(toTarget);
            double angle = Math.acos(dot);
            double currentRadius = startRadius + (endRadius - startRadius) * (dist / maxDistance);
            double maxAngle = Math.atan2(currentRadius, dist);

            if (angle <= maxAngle) {
                if (target instanceof LivingEntity living) {
                    living.hurt(shooter.damageSources().indirectMagic(shooter, shooter), damage);
                    Vec3 knockbackVec = toTarget.scale(knockback);
                    living.setDeltaMovement(living.getDeltaMovement().add(knockbackVec));
                    living.hurtMarked = true;
                    // 受击粒子
                    for (int i = 0; i < 5; i++) {
                        level.addParticle(ParticleTypes.SWEEP_ATTACK,
                                targetCenter.x, targetCenter.y, targetCenter.z,
                                (level.random.nextDouble() - 0.5) * 0.5,
                                (level.random.nextDouble() - 0.5) * 0.5,
                                (level.random.nextDouble() - 0.5) * 0.5);
                    }
                }
            }
        }

        // 4. 冲击波轨迹粒子
        for (double d = 0; d <= maxDistance; d += 0.4) {
            Vec3 point = origin.add(direction.scale(d));
            level.addParticle(ParticleTypes.SWEEP_ATTACK, point.x, point.y, point.z, 0, 0, 0);
            double rad = startRadius + (endRadius - startRadius) * (d / maxDistance);
            for (int i = 0; i < 3; i++) {
                double offX = (level.random.nextDouble() - 0.5) * rad;
                double offY = (level.random.nextDouble() - 0.5) * rad;
                double offZ = (level.random.nextDouble() - 0.5) * rad;
                level.addParticle(ParticleTypes.LARGE_SMOKE,
                        point.x + offX, point.y + offY, point.z + offZ, 0, 0, 0);
            }
        }

        // 起点大爆炸粒子
        level.addParticle(ParticleTypes.EXPLOSION_EMITTER, origin.x, origin.y, origin.z, 0, 0, 0);
    }
}