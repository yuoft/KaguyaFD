package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WallPassingChisel extends KaguyaWeapon {

    public WallPassingChisel() {
        super(Tiers.IRON, -2, new Properties().stacksTo(1).durability(122));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Direction direction = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        if (player != null) {
            BlockState state = level.getBlockState(pos);
            float destroySpeed = state.getBlock().defaultDestroyTime();
            if (destroySpeed > 0 && destroySpeed < 50){
                BlockPos playerTpPos = getPlayerTpPos(level, pos, direction.getOpposite());
                if (playerTpPos != null && playerTpPos.getY() > -64) {
                    for (int i = 0; i < 20; i++){
                        level.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.01f, 0.01f, 0.01f);
                    }
                    if (!level.isClientSide)
                        player.teleportTo(playerTpPos.getX() + 0.5, playerTpPos.getY(), playerTpPos.getZ() + 0.5);

                    player.playSound(SoundEvents.ENDERMAN_TELEPORT);
                    for (int i = 0; i < 20; i++){
                        level.addParticle(ParticleTypes.PORTAL, playerTpPos.getX() + 0.5, playerTpPos.getY(), playerTpPos.getZ() + 0.5, 0.01f, 0.01f, 0.01f);
                    }
                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                    player.getCooldowns().addCooldown(this, 20);
                    return InteractionResult.SUCCESS;
                }else player.displayClientMessage(Component.translatable("info.kaguya.kabenuke.error_pos"), true);
            }else player.displayClientMessage(Component.translatable("info.kaguya.kabenuke.error_block"), true);
        }
        return InteractionResult.PASS;
    }

    private BlockPos getPlayerTpPos(Level level, BlockPos pos, Direction direction) {
        int maxDis = 15;
        for (int i = 0; i < maxDis; i++) {
            BlockPos relative = pos.relative(direction, i + 1);
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
