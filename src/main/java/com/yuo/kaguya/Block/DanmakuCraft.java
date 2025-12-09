package com.yuo.kaguya.Block;

import com.yuo.kaguya.Tile.DanmakuCraftTile;
import com.yuo.kaguya.Tile.ModTileTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class DanmakuCraft extends BaseEntityBlock {
    public DanmakuCraft() {
        super(Properties.of().strength(5,10));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState blockState, boolean pIsMoving) {
        if (!state.is(blockState.getBlock())) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof DanmakuCraftTile) {
                Containers.dropContents(level, pos, (DanmakuCraftTile)tile);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, blockState, pIsMoving);
        }

    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof DanmakuCraftTile) {
                NetworkHooks.openScreen((ServerPlayer)player, (DanmakuCraftTile)tileEntity, pos);
                player.awardStat(Stats.INTERACT_WITH_FURNACE);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DanmakuCraftTile(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return level.isClientSide ? null : createTickerHelper(entityType, ModTileTypes.DANMAKU_CRAFT.get(), DanmakuCraftTile::serverTick);
    }
}
