package com.yuo.kaguya;

import com.yuo.kaguya.Block.ModBlocks;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import com.yuo.kaguya.Menu.ModMenuTypes;
import com.yuo.kaguya.Proxy.ClientProxy;
import com.yuo.kaguya.Proxy.CommonProxy;
import com.yuo.kaguya.Proxy.IProxy;
import com.yuo.kaguya.Tile.ModTileTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("kaguya")
public class Kaguya {
	public static final String MOD_ID = "kaguya";
    public static final IProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	public Kaguya() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		//注册至mod总线
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModTabs.TABS.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModTileTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        proxy.registerHandlers();

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
