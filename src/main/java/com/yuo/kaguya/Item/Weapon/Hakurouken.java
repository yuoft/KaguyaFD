package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Entity.ReboundEntity;
import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Hakurouken extends KaguyaWeapon {
    public Hakurouken() {
        super(Tiers.WOOD, new Properties().durability(100));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.isCrouching()){
            performCharge(level, player);
            player.getCooldowns().addCooldown(this, 5);
        }else {
            DanmakuShootHelper.shootDanmakuRebound(level, player, DanmakuShootHelper.VAL_DEF / 4, DanmakuShootHelper.INA_DEF);
        }
        return super.use(level, player, hand);
    }

    private void performCharge(Level level, Player player) {
        // 计算冲锋参数
        float distance = 5.0f;
        float speed = 1.5f;

        // 获取冲锋方向（玩家视线方向）
        Vec3 lookVec = player.getLookAngle().normalize().reverse();

        // 设置玩家速度
        Vec3 velocity = lookVec.scale(speed);
        player.setDeltaMovement(velocity);
        player.fallDistance = 0;

        // 应用冲锋效果
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 2, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 1, false, false));

        Roukanken.spawnChargeParticles(level, player, lookVec, distance, 0.51f);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.0F, 0.7F);
    }
}
