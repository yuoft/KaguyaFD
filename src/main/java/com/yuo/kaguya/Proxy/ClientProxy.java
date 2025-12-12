package com.yuo.kaguya.Proxy;

import com.yuo.kaguya.Client.Screen.DanmakuCraftScreen;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Menu.ModMenuTypes;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ModelEvent;
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
            setKnifeProperty(ModItems.silverKnifeWhite.get());
            MenuScreens.register(ModMenuTypes.DANMAKU_CRAFT.get(), DanmakuCraftScreen::new);
        });
//        ItemBlockRenderTypes.setRenderLayer(OreCropBlocks.customSapling.get(), RenderType.cutout());
    }

    private void setKnifeProperty(Item item){
        ItemProperties.register(item, RlUtil.fa("throwing"), (itemStack, clientWorld, livingEntity, i)
                -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
    }
}
