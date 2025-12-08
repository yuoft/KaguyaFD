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
}
