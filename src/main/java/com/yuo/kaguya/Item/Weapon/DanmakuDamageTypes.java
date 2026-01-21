package com.yuo.kaguya.Item.Weapon;

import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DanmakuDamageTypes {
    private static final String typeName = "danmaku";
    public static DamageType damageType = new DamageType(typeName, 0.0f);

    public static final ResourceKey<DamageType> DANMAKU = ResourceKey.create(Registries.DAMAGE_TYPE, KaguyaUtils.fa("danmaku"));

    public static final RegistrySetBuilder DAMAGE_BUILDER = new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, DanmakuDamageTypes::bootstrap);

    public static HolderLookup.Provider append(HolderLookup.Provider original) {
        return DAMAGE_BUILDER.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
    }

    // 注册
    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(DANMAKU, damageType);
    }

    /**
     * 弹幕伤害
     * @param projectile 弹幕实体
     * @param owner 发射弹幕实体
     */
    public static DanmakuDamageSource danmaku(Entity projectile, @Nullable LivingEntity owner) {
        Holder<DamageType> holder = owner.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(DANMAKU).get();
        return new DanmakuDamageSource(holder, projectile, owner);
    }

    public static DanmakuDamageSource danmaku(@Nullable LivingEntity owner) {
        Holder<DamageType> holder = owner.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(DANMAKU).get();
        return new DanmakuDamageSource(holder, owner);
    }

    public static boolean isDanmaku(DamageSource source){
        if (source instanceof DanmakuDamageSource) return true;
        return source.getMsgId().equals(typeName);
    }

    public static class DanmakuDamageSource extends DamageSource{

        public DanmakuDamageSource(Holder<DamageType> holder, Entity projectile, LivingEntity owner) {
            super(holder, projectile, owner);
        }

        public DanmakuDamageSource(Holder<DamageType> holder, LivingEntity owner) {
            super(holder, owner);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity livingEntity) {
            String deathMessage = "info.kaguya.damage_source.danmaku";
            if (this.getEntity() != null) {
                deathMessage = "info.kaguya.damage_source.danmaku_player";
                return Component.translatable(deathMessage, livingEntity.getDisplayName(), this.getEntity().getDisplayName());
            }
            return Component.translatable(deathMessage, livingEntity.getDisplayName());
        }

        //是否根据难度缩放伤害值
        @Override
        public boolean scalesWithDifficulty() {
            return false;
        }
    }
}
