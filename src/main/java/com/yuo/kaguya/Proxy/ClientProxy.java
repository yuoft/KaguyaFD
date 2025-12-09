package com.yuo.kaguya.Proxy;

import com.yuo.kaguya.Client.Screen.DanmakuCraftScreen;
import com.yuo.kaguya.Menu.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
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
            MenuScreens.register(ModMenuTypes.DANMAKU_CRAFT.get(), DanmakuCraftScreen::new);
        });
//        ItemBlockRenderTypes.setRenderLayer(OreCropBlocks.customSapling.get(), RenderType.cutout());
    }
}
