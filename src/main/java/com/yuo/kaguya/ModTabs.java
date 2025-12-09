package com.yuo.kaguya;

import com.yuo.kaguya.Item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

//创造模式物品栏 实例化
public class ModTabs {
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kaguya.MOD_ID);
	public static final RegistryObject<CreativeModeTab> Kaguya_TAB = TABS.register(Kaguya.MOD_ID + "_tab_item", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.tab.kaguya.item"))
			.icon(() -> ModItems.extend.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
					if (entry.get() instanceof KaguyaMaterialItem || entry.get() instanceof KaguyaFood){
						output.accept(new ItemStack(entry.get()));
					}
				}

			}).build());
	public static final RegistryObject<CreativeModeTab> Kaguya_TAB0 = TABS.register(Kaguya.MOD_ID + "_tab_sword", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.tab.kaguya.weapon"))
			.icon(() -> ModItems.hisouSword.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
					if (entry.get() instanceof SwordItem || entry.get() instanceof KaguyaArmor){
						output.accept(new ItemStack(entry.get()));
					}
				}

			}).build());
	public static final RegistryObject<CreativeModeTab> Kaguya_TAB1 = TABS.register(Kaguya.MOD_ID + "_tab_prpo", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.tab.kaguya.prpo"))
			.icon(() -> ModItems.thirdEye0.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
					if (entry.get() instanceof KaguyaPrpo){
						output.accept(new ItemStack(entry.get()));
					}
				}

			}).build());
	public static final RegistryObject<CreativeModeTab> Kaguya_TAB2 = TABS.register(Kaguya.MOD_ID + "_tab_danmaku", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.tab.kaguya.danmaku"))
			.icon(() -> ModItems.heartShot.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
					if (entry.get() instanceof DanmakuShotItem danmakuShotItem){
						output.accept(entry.get());
					}
				}

			}).build());
}
