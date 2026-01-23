package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Kaguya;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Kaguya.MOD_ID);

    public static final RegistryObject<EntityType<DanmakuBase>> DANMAKU = ENTITY_TYPES.register("danmaku",
            () -> DanmakuBase.TYPE);
    public static final RegistryObject<EntityType<DanmakuButterfly>> DANMAKU_FLY = ENTITY_TYPES.register("danmaku_fly",
            () -> DanmakuButterfly.TYPE);
    public static final RegistryObject<EntityType<SilverKnifeEntity>> SILVER_KNIFE = ENTITY_TYPES.register("silver_knife",
            () -> SilverKnifeEntity.TYPE);
    public static final RegistryObject<EntityType<DanmakuArrow>> DANMAKU_ARROW = ENTITY_TYPES.register("danmaku_arrow",
            () -> DanmakuArrow.TYPE);
    public static final RegistryObject<EntityType<DanmakuLaser>> DANMAKU_LASER = ENTITY_TYPES.register("danmaku_laser",
            () -> DanmakuLaser.TYPE);
    public static final RegistryObject<EntityType<WindEntity>> WIND = ENTITY_TYPES.register("wind",
            () -> WindEntity.TYPE);

    public static final RegistryObject<EntityType<BeamLaserEntity>> BEAM_LASER =
            ENTITY_TYPES.register("beam_laser",
                    () -> EntityType.Builder.<BeamLaserEntity>of(BeamLaserEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f) // 初始大小，实际大小在 tick 中更新
                            .clientTrackingRange(32)
                            .updateInterval(1)
                            .build("beam_laser"));
    public static final RegistryObject<EntityType<GapEntity>> GAP = ENTITY_TYPES.register("gap",
            () -> GapEntity.TYPE);

}
