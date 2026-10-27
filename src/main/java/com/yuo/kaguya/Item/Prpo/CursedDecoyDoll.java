package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 诅咒人偶
 */
public class CursedDecoyDoll extends KaguyaPrpo {
    /** 存在时间：5 分钟 = 6000 tick */
    private static final int LIFETIME_TICKS = 20 * 60 * 5;

    public CursedDecoyDoll() {
        super(new Properties().durability(30));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            Wolf wolf = EntityType.WOLF.create(level);
            if (wolf != null) {
                // ── 1. 放置在玩家前方 1.5 格 ──
                Vec3 look = player.getLookAngle();
                double x = player.getX() + look.x * 1.5;
                double y = player.getY();
                double z = player.getZ() + look.z * 1.5;
                wolf.moveTo(x, y, z, player.getYRot(), 0.0F);

                // ── 2. 驯服，主人为使用玩家 ──
                // 注意：必须在乘属性之前 tame，因为原版 setTame 会把生命/攻击重置为 20/4
                wolf.tame(player);

                CompoundTag nbt = wolf.getPersistentData(); // 使用 persistentData 不会被其他逻辑覆盖
                nbt.putInt("CursedDecoyDespawnTimer", LIFETIME_TICKS); // 6000 ticks
                nbt.putBoolean("IsCursedDecoyWolf", true);
                nbt.putLong("CursedDecoySpawnTime", level.getGameTime());   // ← 新增：召唤时间戳

                wolf.setCustomName(Component.translatable("info.kaguya.cursed_decoy_doll.wolf_name"));
                wolf.setCustomNameVisible(true);

                // ── 4. 属性 ×5 ──
                multiplyAttribute(wolf, Attributes.MAX_HEALTH, 5.0);
                multiplyAttribute(wolf, Attributes.ATTACK_DAMAGE, 5.0);
                multiplyAttribute(wolf, Attributes.MOVEMENT_SPEED, 1.2);
                multiplyAttribute(wolf, Attributes.FOLLOW_RANGE, 2.0);
                multiplyAttribute(wolf, Attributes.ARMOR, 5.0);
                // 生命值拉满（MAX_HEALTH 修改后需手动 setHealth）
                wolf.setHealth(wolf.getMaxHealth());
                level.addFreshEntity(wolf);

                // ── 7. 反馈特效 ──
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            x, y + 0.5, z, 15, 0.3, 0.5, 0.3, 0.02);
                }
                level.playSound(null, x, y, z,
                        SoundEvents.WOLF_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.7F);
            }

            player.getCooldowns().addCooldown(this, 20);
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /** 将实体某项属性的基础值乘以 multiplier */
    private static void multiplyAttribute(LivingEntity entity, Attribute attribute, double multiplier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * multiplier);
        }
    }
}