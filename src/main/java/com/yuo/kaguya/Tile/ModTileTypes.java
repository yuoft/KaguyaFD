package com.yuo.kaguya.Tile;

import com.yuo.kaguya.Block.ModBlocks;
import com.yuo.kaguya.Kaguya;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//方块实体类型注册
public class ModTileTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Kaguya.MOD_ID);

    public static final RegistryObject<BlockEntityType<DanmakuCraftTile>> DANMAKU_CRAFT = BLOCK_ENTITY_TYPES.register("danmaku_craft",
            () -> BlockEntityType.Builder.of(DanmakuCraftTile::new, ModBlocks.danmakuCraft.get()).build(null));
}
