package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Kaguya;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Kaguya.MOD_ID);

    public static final RegistryObject<EntityType<DanmakuBase>> DANMAKU = ENTITY_TYPES.register("danmaku",
            () -> DanmakuBase.TYPE);
    public static final RegistryObject<EntityType<DanmakuButterfly>> HEART_SHOT = ENTITY_TYPES.register("danmaku_fly",
            () -> DanmakuButterfly.TYPE);
    public static final RegistryObject<EntityType<SilverKnife>> SILVER_KNIFE_RED = ENTITY_TYPES.register("silver_knife_red",
            () -> SilverKnife.TYPE_RED);
    public static final RegistryObject<EntityType<SilverKnife>> SILVER_KNIFE_GREEN = ENTITY_TYPES.register("silver_knife_green",
            () -> SilverKnife.TYPE_GREEN);
    public static final RegistryObject<EntityType<SilverKnife>> SILVER_KNIFE_BLUE = ENTITY_TYPES.register("silver_knife_blue",
            () -> SilverKnife.TYPE_BLUE);
    public static final RegistryObject<EntityType<SilverKnife>> SILVER_KNIFE_WHITE = ENTITY_TYPES.register("silver_knife_white",
            () -> SilverKnife.TYPE_WHITE);
}
