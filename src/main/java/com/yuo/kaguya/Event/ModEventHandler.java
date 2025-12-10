package com.yuo.kaguya.Event;

import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Kaguya;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kaguya.MOD_ID)
public class ModEventHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        BlockState state = event.getState();
        if (state.getBlock() instanceof LeavesBlock){
            LevelAccessor level = event.getLevel();
            ItemStack useItem = event.getPlayer().getMainHandItem();
            BlockPos pos = event.getPos();
            if (useItem.getItem() == ModItems.hisouSword.get() && pos.getY() >= 128){
                RandomSource random = level.getRandom();
                int looting = useItem.getEnchantmentLevel(Enchantments.MOB_LOOTING);
                if (random.nextFloat() < 0.15f + looting * 0.05f){
                    ItemEntity itemEntity = new ItemEntity(event.getPlayer().level(), pos.getX(), pos.getY() + 0.5d, pos.getZ(), new ItemStack(ModItems.heavenlyPeach.get()));
                    level.addFreshEntity(itemEntity);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack totem = getPlayerBagItem(player, ModItems.extend.get());
            if (!totem.isEmpty()){
                playTotem(totem, player);
                if (player instanceof ServerPlayer serverplayer) {
                    serverplayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayer, totem);
                }
                player.removeAllEffects();
                player.setHealth(player.getMaxHealth());
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2600, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 700, 4));
//                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1100, 0));
                totem.shrink(1);
                event.setCanceled(true);
            }
        }
    }

    /**
     * 播放图腾动画
     * @param stack 动画物品
     * @param entity 对谁播放
     */
    @OnlyIn(Dist.CLIENT)
    public static void playTotem(ItemStack stack, Entity entity) {
        Minecraft instance = Minecraft.getInstance();
        ClientLevel world = instance.level;
        if (world != null) {
            instance.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
            world.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
            instance.gameRenderer.displayItemActivation(stack);
        }

    }

    /**
     * 获取玩家背包中的物品
     * @param player 玩家
     */
    private static ItemStack getPlayerBagItem(Player player, Item item){
        ItemStack mainhand = player.getMainHandItem();
        if (mainhand.getItem() == item){
            return mainhand;
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == item){
            return offhand;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item)
                return stack;
        }

        return ItemStack.EMPTY;
    }
}
