package com.yuo.kaguya.Menu;

import com.yuo.kaguya.Kaguya;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Kaguya.MOD_ID);

    public static final RegistryObject<MenuType<DanmakuCraftMenu>> DANMAKU_CRAFT = MENU_TYPES.register("danmaku_craft", () ->
            IForgeMenuType.create(DanmakuCraftMenu::new));

    private static <T extends AbstractContainerMenu> MenuType<T> getMenu(IContainerFactory<T> containerFactory) {
        return new MenuType<T>(containerFactory, FeatureFlags.VANILLA_SET);
    }
}
