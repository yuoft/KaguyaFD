package com.yuo.kaguya.Item.Food;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * 伊吹瓢
 */
public class LbukiGourd extends KaguyaFood{
    public LbukiGourd() {
        super(9, ibukiGourd, false);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Level level = context.getLevel();
        FluidState state = level.getFluidState(pos);
        if (!state.isEmpty() && state.getType() == Fluids.WATER){
            ItemStack stack = context.getItemInHand();
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 3));
            context.getPlayer().playSound(SoundEvents.BOTTLE_FILL);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    /**
     * 食用消耗一点耐久
     */
    @Override
    protected ItemStack eat(Level level, LivingEntity living, ItemStack stack) {
        if (stack.isEdible()) {
            level.playSound(null, living.getX(), living.getY(), living.getZ(), living.getEatingSound(stack), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
            this.addEatEffect(stack, level, living);
            if (!(living instanceof Player) || !((Player)living).getAbilities().instabuild) {
                stack.hurtAndBreak(1, living, e -> living.broadcastBreakEvent(e.getUsedItemHand()));
            }
            if (living instanceof Player player){
                player.getFoodData().eat(ibukiGourd.getNutrition(), ibukiGourd.getSaturationModifier());
            }

            living.gameEvent(GameEvent.EAT);
        }

        return stack;
    }

}
