package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.GapEntity;
import com.yuo.kaguya.Entity.KaguyaLevelSaveData;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class GapItem extends KaguyaPrpo {
    public GapItem() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Level level = context.getLevel();
        BlockPos spawnPos = clickedPos.relative(clickedFace, 1);
        if (player == null) return InteractionResult.FAIL;
        if (level.getBlockState(spawnPos).isAir() || level.getBlockState(spawnPos).canBeReplaced()) {
            GapEntity gap = new GapEntity(level, spawnPos, player.getYRot());
            if (!level.isClientSide) {
                level.addFreshEntity(gap);
                KaguyaLevelSaveData saveData = KaguyaLevelSaveData.get(level);
                saveData.addPos(gap.getUUID(), spawnPos);
            }
            if (level.isClientSide){
                player.playSound(SoundEvents.ENDERMAN_TELEPORT);
            }
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return super.useOn(context);
    }
}
