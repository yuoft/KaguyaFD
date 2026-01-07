package com.yuo.kaguya.Proxy;

import com.yuo.kaguya.Client.Screen.DanmakuCraftScreen;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Menu.ModMenuTypes;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 客户端属性注册
 */
public class ClientProxy implements IProxy {

    @Override
    public void registerHandlers() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::clientSetup);
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            setRemorseRodProperty(ModItems.remorseRod.get());
            MenuScreens.register(ModMenuTypes.DANMAKU_CRAFT.get(), DanmakuCraftScreen::new);
        });
//        ItemBlockRenderTypes.setRenderLayer(OreCropBlocks.customSapling.get(), RenderType.cutout());
    }

    private void setRemorseRodProperty(Item item){
        ItemProperties.register(item, KaguyaUtils.fa("fix"), (itemStack, clientWorld, livingEntity, i)
                -> livingEntity != null && itemStack.areShareTagsEqual(livingEntity.getItemInHand(InteractionHand.MAIN_HAND))
                && itemStack.getDamageValue() > 0 ? 1.0F : 0.0F);
    }
}
