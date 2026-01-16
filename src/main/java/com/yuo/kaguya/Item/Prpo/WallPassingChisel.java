package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WallPassingChisel extends KaguyaPrpo {

    public WallPassingChisel() {
        super(new Properties().stacksTo(1).durability(122));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Direction direction = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide && player != null) {
            BlockState state = level.getBlockState(pos);
            float destroySpeed = state.getBlock().defaultDestroyTime();
            if (destroySpeed > 0 && destroySpeed < 50){
                BlockPos playerTpPos = getPlayerTpPos(level, pos, direction);
                if (playerTpPos != null) {
                    for (int i = 0; i < 10; i++){
                        level.addParticle(ParticleTypes.PORTAL, pos.getX(), pos.getY(), pos.getZ(), level.random.nextFloat() * 0.25f, level.random.nextFloat() * 0.5f, level.random.nextFloat() * 0.25f);
                    }
                    player.teleportTo(playerTpPos.getX(), playerTpPos.getY(), playerTpPos.getZ());

                    for (int i = 0; i < 10; i++){
                        level.addParticle(ParticleTypes.PORTAL, playerTpPos.getX(), playerTpPos.getY(), playerTpPos.getZ(), level.random.nextFloat() * 0.25f, level.random.nextFloat() * 0.5f, level.random.nextFloat() * 0.25f);
                    }
                    if (!player.getAbilities().instabuild) {
                        player.getCooldowns().addCooldown(this, 20);
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                }
            }

        }
        return super.useOn(context);
    }

    private BlockPos getPlayerTpPos(Level level, BlockPos pos, Direction direction) {
        int maxDis = 15;
        for (int i = 0; i < maxDis; i++) {
            BlockPos relative = pos.relative(direction, i);
            if (isAirOrNoSolid(level, relative)) {
                BlockPos above = relative.above();
                BlockPos below = relative.below();
                if (isAirOrNoSolid(level, above)) {
                    return relative;
                }else if (isAirOrNoSolid(level, below)) {
                    return below;
                }
            }
        }
        return null;
    }

    private boolean isAirOrNoSolid(Level level, BlockPos pos){
        return level.getBlockState(pos).isAir() || !level.getBlockState(pos).isSolid();
    }
}
