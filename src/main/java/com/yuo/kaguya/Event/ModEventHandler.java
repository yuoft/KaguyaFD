package com.yuo.kaguya.Event;

import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.yuo.endless.NetWork.NetWorkHandler;
import com.yuo.endless.NetWork.TotemPacket;
import com.yuo.kaguya.Entity.IceStatueEntity;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Kaguya;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = Kaguya.MOD_ID)
public class ModEventHandler {
    private static final RandomSource RANDOM = RandomSource.create();
    private static MobSpawnSettings.SpawnerData SPAWNER_DATA0;
    private static MobSpawnSettings.SpawnerData SPAWNER_DATA1;
    private static MobSpawnSettings.SpawnerData SPAWNER_DATA2;
    private static MobSpawnSettings.SpawnerData SPAWNER_DATA3;
    private static MobSpawnSettings.SpawnerData SPAWNER_DATA4;

    @SubscribeEvent
    public static void addMobSpawnInfo(LevelEvent.PotentialSpawns event) {
        LevelAccessor var2 = event.getLevel();
        if (var2 instanceof ServerLevel level) {

            ResourceLocation dimension = level.dimension().location();
            if (event.getMobCategory() == MobCategory.MONSTER && dimensionIsOkay(dimension)) {
                List<SpawnerData> spawnerData = event.getSpawnerDataList();
                boolean canZombieSpawn = spawnerData.stream().anyMatch((data) -> data.type.equals(EntityType.ZOMBIE));
                if (SPAWNER_DATA0 == null){
                    SPAWNER_DATA0 = new MobSpawnSettings.SpawnerData(ModEntityTypes.HAKUREI_REIMU.get(), 2, 0, 1);
                    SPAWNER_DATA1 = new MobSpawnSettings.SpawnerData(ModEntityTypes.KIRISAME_MARISA.get(), 2, 0, 1);
                    SPAWNER_DATA2 = new MobSpawnSettings.SpawnerData(ModEntityTypes.RUMIA.get(), 2, 0, 1);
                    SPAWNER_DATA3 = new MobSpawnSettings.SpawnerData(ModEntityTypes.CIRNO.get(), 2, 0, 1);
                }
                if (canZombieSpawn) {
                    event.addSpawnerData(SPAWNER_DATA0);
                    event.addSpawnerData(SPAWNER_DATA1);
                    event.addSpawnerData(SPAWNER_DATA2);
                    event.addSpawnerData(SPAWNER_DATA3);
                }
            }else if (event.getMobCategory() == MobCategory.CREATURE && dimensionIsOverworld(dimension)){
                List<SpawnerData> spawnerData = event.getSpawnerDataList();
                boolean canSpawn = spawnerData.stream().anyMatch((data) -> data.type.equals(EntityType.WANDERING_TRADER));
                if (SPAWNER_DATA4 == null){
                    SPAWNER_DATA4 = new MobSpawnSettings.SpawnerData(ModEntityTypes.KOCHIYA_SANAE.get(), 5, 0, 1);
                }
                if (canSpawn){
                    event.addSpawnerData(SPAWNER_DATA0);
                }
            }
        }

    }

    @SubscribeEvent
    public static void onEntityDrop(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        BlockPos pos = entity.getOnPos();
        int lootingLevel = event.getLootingLevel();
        if (entity.getType() == InitEntities.FAIRY.get()){
            int i = Mth.randomBetweenInclusive(level.random, 1, 1 + lootingLevel);
            Collection<ItemEntity> drops = event.getDrops();
            addDropItem(drops, level, pos, ModItems.smallPotion.get(), i);
            addDropItem(drops, level, pos, ModItems.thPotion.get(), i);
            addDropItem(drops, level, pos, ModItems.danmakuMaterial.get(), i);
        }
    }

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
                NetWorkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new TotemPacket(totem, player));
                if (player instanceof ServerPlayer serverplayer) {
                    serverplayer.awardStat(Stats.ITEM_USED.get(ModItems.extend.get()), 1);
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

    private static boolean dimensionIsOkay(ResourceLocation id) {
        return id.equals(Level.OVERWORLD.location()) || id.equals(Level.NETHER.location()) || id.equals(Level.END.location());
    }

    private static boolean dimensionIsOverworld(ResourceLocation id) {
        return id.equals(Level.OVERWORLD.location());
    }

    /**
     * 添加掉落
     */
    public static void addDropItem(Collection<ItemEntity> drops, Level level, BlockPos pos, Item item, int number){
        drops.add(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(item, number)));
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

        if (player.isCreative()) return item.getDefaultInstance();
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
