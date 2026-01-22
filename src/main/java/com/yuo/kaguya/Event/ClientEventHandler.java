package com.yuo.kaguya.Event;

import com.yuo.kaguya.Client.Model.*;
import com.yuo.kaguya.Client.Render.*;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.DanmakuShotItem;
import com.yuo.kaguya.Item.Prpo.GapFoldingUmbrella;
import com.yuo.kaguya.Item.Prpo.SukimaGap;
import com.yuo.kaguya.Kaguya;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
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
        event.registerEntityRenderer(ModEntityTypes.SILVER_KNIFE_RED.get(), e -> new SilverKnifeRedRender(e, SilverKnifeModel.LAYER_RED));
        event.registerEntityRenderer(ModEntityTypes.SILVER_KNIFE_GREEN.get(), e -> new SilverKnifeGreenRender(e, SilverKnifeModel.LAYER_GREEN));
        event.registerEntityRenderer(ModEntityTypes.SILVER_KNIFE_BLUE.get(), e -> new SilverKnifeBlueRender(e, SilverKnifeModel.LAYER_BLUE));
        event.registerEntityRenderer(ModEntityTypes.SILVER_KNIFE_WHITE.get(), e -> new SilverKnifeWhiteRender(e, SilverKnifeModel.LAYER_WHITE));
        event.registerEntityRenderer(ModEntityTypes.DANMAKU_ARROW.get(), ArrowShotRender::new);
        event.registerEntityRenderer(ModEntityTypes.WIND.get(), WindRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.DANMAKU_LASER.get(), LaserRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BEAM_LASER.get(), BeamLaserRender::new);
        event.registerEntityRenderer(ModEntityTypes.GAP.get(), GapRender::new);
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
            }else if (item instanceof GapFoldingUmbrella){
                event.getItemColors().register(GapFoldingUmbrella::getColor, item);
            }else if (item instanceof SukimaGap){
                event.getItemColors().register(SukimaGap::getColor, item);
            }
        }
    }

    //纹理映射
    @SubscribeEvent
    public static void registerEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SilverKnifeModel.LAYER_RED, SilverKnifeModel::createBodyLayer);
        event.registerLayerDefinition(SilverKnifeModel.LAYER_GREEN, SilverKnifeModel::createBodyLayer);
        event.registerLayerDefinition(SilverKnifeModel.LAYER_BLUE, SilverKnifeModel::createBodyLayer);
        event.registerLayerDefinition(SilverKnifeModel.LAYER_WHITE, SilverKnifeModel::createBodyLayer);
        event.registerLayerDefinition(ButterFlyModel.LAYER_LOCATION, ButterFlyModel::createBodyLayer);
        event.registerLayerDefinition(ArrowShotModel.LAYER_LOCATION, ArrowShotModel::createBodyLayer);
        event.registerLayerDefinition(WindChargeModel.LAYER_LOCATION, WindChargeModel::createBodyLayer);
        event.registerLayerDefinition(LaserModel.LAYER_LOCATION, LaserModel::createBodyLayer);
        event.registerLayerDefinition(GapModel.LAYER_LOCATION, GapModel::createBodyLayer);
    }
}
