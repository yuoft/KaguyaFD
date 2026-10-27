package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class MagicBroom extends KaguyaPrpo {

    /** 可被清扫的方块集合 */
    private static final Set<Block> CLEANABLE_BLOCKS = new HashSet<>();

    static {
        CLEANABLE_BLOCKS.add(Blocks.FIRE);       // 火焰
        CLEANABLE_BLOCKS.add(Blocks.SOUL_FIRE);  // 灵魂火
        CLEANABLE_BLOCKS.add(Blocks.SNOW);       // 积雪层（不是雪块）
        CLEANABLE_BLOCKS.add(Blocks.COBWEB);     // 蜘蛛网
        CLEANABLE_BLOCKS.add(Blocks.MOSS_CARPET);     // 覆地苔藓
        CLEANABLE_BLOCKS.add(Blocks.SCULK_VEIN);     // 幽匿脉络
        // 原版没有“落叶”方块。若你的整合包/模组有落叶装饰方块，
        // 可在此添加，或调用 MagicBroom.addCleanableBlock(block) 扩展
    }

    /** 供其他模组/代码动态注册可清扫方块 */
    public static void addCleanableBlock(Block block) {
        CLEANABLE_BLOCKS.add(block);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;

            // 清扫中心：优先取视线命中的方块，否则取视线前方 3 格
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 center;

            HitResult hit = player.pick(5.0D, 0.0F, false);
            if (hit.getType() == HitResult.Type.BLOCK) {
                center = hit.getLocation();
            } else {
                center = eye.add(look.scale(3.0D));
            }

            // 以 center 为中心构建 5x3x5 区域
            int half = 2;
            int cx = (int) Math.floor(center.x);
            int cy = (int) Math.floor(center.y);
            int cz = (int) Math.floor(center.z);

            BlockPos min = new BlockPos(cx - half, cy - 1, cz - half);
            BlockPos max = new BlockPos(cx + half, cy + 1, cz + half);

            int cleaned = 0;
            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                BlockState state = level.getBlockState(pos);
                if (CLEANABLE_BLOCKS.contains(state.getBlock())) {
                    level.destroyBlock(pos, false);
                    cleaned++;
                }
            }

            if (cleaned > 0) {
                player.getCooldowns().addCooldown(this, 60);
            }
        }

        return super.use(level, player, hand);
    }
}