package com.yuo.kaguya.Event;

import com.yuo.kaguya.Entity.IceStatueEntity;
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
    }
}
