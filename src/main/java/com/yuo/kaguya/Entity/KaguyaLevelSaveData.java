package com.yuo.kaguya.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class KaguyaLevelSaveData extends SavedData {
    public static final String GAP_POS = "kaguya_gap_pos";
    private final Map<UUID, BlockPos> blockPosList = new HashMap<>();
    private static final String NBT_GAP = "kaguya:gap";
    private static final String NBT_UUID = "kaguya:uuid";
    private static final String NBT_POS_X = "kaguya:pos_x";
    private static final String NBT_POS_Y = "kaguya:pos_y";
    private static final String NBT_POS_Z = "kaguya:pos_z";

    public KaguyaLevelSaveData() {
    }

    public KaguyaLevelSaveData(CompoundTag tag) {
        this.load(tag);
    }

    public void addPos(UUID uuid, BlockPos pos) {
        blockPosList.put(uuid, pos);
        setDirty();
    }

    public BlockPos getPos(UUID uuid){
        BlockPos pos = blockPosList.get(uuid);
        return pos != null ? pos : BlockPos.ZERO;
    }

    public void removePos(UUID uuid) {
        blockPosList.remove(uuid);
        setDirty();
    }

    public Map<UUID, BlockPos> getBlockPosList(){
        return Collections.unmodifiableMap(blockPosList);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag posList = new ListTag();
        for (Map.Entry<UUID, BlockPos> entry : blockPosList.entrySet()) {
            CompoundTag posTag = new CompoundTag();
            posTag.putUUID(NBT_UUID, entry.getKey());
            BlockPos value = entry.getValue();
            posTag.putInt(NBT_POS_X, value.getX());
            posTag.putInt(NBT_POS_Y, value.getY());
            posTag.putInt(NBT_POS_Z, value.getZ());
            posList.add(posTag);
        }
        tag.put(NBT_GAP, posList);
        return tag;
    }

    private KaguyaLevelSaveData load(CompoundTag tag) {
        Tag listTag = tag.get(NBT_GAP);
        if (listTag != null) {
            ListTag posList = (ListTag) listTag;
            for (int i = 0; i < posList.size(); i++) {
                CompoundTag posTag = posList.getCompound(i);
                UUID uuid = posTag.getUUID(NBT_UUID);
                int x = posTag.getInt(NBT_POS_X);
                int y = posTag.getInt(NBT_POS_Y);
                int z = posTag.getInt(NBT_POS_Z);
                BlockPos pos = new BlockPos(x, y, z);
                this.addPos(uuid, pos);
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
        return dataStorage.computeIfAbsent(
                KaguyaLevelSaveData::new,
                KaguyaLevelSaveData::new,
                KaguyaLevelSaveData.GAP_POS
        );
    }

    /**
     * 传送实体
     * @param living 带传送的实体
     * @param gap 隙间
     */
    public static void tpEntity(LivingEntity living, GapEntity gap, Level level) {
        KaguyaLevelSaveData saveData;
        BlockPos tpPos;
        if (!level.isClientSide && level instanceof ServerLevel sl){
            saveData = get(level);
            UUID uuid = saveData.getTpUuid(gap);
            if (living instanceof Player player){
                if (saveData.getBlockPosList().size() < 2) {
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }
                if (uuid == null){
                    player.displayClientMessage(Component.translatable("info.kaguya.sukima_gap.no_tp"), true);
                    return;
                }
            }
            if (uuid != null){
                Entity entity = sl.getEntity(uuid);
                if (entity instanceof GapEntity gapEntity){
                    gapEntity.setCoolTime(20);
                }

                tpPos = saveData.getBlockPosList().get(uuid).relative(gap.getDirection().getClockWise(), 2);
                living.teleportTo(tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5);
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
        Map<UUID, BlockPos> posList = getBlockPosList();
        Map<UUID, Double> distanceMap = new HashMap<>();
        UUID gapUUID = gap.getUUID();
        BlockPos gapPos = posList.getOrDefault(gapUUID, BlockPos.ZERO);
        for (Map.Entry<UUID, BlockPos> entry : posList.entrySet()) {
            BlockPos pos = entry.getValue();
            if (!gapUUID.equals(entry.getKey())) { //非原点
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
}
