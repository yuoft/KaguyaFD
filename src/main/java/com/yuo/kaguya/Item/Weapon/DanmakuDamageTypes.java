package com.yuo.kaguya.Item.Weapon;

import com.mojang.datafixers.util.Either;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
        return new DanmakuDamageSource(projectile, owner);
    }

    public static boolean isDanmaku(DamageSource source){
        if (source instanceof DanmakuDamageSource) return true;
        return source.getMsgId().equals(typeName);
    }

    // 缓存Holder.Reference，避免重复创建
    @Nullable
    private static Holder.Reference<DamageType> cachedHolder = null;

    // 获取DamageType的Holder
    private static Holder.Reference<DamageType> getHolder() {
        if (cachedHolder == null) {
            // 使用createStandAlone创建标准的Holder.Reference
            // 注意：这需要在注册表上下文中使用
            // 在实际使用中，您需要传入正确的HolderOwner
            cachedHolder = Holder.Reference.createStandAlone(new HolderOwner<>() {}, DANMAKU);

            // 设置DamageType值（这里简化处理，实际应该从注册表获取）
            // 在实际游戏中，应该通过RegistryAccess获取
            cachedHolder.bindValue(damageType);
        }
        return cachedHolder;
    }

    public static class DanmakuDamageSource extends DamageSource{

        public DanmakuDamageSource(Entity projectile, LivingEntity owner) {
            super(new DirectHolder(), projectile, owner);
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

    // 直接Holder实现
    private static class DirectHolder implements Holder<DamageType> {
        @Override
        public DamageType value() {
            return damageType;
        }

        @Override
        public boolean isBound() {
            return true;
        }

        @Override
        public boolean is(ResourceLocation resourceLocation) {
            return DANMAKU.location().equals(resourceLocation);
        }

        @Override
        public boolean is(ResourceKey<DamageType> key) {
            return key.equals(DANMAKU);
        }

        @Override
        public boolean is(Predicate<ResourceKey<DamageType>> predicate) {
            return predicate.test(DANMAKU);
        }

        @Override
        public boolean is(TagKey<DamageType> tagKey) {

            // 返回true的标签：这些是正常的投掷物伤害标签
            if (tagKey.equals(DamageTypeTags.IS_PROJECTILE)) {
                return true; // 弹幕是投掷物
            }

            // 返回false的标签：这些标签会阻止受伤效果
            if (tagKey.equals(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false; // 重要！不能绕过无敌时间
            }

            if (tagKey.equals(DamageTypeTags.BYPASSES_ARMOR)) {
                return false; // 不绕过盔甲
            }

            if (tagKey.equals(DamageTypeTags.BYPASSES_EFFECTS)) {
                return false; // 不绕过效果
            }

            if (tagKey.equals(DamageTypeTags.BYPASSES_COOLDOWN)) {
                return false; // 不绕过冷却
            }

            if (tagKey.equals(DamageTypeTags.BYPASSES_SHIELD)) {
                return false; // 不绕过盾牌
            }

            if (tagKey.equals(DamageTypeTags.WITCH_RESISTANT_TO)) {
                return false; // 女巫不抵抗
            }

            if (tagKey.equals(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)) {
                return false; // 不避开荆棘
            }

            if (tagKey.equals(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)) {
                return false; // 不总是伤害末影龙
            }

            if (tagKey.equals(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) {
                return false; // 不总是触发蠹虫
            }

            // 其他标签默认返回false
            return false;
        }

        @Override
        public Stream<TagKey<DamageType>> tags() {
            // 只返回IS_PROJECTILE标签
            return Stream.of(DamageTypeTags.IS_PROJECTILE);
        }

        @Override
        public Either<ResourceKey<DamageType>, DamageType> unwrap() {
            return Either.right(damageType);
        }

        @Override
        public Optional<ResourceKey<DamageType>> unwrapKey() {
            return Optional.of(DANMAKU);
        }

        @Override
        public Kind kind() {
            return Kind.DIRECT;
        }

        @Override
        public boolean canSerializeIn(HolderOwner<DamageType> owner) {
            return true;
        }
    }
}
