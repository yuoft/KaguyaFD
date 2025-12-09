package com.yuo.kaguya.Block;

import com.yuo.kaguya.Kaguya;
import net.minecraft.world.level.block.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//方块注册
public class ModBlocks {
	//同物品
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Kaguya.MOD_ID);

    public static RegistryObject<Block> danmakuCraft = BLOCKS.register("danmaku_craft", DanmakuCraft::new);

}
