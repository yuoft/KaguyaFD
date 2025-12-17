package com.yuo.kaguya.Item.Weapon;

import com.mojang.datafixers.util.Either;
import com.yuo.kaguya.RlUtil;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DanmakuDamageTypes {

    public static ResourceKey<DamageType> DANMAKU = ResourceKey.create(Registries.DAMAGE_TYPE, RlUtil.fa("danmaku"));

    public static final RegistrySetBuilder DAMAGE_BUILDER = new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, DanmakuDamageTypes::bootstrap);

    public static HolderLookup.Provider append(HolderLookup.Provider original) {
        return DAMAGE_BUILDER.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), original);
    }

    // 注册
    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(DANMAKU, new DamageType(type, 2.0f));
    }

    private static final String type = "danmaku";
    static DamageType damageType = new DamageType(type, 2.0f);

    /**
     * 弹幕伤害
     * @param living 攻击实体
     * @param target 受伤实体
     */
    public static DanmakuDamageSource danmaku(LivingEntity living, LivingEntity target){
        return new DanmakuDamageSource(living, target);
    }

    public static boolean isDanmaku(DamageSource source){
        if (source instanceof DanmakuDamageSource) return true;
        return source.getMsgId().equals(type);
    }

    private static Direct direct(DamageType t) {
        return new Direct(t);
    }

    public static class DanmakuDamageSource extends DamageSource{
        private final LivingEntity player;
        private final LivingEntity target;

        public DanmakuDamageSource(LivingEntity living, LivingEntity target) {
            super(direct(damageType), target, living);
            this.target = target;
            this.player = living;
        }

        @Override
        public @NotNull Holder<DamageType> typeHolder() {
            return direct(damageType);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity livingEntity) {
            return Component.translatable("info.kaguya.damage_source.danmaku", target.getDisplayName(), player.getDisplayName());
        }

        //是否根据难度缩放伤害值
        @Override
        public boolean scalesWithDifficulty() {
            return false;
        }

        @Override
        public @NotNull String toString() {
            return "DanmakuDamageSource (" + this.getEntity() + ")";
        }
    }

    public static class Direct implements Holder<DamageType> {
        @Nullable
        private DamageType value;
        public Direct(DamageType value) {
            this.value = value;
        }

        public boolean isBound() {
            return true;
        }

        public boolean is(ResourceLocation location) {
            return false;
        }

        public boolean is(ResourceKey<DamageType> key) {
            return false;
        }

        public boolean is(TagKey<DamageType> tagKey) {
            return false;
        }

        public boolean is(Predicate<ResourceKey<DamageType>> keyPredicate) {
            return false;
        }

        public Either<ResourceKey<DamageType>, DamageType> unwrap() {
            return Either.right(this.value);
        }

        public Optional<ResourceKey<DamageType>> unwrapKey() {
            return Optional.of(DanmakuDamageTypes.DANMAKU);
        }

        public Kind kind() {
            return Kind.DIRECT;
        }

        public String toString() {
            return "Direct{" + this.value + "}";
        }

        public boolean canSerializeIn(HolderOwner<DamageType> holderOwner) {
            return true;
        }

        public Stream<TagKey<DamageType>> tags() {
            return Stream.of();
        }

        public DamageType value() {
            return this.value;
        }
    }
}
