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
    public static final RegistryObject<EntityType<YinYangOrbEntity>> YINYANG_ORB = ENTITY_TYPES.register("yinyang_orb",
            () -> YinYangOrbEntity.TYPE);
    public static final RegistryObject<EntityType<BigOrbEntity>> BIG_ORB = ENTITY_TYPES.register("big_orb",
            () -> BigOrbEntity.TYPE);

    public static final RegistryObject<EntityType<BeamLaserEntity>> BEAM_LASER = ENTITY_TYPES.register("beam_laser",
            () -> BeamLaserEntity.TYPE);
    public static final RegistryObject<EntityType<GapEntity>> GAP = ENTITY_TYPES.register("gap",
            () -> GapEntity.TYPE);
    public static final RegistryObject<EntityType<KinkakuJiEntity>> KINKAKU_JI = ENTITY_TYPES.register("kinkaku_ji",
            () -> KinkakuJiEntity.TYPE);
    public static final RegistryObject<EntityType<GoldRaftEntity>> GOLD_BOAT = ENTITY_TYPES.register("gold_raft",
            () -> GoldRaftEntity.TYPE);
    public static final RegistryObject<EntityType<DragonNeckEntity>> DRAGON_NECK = ENTITY_TYPES.register("dragon_neck",
            () -> DragonNeckEntity.TYPE);
    public static final RegistryObject<EntityType<IceStatueEntity>> FROZEN_STATUE = ENTITY_TYPES.register("frozen_statue",
            () -> IceStatueEntity.TYPE);

}
