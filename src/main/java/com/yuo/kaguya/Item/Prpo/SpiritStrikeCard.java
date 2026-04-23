package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SpiritStrikeCard extends KaguyaPrpo {
    private static final double RADIUS = 12.0;
    private static final double KNOCKBACK_STRENGTH = 2.0;
    public SpiritStrikeCard() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // 获取周围范围内的所有敌对生物
            AABB area = player.getBoundingBox().inflate(RADIUS);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != player && e.isAlive() && (e instanceof Enemy || !e.getType().getCategory().isFriendly()));

            if (!entities.isEmpty()) {
                Vec3 playerPos = player.position();

                for (LivingEntity target : entities) {
                    Vec3 direction = target.position().subtract(playerPos).normalize();
                    Vec3 knockback = direction.scale(KNOCKBACK_STRENGTH).add(0, 0.4, 0);

                    target.setDeltaMovement(target.getDeltaMovement().add(knockback));
                    target.hurtMarked = true; // 标记为受伤，确保客户端同步
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.8F);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.8F, 1.2F);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                player.getCooldowns().addCooldown(this, 20);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
