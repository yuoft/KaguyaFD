package com.yuo.kaguya.Entity;

import com.mojang.datafixers.util.Either;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder.ChunkLoadingFailure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KaguyaLevelSaveData extends SavedData {
    public static final String GAP_POS = "kaguya_gap_pos";
    private final Map<UUID, GapSaveData> gapSaveData = new HashMap<>();
    private static final String NBT_GAP = "kaguya:gap";
    private static final String NBT_UUID = "kaguya:uuid";
    private static final String NBT_POS_X = "kaguya:pos_x";
    private static final String NBT_POS_Y = "kaguya:pos_y";
    private static final String NBT_POS_Z = "kaguya:pos_z";
    private static final String NBT_COLOR = "kaguya:color";
    private static final String NBT_DIMENSION = "kaguya:dimension";

    public KaguyaLevelSaveData() {
    }

    public KaguyaLevelSaveData(CompoundTag tag) {
        this.load(tag);
    }

    /**
     * 添加隙间
     * @param uuid id
     * @param pos 坐标
     * @param color 颜色
     */
    public void addGapData(UUID uuid, BlockPos pos, DanmakuColor color,ResourceKey<Level> dimension) {
        gapSaveData.put(uuid, new GapSaveData(pos, color, dimension));
        setDirty();
    }

    public GapSaveData getGapSaveData(UUID uuid){
        GapSaveData data = gapSaveData.get(uuid);
        return data != null ? data : GapSaveData.EMPTY;
    }

    public void removeData(UUID uuid) {
        gapSaveData.remove(uuid);
        setDirty();
    }

    public Map<UUID, GapSaveData> getGapSaveData(){
        return Collections.unmodifiableMap(gapSaveData);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag posList = new ListTag();
        for (Map.Entry<UUID, GapSaveData> entry : gapSaveData.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID(NBT_UUID, entry.getKey());

            GapSaveData data = entry.getValue();
            BlockPos pos = data.getPos();
            nbt.putInt(NBT_POS_X, pos.getX());
            nbt.putInt(NBT_POS_Y, pos.getY());
            nbt.putInt(NBT_POS_Z, pos.getZ());

            nbt.putInt(NBT_COLOR, data.getColor().ordinal());
            nbt.putString(NBT_DIMENSION, data.getDimension().location().toString());
            posList.add(nbt);
        }
        tag.put(NBT_GAP, posList);
        return tag;
    }

    private KaguyaLevelSaveData load(CompoundTag tag) {
        Tag listTag = tag.get(NBT_GAP);
        if (listTag != null) {
            ListTag posList = (ListTag) listTag;
            for (int i = 0; i < posList.size(); i++) {
                CompoundTag nbt = posList.getCompound(i);
                UUID uuid = nbt.getUUID(NBT_UUID);
                int x = nbt.getInt(NBT_POS_X);
                int y = nbt.getInt(NBT_POS_Y);
                int z = nbt.getInt(NBT_POS_Z);
                int colorId = nbt.getInt(NBT_COLOR);
                String key = nbt.getString(NBT_DIMENSION);

                BlockPos pos = new BlockPos(x, y, z);
                DanmakuColor color = DanmakuColor.getColor(colorId);
                this.addGapData(uuid, pos, color, loadDimension(key));
            }
        }

        return this;
    }

    public static KaguyaLevelSaveData get(Level level) {
        if (!(level instanceof ServerLevel)) {
            throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
        }
        ServerLevel world = level.getServer().getLevel(ServerLevel.OVERWORLD);
        if (world == null)
            throw new RuntimeException("ServerLevel is null.");
        DimensionDataStorage dataStorage = world.getDataStorage();
        return dataStorage.computeIfAbsent(KaguyaLevelSaveData::new, KaguyaLevelSaveData::new, KaguyaLevelSaveData.GAP_POS);
    }

    /**
     * 传送实体
     * @param living 带传送的实体
     * @param gap 隙间
     */
    public static void tpEntity(LivingEntity living, GapEntity gap, Level level) {
        KaguyaLevelSaveData saveData;
        BlockPos tpPos;
        if (!level.isClientSide && level instanceof ServerLevel){
            saveData = get(level);
            UUID uuid = saveData.getTpUuid(gap);
            Map<UUID, GapSaveData> dataMap = saveData.getGapSaveData();

            if (living instanceof Player player){
                if (dataMap.size() < 2) {
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }
                if (uuid == null){
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }
            }
            if (uuid != null){
                GapSaveData data = dataMap.get(uuid);
                ResourceKey<Level> dimension = data.getDimension();
                Entity entity = findEntityInDimension(uuid, dimension);
                if (entity == null && living instanceof Player player){
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }

                if (entity instanceof GapEntity gapEntity){
                    gapEntity.setCoolTime(20);
                }
                tpPos = data.getPos().relative(gap.getDirection().getClockWise(), 1);
                if (!dimension.equals(level.dimension())){ //跨纬度传送
                    ServerLevel level1 = level.getServer().getLevel(dimension);
                    if (level1 != null) {
//                        living.changeDimension(level1);
                        living.teleportTo(level1, tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5,
                                Set.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z, RelativeMovement.X_ROT, RelativeMovement.Y_ROT)
                                ,living.getYRot(), living.getXRot());
                    }
                }else living.teleportTo(tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5);

                gap.setCoolTime(20);
            }
        }
    }

    /**
     * 获取要tp的实体uuid
     */
    public UUID getTpUuid(GapEntity gap){
        Optional<Entry<UUID, Double>> first = getGapDistance(gap).entrySet().stream().findFirst();
        return first.map(Entry::getKey).orElse(null);
    }

    /**
     * 获取其它隙间距离原点隙间的距离  最少大于5
     * @param gap 原点
     * @return 距离map
     */
    public Map<UUID, Double> getGapDistance(GapEntity gap){
        Map<UUID, GapSaveData> saveData = getGapSaveData();
        Map<UUID, Double> distanceMap = new HashMap<>();
        UUID gapUUID = gap.getUUID();
        DanmakuColor color = gap.getColor();
        BlockPos gapPos = saveData.getOrDefault(gapUUID, GapSaveData.EMPTY).getPos(); //当前坐标
        Map<UUID, GapSaveData> instantData = new HashMap<>(); //同色集合
        for (Entry<UUID, GapSaveData> entry : saveData.entrySet()) {
            if (gapUUID.equals(entry.getKey())) continue;
            GapSaveData value = entry.getValue();
            if (value.getColor() == color) {
                instantData.put(entry.getKey(), value);
            }
        }


        for (Map.Entry<UUID, GapSaveData> entry : instantData.entrySet()) {
            GapSaveData data = entry.getValue();
            if (!gapUUID.equals(entry.getKey())) { //非原点
                BlockPos pos = data.getPos();
                double dist = Math.sqrt(gapPos.distToLowCornerSqr(pos.getX(), pos.getY(), pos.getZ()));
                if (dist > 5){
                    distanceMap.put(entry.getKey(), dist);
                }
            }
        }

        // 按值升序排序
        return distanceMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 在指定维度中查找实体
     */
    @Nullable
    public static Entity findEntityInDimension(UUID uuid, ResourceKey<Level> dimension) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }

        ServerLevel level = server.getLevel(dimension);
        if (level == null) {
            return null;
        }
        Entity entity = level.getEntity(uuid);
        if (entity != null) {
            return entity;
        }

        return findEntityByForceLoading(uuid, level);
    }

    /**
     * 通过强制加载区块查找实体
     */
    @Nullable
    private static Entity findEntityByForceLoading(UUID uuid, ServerLevel level) {
        // 从保存数据中获取实体的最后已知位置
        KaguyaLevelSaveData saveData = KaguyaLevelSaveData.get(level);
        KaguyaLevelSaveData.GapSaveData gapData = saveData.getGapSaveData(uuid);

        if (gapData == null) {
            return null;
        }

        BlockPos pos = gapData.getPos();

        // 强制加载实体所在的区块
        ChunkPos chunkPos = new ChunkPos(pos);
        CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> chunkFuture = level.getChunkSource().getChunkFuture(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

        try {
            // 等待区块加载完成
            ChunkAccess chunk = chunkFuture.get(5, TimeUnit.SECONDS).orThrow();

            // 再次尝试获取实体
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }

            // 如果还是没有，尝试从区块实体列表中查找
//            if (chunk instanceof LevelChunk levelChunk) {
//                for (Entity chunkEntity : levelChunk.getEntities()) {
//                    if (chunkEntity.getUUID().equals(uuid)) {
//                        return chunkEntity;
//                    }
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从NBT读取维度
     */
    public static ResourceKey<Level> loadDimension(String key) {
        ResourceLocation location = KaguyaUtils.def(key);
        return ResourceKey.create(Registries.DIMENSION, location);
    }

    /**
     * 隙间数据
     */
    public static class GapSaveData {
        private final BlockPos pos;
        private final DanmakuColor color;
        private final ResourceKey<Level> dimension;
        public static final GapSaveData EMPTY = new GapSaveData(BlockPos.ZERO, DanmakuColor.GRAY, Level.OVERWORLD);

        public GapSaveData(BlockPos pos, DanmakuColor color, ResourceKey<Level> dimension) {
            this.pos = pos;
            this.color = color;
            this.dimension = dimension;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }

        public BlockPos getPos() {
            return pos;
        }

        public DanmakuColor getColor() {
            return color;
        }
    }
}
