package com.yuo.kaguya.Event;

import com.yuo.kaguya.Client.Model.*;
import com.yuo.kaguya.Client.Render.*;
import com.yuo.kaguya.Client.Render.Mob.YSMModelRender;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.ModColorItemUtils;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.DanmakuShotItem;
import com.yuo.kaguya.Item.Prpo.GapFoldingUmbrella;
import com.yuo.kaguya.Item.Prpo.SukimaGap;
import com.yuo.kaguya.Item.Weapon.SilverKnife;
import com.yuo.kaguya.Kaguya;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * 客户端事件
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Kaguya.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventHandler {

    //实体渲染注册
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.DANMAKU.get(), DanmakuRender::new); //投掷物渲染
        event.registerEntityRenderer(ModEntityTypes.DANMAKU_FLY.get(), ButterFlyRender::new);
        event.registerEntityRenderer(ModEntityTypes.SILVER_KNIFE.get(), SilverKnifeRender::new);
        event.registerEntityRenderer(ModEntityTypes.DANMAKU_ARROW.get(), ArrowShotRender::new);
        event.registerEntityRenderer(ModEntityTypes.WIND.get(), WindRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.DANMAKU_LASER.get(), LaserRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.DANMAKU_LASER0.get(), LaserRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BEAM_LASER.get(), BeamLaserRender::new);
        event.registerEntityRenderer(ModEntityTypes.GAP.get(), GapRender::new);
        event.registerEntityRenderer(ModEntityTypes.KINKAKU_JI.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.GOLD_BOAT.get(), GoldBoatRender::new);
        event.registerEntityRenderer(ModEntityTypes.DRAGON_NECK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.FROZEN_STATUE.get(), IceStatueRender::new);
        event.registerEntityRenderer(ModEntityTypes.YINYANG_ORB.get(), YinYangOrbRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BIG_ORB.get(), BigOrbRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CROSS_ENTITY.get(), CrossRender::new);
        event.registerEntityRenderer(ModEntityTypes.REBOUND_SHIELD.get(), ReboundRender::new);

        event.registerEntityRenderer(ModEntityTypes.HAKUREI_REIMU.get(), YSMModelRender::new);
        event.registerEntityRenderer(ModEntityTypes.KIRISAME_MARISA.get(), YSMModelRender::new);
        event.registerEntityRenderer(ModEntityTypes.RUMIA.get(), YSMModelRender::new);
        event.registerEntityRenderer(ModEntityTypes.CIRNO.get(), YSMModelRender::new);
        event.registerEntityRenderer(ModEntityTypes.KOCHIYA_SANAE.get(), YSMModelRender::new);
        event.registerEntityRenderer(ModEntityTypes.MORICHIKA_RINNOSUKE.get(), YSMModelRender::new);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (String skinName : event.getSkins()) {
            PlayerRenderer skin = event.getSkin(skinName);
            if (skin != null) {
                skin.addLayer(new PlayerCircleRenderer(skin));
            }
        }
    }

    //染色
    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.Item event) {
        for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof DanmakuShotItem){
                event.getItemColors().register(DanmakuShotItem::getColor, item);
            }else if (item instanceof GapFoldingUmbrella || item instanceof SukimaGap || item instanceof SilverKnife){
                event.getItemColors().register(ModColorItemUtils::getColor, item);
            }
        }
    }

    //纹理映射
    @SubscribeEvent
    public static void registerEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SilverKnifeModel.LAYER_LOCATION, SilverKnifeModel::createBodyLayer);
        event.registerLayerDefinition(ButterFlyModel.LAYER_LOCATION, ButterFlyModel::createBodyLayer);
        event.registerLayerDefinition(ArrowShotModel.LAYER_LOCATION, ArrowShotModel::createBodyLayer);
        event.registerLayerDefinition(WindChargeModel.LAYER_LOCATION, WindChargeModel::createBodyLayer);
        event.registerLayerDefinition(LaserModel.LAYER_LOCATION, LaserModel::createBodyLayer);
        event.registerLayerDefinition(GapModel.LAYER_LOCATION, GapModel::createBodyLayer);
        event.registerLayerDefinition(CrossModel.LAYER_LOCATION, CrossModel::createBodyLayer);
    }

}
