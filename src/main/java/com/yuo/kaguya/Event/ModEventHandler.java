package com.yuo.kaguya.Event;

import com.yuo.kaguya.Entity.IceStatueEntity;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Kaguya;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kaguya.MOD_ID)
public class ModEventHandler {
    private static final RandomSource RANDOM = RandomSource.create();

    @SubscribeEvent
    public static void onTick(PlayerTickEvent event){
        Player player = event.player;
        if (isPlayerFalling(player)){
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offhandItem = player.getOffhandItem();

            boolean flag = false;

            if (mainHandItem.getItem() == ModItems.yuukaParasol.get()) {
                flag = true;
            }else if (offhandItem.getItem() == ModItems.yuukaParasol.get()) {
                flag = true;
            }

            if (flag) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 0, 0));
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event){
        Entity target = event.getTarget();
        Player player = event.getEntity();
        Level level = event.getEntity().level();
        if (target instanceof IceStatueEntity iceStatue) { //冰雕破坏
            ItemStack handItem = player.getMainHandItem();
            if (handItem.is(ItemTags.PICKAXES)){
                iceStatue.playSound(SoundEvents.GLASS_HIT,2, 0.5F + (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 0.5F);
                event.setCanceled(true);

                iceStatue.setCrackAmount(iceStatue.getCrackAmount() + 1);
                if (iceStatue.getCrackAmount() > 9){
                    CompoundTag tag = new CompoundTag();
                    iceStatue.saveWithoutId(tag);
                    iceStatue.playSound(SoundEvents.GLASS_BREAK);
                    if (!level.isClientSide()){
                        iceStatue.spawnAtLocation(new ItemStack(Items.ICE), 1);
                    }

                    iceStatue.remove(RemovalReason.KILLED);
                }
            }
        }
    }

    //添加附魔
    @SubscribeEvent
    public static void onCraft(ItemCraftedEvent event){
        ItemStack crafting = event.getCrafting();
        Item item = crafting.getItem();
        if (item == ModItems.fireRatBobe.get()){
            crafting.enchant(Enchantments.FIRE_PROTECTION, 10);
        }else if (item == ModItems.marisaHead.get()){
            crafting.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 5);
        }else if (item == ModItems.suwakoHead.get()){
            crafting.enchant(Enchantments.THORNS, 10);
        }else if (item == ModItems.yuukaParasol.get()){
            crafting.enchant(Enchantments.KNOCKBACK, 3);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        BlockState state = event.getState();
        if (state.getBlock() instanceof LeavesBlock){
            LevelAccessor level = event.getLevel();
            ItemStack useItem = event.getPlayer().getMainHandItem();
            BlockPos pos = event.getPos();
            if (useItem.getItem() == ModItems.hisouSword.get() && pos.getY() >= 128){ //物品掉落
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

    //玩家登入
    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        if (!player.getPersistentData().getBoolean("kaguya:login")){
            player.getPersistentData().putBoolean("kaguya:login", true);
            player.sendSystemMessage(Component.translatable("kaguya.message.login")
                    .setStyle(Style.EMPTY.withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.translatable("kaguya.message.login0")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://space.bilibili.com/21854371"))));
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
    public static ItemStack getPlayerBagItem(Player player, Item item){
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

    /**
     * 玩家是否处于下落状态
     */
    public static boolean isPlayerFalling(Player player) {
        // 基础检查：不在地面上，且垂直速度为负
        if (player.onGround() || player.getDeltaMovement().y >= 0) {
            return false;
        }

        // 额外检查：排除攀爬、游泳、飞行等状态
        //  在梯子上或藤蔓上 在水中 使用鞘翅滑翔
        if (player.isVisuallyCrawling() || player.isInWater() || player.isFallFlying()) {
            return false;
        }

        if (player.getAbilities().mayfly) return false;

        return !player.getAbilities().instabuild && !player.level().isClientSide; //生存模式
    }
}
