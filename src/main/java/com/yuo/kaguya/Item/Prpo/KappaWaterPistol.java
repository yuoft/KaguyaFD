package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuShootHelper;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Item.KaguyaPrpo;
import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class KappaWaterPistol extends KaguyaWeapon {
    public KappaWaterPistol(){
        super(Tiers.IRON, 0, new Properties().stacksTo(1).durability(39));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack itemInHand = player.getItemInHand(hand);

            if (itemInHand.getDamageValue() <= 39){
                DanmakuShootHelper.shootDanmakuGravity(level, player, 0.35f, 0.1f, DanmakuType.random(level.random));
                player.getCooldowns().addCooldown(this, 5);
                player.playSound(SoundEvents.WATER_AMBIENT);
            }else player.displayClientMessage(Component.nullToEmpty("No Water!"), true);

            if (!player.getAbilities().instabuild) {
                itemInHand.setDamageValue(Math.min(39, itemInHand.getDamageValue() + 1));
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if (player == null) return super.useOn(context);

        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Level level = context.getLevel();

        pos = pos.relative(face); //获取右键的方块坐标
        FluidState state = level.getFluidState(pos);
        if (!state.isEmpty() && state.getType() == Fluids.WATER){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            player.playSound(SoundEvents.BUCKET_FILL);
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1)); //吸水恢复一点耐久
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

}
