package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class CloseEye3rd extends KaguyaPrpo {

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int i) {
        if (living instanceof Player player && !level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            // 设置玩家为隐身模式（会影响盔甲和手持物品）
            player.setInvisible(false);
            // 广播玩家状态更新
            serverLevel.getChunkSource().broadcast(player,
                    new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData().getNonDefaultValues()));
        }
        super.releaseUsing(stack, level, living, i);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int i) {
        if (living instanceof Player player && !level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 0));
            ServerLevel serverLevel = (ServerLevel) level;
            // 设置玩家为隐身模式（会影响盔甲和手持物品）
            player.setInvisible(true);
            // 广播玩家状态更新
            serverLevel.getChunkSource().broadcast(player,
                    new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData().getNonDefaultValues()));
        }
        super.onUseTick(level, living, stack, i);
    }
}
