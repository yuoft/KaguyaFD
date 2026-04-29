package com.yuo.kaguya.Entity;

import com.mojang.datafixers.util.Either;
import com.yuo.kaguya.Entity.Danmaku.DanmakuColor;
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
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SukimaGapLevelSaveData extends SavedData {
    public static final String GAP_POS = "kaguya_gap_pos";
    private final Map<UUID, GapSaveData> gapSaveData = new HashMap<>();
    private static final String NBT_GAP = "kaguya:gap";
    private static final String NBT_UUID = "kaguya:uuid";
    private static final String NBT_POS_X = "kaguya:pos_x";
    private static final String NBT_POS_Y = "kaguya:pos_y";
    private static final String NBT_POS_Z = "kaguya:pos_z";
    private static final String NBT_COLOR = "kaguya:color";
    private static final String NBT_YAW = "kaguya:yaw";
    private static final String NBT_IS_TICKING = "kaguya:is_ticking";
    private static final String NBT_DIMENSION = "kaguya:dimension";

    public SukimaGapLevelSaveData() {
    }

    public SukimaGapLevelSaveData(CompoundTag tag) {
        this.load(tag);
    }

    public void addGapData(UUID uuid, Vec3 pos, float yaw, DanmakuColor color, ResourceKey<Level> dimension, boolean ticking) {
        gapSaveData.put(uuid, new GapSaveData(pos, yaw, color, dimension, ticking));
        setDirty();
    }

    public GapSaveData getGapSaveData(UUID uuid) {
        return gapSaveData.getOrDefault(uuid, GapSaveData.EMPTY);
    }

    public void removeData(UUID uuid) {
        gapSaveData.remove(uuid);
        setDirty();
    }

    public Map<UUID, GapSaveData> getGapSaveData() {
        return Collections.unmodifiableMap(gapSaveData);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag posList = new ListTag();
        for (Map.Entry<UUID, GapSaveData> entry : gapSaveData.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID(NBT_UUID, entry.getKey());

            GapSaveData data = entry.getValue();
            Vec3 pos = data.getPos();
            nbt.putDouble(NBT_POS_X, pos.x);
            nbt.putDouble(NBT_POS_Y, pos.y);
            nbt.putDouble(NBT_POS_Z, pos.z);
            nbt.putFloat(NBT_YAW, data.getYaw());
            nbt.putBoolean(NBT_IS_TICKING, data.isTicking());

            nbt.putInt(NBT_COLOR, data.getColor().ordinal());
            nbt.putString(NBT_DIMENSION, data.getDimension().location().toString());
            posList.add(nbt);
        }
        tag.put(NBT_GAP, posList);
        return tag;
    }

    private SukimaGapLevelSaveData load(CompoundTag tag) {
        Tag listTag = tag.get(NBT_GAP);
        if (listTag != null) {
            ListTag posList = (ListTag) listTag;
            for (int i = 0; i < posList.size(); i++) {
                CompoundTag nbt = posList.getCompound(i);
                UUID uuid = nbt.getUUID(NBT_UUID);
                DanmakuColor color = DanmakuColor.getColor(nbt.getInt(NBT_COLOR));
                String key = nbt.getString(NBT_DIMENSION);
                ResourceKey<Level> dimension = loadDimension(key);

                Vec3 pos;
                double x = nbt.getDouble(NBT_POS_X);
                double y = nbt.getDouble(NBT_POS_Y);
                double z = nbt.getDouble(NBT_POS_Z);
                pos = new Vec3(x, y, z);
                float yaw = nbt.getFloat(NBT_YAW);
                boolean isTicking = nbt.getBoolean(NBT_IS_TICKING);
                this.addGapData(uuid, pos, yaw, color, dimension, isTicking);
            }
        }
        return this;
    }

    public static SukimaGapLevelSaveData get(Level level) {
        if (!(level instanceof ServerLevel)) {
            throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
        }
        ServerLevel world = level.getServer().getLevel(ServerLevel.OVERWORLD);
        if (world == null)
            throw new RuntimeException("ServerLevel is null.");
        DimensionDataStorage dataStorage = world.getDataStorage();
        return dataStorage.computeIfAbsent(SukimaGapLevelSaveData::new, SukimaGapLevelSaveData::new, SukimaGapLevelSaveData.GAP_POS);
    }

    /**
     * 传送实体
     */
    public static void tpEntity(Entity living, SukimaGapEntity gap, Level level) {
        // 防止隙间自身被传送
        if (living instanceof SukimaGapEntity) return;

        if (!level.isClientSide && level instanceof ServerLevel) {
            SukimaGapLevelSaveData saveData = get(level);
            UUID targetUuid = saveData.getTpUuid(gap);
            Map<UUID, GapSaveData> dataMap = saveData.getGapSaveData();

            if (living instanceof Player player) {
                if (dataMap.size() < 2) {
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }
                if (targetUuid == null) {
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }
            }
            if (targetUuid != null) {
                GapSaveData data = dataMap.get(targetUuid);
                ResourceKey<Level> dimension = data.getDimension();
                Entity entity = findEntityInDimension(targetUuid, dimension);
                if (entity == null && living instanceof Player player) {
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }

                if (entity instanceof SukimaGapEntity gapEntity) {
                    gapEntity.setCoolTime(20);
                }

                // 计算前方 2 格偏移（基于目标隙间的朝向）
                float yaw = data.getYaw();
                double rad = Math.toRadians(yaw);
                double offX = Math.sin(rad) * 2.0;
                double offZ = Math.cos(rad) * 2.0;
                Vec3 tpPos = data.getPos().add(offX, 0, offZ);

                if (!dimension.equals(level.dimension())) {
                    ServerLevel targetWorld = level.getServer().getLevel(dimension);
                    if (targetWorld != null) {
                        living.teleportTo(targetWorld, tpPos.x, tpPos.y, tpPos.z,
                                Set.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z,
                                        RelativeMovement.X_ROT, RelativeMovement.Y_ROT),
                                living.getYRot(), living.getXRot());
                    }
                } else {
                    living.teleportTo(tpPos.x, tpPos.y, tpPos.z);
                }
                gap.setCoolTime(20);
            }
        }
    }

    /**
     * 获取要tp的实体uuid
     */
    public UUID getTpUuid(SukimaGapEntity gap) {
        Optional<Entry<UUID, Double>> first = getGapDistance(gap).entrySet().stream().findFirst();
        return first.map(Entry::getKey).orElse(null);
    }

    /**
     * 获取其它隙间距离原点隙间的距离  最少大于5
     * 临时隙间优先传送临时隙间
     * @param gap 原点隙间
     * @return 距离map
     */
    public Map<UUID, Double> getGapDistance(SukimaGapEntity gap) {
        Map<UUID, GapSaveData> saveData = getGapSaveData();
        UUID gapUUID = gap.getUUID();
        DanmakuColor color = gap.getColor();
        Vec3 gapPos = saveData.getOrDefault(gapUUID, GapSaveData.EMPTY).getPos();

        // 收集候选隙间
        List<Map.Entry<UUID, GapSaveData>> candidates = new ArrayList<>();
        for (Map.Entry<UUID, GapSaveData> entry : saveData.entrySet()) {
            if (gapUUID.equals(entry.getKey())) continue;
            GapSaveData data = entry.getValue();
            if (data.getColor() == color) {
                double dist = gapPos.distanceTo(data.getPos());
                if (dist > 5.0) {
                    candidates.add(entry);
                }
            }
        }

        // 排序：临时隙间优先，同类型则距离近优先
        candidates.sort((a, b) -> {
            GapSaveData dataA = a.getValue();
            GapSaveData dataB = b.getValue();
            // 临时隙间（ticking=true）排在前面
            if (dataA.isTicking() != dataB.isTicking()) {
                return Boolean.compare(dataB.isTicking(), dataA.isTicking()); // true 在前
            }
            // 都是临时或都不是临时，按距离升序
            double distA = gapPos.distanceTo(dataA.getPos());
            double distB = gapPos.distanceTo(dataB.getPos());
            return Double.compare(distA, distB);
        });

        // 构建 LinkedHashMap 保持顺序
        Map<UUID, Double> result = new LinkedHashMap<>();
        for (Map.Entry<UUID, GapSaveData> entry : candidates) {
            double dist = gapPos.distanceTo(entry.getValue().getPos());
            result.put(entry.getKey(), dist);
        }
        return result;
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
        SukimaGapLevelSaveData saveData = SukimaGapLevelSaveData.get(level);
        SukimaGapLevelSaveData.GapSaveData gapData = saveData.getGapSaveData(uuid);

        if (gapData == null || gapData == GapSaveData.EMPTY) {
            return null;
        }

        Vec3 pos = gapData.getPos();
        // 将 Vec3 转为 BlockPos 用于强制加载区块（大约中心点）
        BlockPos blockPos = BlockPos.containing(pos);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> chunkFuture =
                level.getChunkSource().getChunkFuture(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

        try {
            chunkFuture.get(5, TimeUnit.SECONDS).orThrow();
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
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
        private final Vec3 pos;
        private final float yaw;        //隙间的水平朝向角度
        private final DanmakuColor color;
        private final ResourceKey<Level> dimension; //所在维度
        private final boolean ticking;   //临时隙间？
        public static final GapSaveData EMPTY = new GapSaveData(Vec3.ZERO, 0f, DanmakuColor.GRAY, Level.OVERWORLD, false);

        public GapSaveData(Vec3 pos, float yaw, DanmakuColor color, ResourceKey<Level> dimension, boolean ticking) {
            this.pos = pos;
            this.yaw = yaw;
            this.color = color;
            this.dimension = dimension;
            this.ticking = ticking;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }

        public Vec3 getPos() {
            return pos;
        }

        public DanmakuColor getColor() {
            return color;
        }

        public float getYaw() {
            return yaw;
        }

        public boolean isTicking() { return ticking; }
    }
}