package com.yuo.kaguya;

import com.yuo.kaguya.Block.ModBlocks;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Event.MobEvents;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Menu.ModMenuTypes;
import com.yuo.kaguya.Proxy.ClientProxy;
import com.yuo.kaguya.Proxy.CommonProxy;
import com.yuo.kaguya.Proxy.IProxy;
import com.yuo.kaguya.Tile.ModTileTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("kaguya")
public class Kaguya {
	public static final String MOD_ID = "kaguya";
    public static final IProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	@SuppressWarnings("removal")
    public Kaguya() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_CONFIG); //配置文件
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		//注册至mod总线
        modEventBus.addListener(this::commonSetup);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModTabs.TABS.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModTileTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        proxy.registerHandlers();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Config.loadConfig();
    }

    /**
     * 检查mod
     * @param modId 模组id
     * @return 存在 true
     */
    private boolean checkMod(String modId){
        return ModList.get().isLoaded(modId);
    }
}
