package com.yuo.kaguya.Event;

import com.yuo.kaguya.Entity.IceStatueEntity;
import com.yuo.kaguya.Entity.Mob.Cirno;
import com.yuo.kaguya.Entity.Mob.HakureiReimu;
import com.yuo.kaguya.Entity.Mob.KFDBaseMob;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Kaguya;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Kaguya.MOD_ID, bus = Bus.MOD)
public class MobEvents {

    //实体属性
    @SubscribeEvent
    public static void onRegisterEntitiesAttr(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.FROZEN_STATUE.get(), IceStatueEntity.bakeAttributes().build());
        event.put(ModEntityTypes.HAKUREI_REIMU.get(), HakureiReimu.createAttributes().build());
        event.put(ModEntityTypes.KIRISAME_MARISA.get(), KFDBaseMob.createAttributes().build());
        event.put(ModEntityTypes.RUMIA.get(), KFDBaseMob.createAttributes().build());
        event.put(ModEntityTypes.CIRNO.get(), Cirno.createAttributes().build());
        event.put(ModEntityTypes.KOCHIYA_SANAE.get(), KFDBaseMob.createAttributes().build());
        event.put(ModEntityTypes.MORICHIKA_RINNOSUKE.get(), KFDBaseMob.createAttributes().build());
    }

}
