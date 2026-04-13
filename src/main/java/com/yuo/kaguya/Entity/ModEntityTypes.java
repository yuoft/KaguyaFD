package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Entity.Mob.BaseMobEntity;
import com.yuo.kaguya.Entity.Mob.SanaeMob;
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
    public static final RegistryObject<EntityType<DanmakuLaser0>> DANMAKU_LASER0 = ENTITY_TYPES.register("danmaku_laser0",
            () -> DanmakuLaser0.TYPE);
    public static final RegistryObject<EntityType<WindEntity>> WIND = ENTITY_TYPES.register("wind",
            () -> WindEntity.TYPE);
    public static final RegistryObject<EntityType<YinYangOrbEntity>> YINYANG_ORB = ENTITY_TYPES.register("yinyang_orb",
            () -> YinYangOrbEntity.TYPE);
    public static final RegistryObject<EntityType<BigOrbEntity>> BIG_ORB = ENTITY_TYPES.register("big_orb",
            () -> BigOrbEntity.TYPE);
    public static final RegistryObject<EntityType<ReboundEntity>> REBOUND_SHIELD = ENTITY_TYPES.register("rebound_shield",
            () -> ReboundEntity.TYPE);

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
    public static final RegistryObject<EntityType<CrossEntity>> CROSS_ENTITY = ENTITY_TYPES.register("cross_entity",
            () -> CrossEntity.TYPE);

    private static final float sizeWidth = 0.6f;
    private static final float sizeHeight = 1.5f;
    private static final int trackingRange = 16;
    private static final int updateInterval = 1;

    public static final RegistryObject<EntityType<BaseMobEntity>> HAKUREI_REIMU = ENTITY_TYPES.register("hakurei_reimu",
            () -> EntityType.Builder.of(BaseMobEntity::new, MobCategory.MONSTER).sized(sizeWidth, sizeHeight)
                    .clientTrackingRange(trackingRange).updateInterval(updateInterval).build("hakurei_reimu"));
    public static final RegistryObject<EntityType<BaseMobEntity>> KIRISAME_MARISA = ENTITY_TYPES.register("kirisame_marisa",
            () -> EntityType.Builder.of(BaseMobEntity::new, MobCategory.MONSTER).sized(sizeWidth, sizeHeight)
                    .clientTrackingRange(trackingRange).updateInterval(updateInterval).build("kirisame_marisa"));
    public static final RegistryObject<EntityType<BaseMobEntity>> RUMIA = ENTITY_TYPES.register("rumia",
            () -> EntityType.Builder.of(BaseMobEntity::new, MobCategory.MONSTER).sized(sizeWidth, sizeHeight)
                    .clientTrackingRange(trackingRange).updateInterval(updateInterval).build("rumia"));
    public static final RegistryObject<EntityType<BaseMobEntity>> CIRNO = ENTITY_TYPES.register("cirno",
            () -> EntityType.Builder.of(BaseMobEntity::new, MobCategory.MONSTER).sized(sizeWidth, sizeHeight)
                    .clientTrackingRange(trackingRange).updateInterval(updateInterval).build("cirno"));
    public static final RegistryObject<EntityType<SanaeMob>> KOCHIYA_SANAE = ENTITY_TYPES.register("kochiya_sanae",
            () -> EntityType.Builder.of(SanaeMob::new, MobCategory.CREATURE).sized(sizeWidth, sizeHeight)
                    .clientTrackingRange(trackingRange).updateInterval(updateInterval).build("kochiya_sanae"));

}
