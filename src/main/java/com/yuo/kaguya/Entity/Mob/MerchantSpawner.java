package com.yuo.kaguya.Entity.Mob;

import com.yuo.kaguya.Entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class MerchantSpawner implements CustomSpawner {

    private static final int SPAWN_INTERVAL = 12000; // 10 分钟尝试一次
    private static final int MAX_SPAWN_CHANCE = 75;   // 最高尝试概率 (%)
    private static final int SPAWN_CHANCE_STEP = 25;  // 每次失败增加的概率
    private static final double FINAL_SPAWN_CHANCE = 0.25; // 尝试成功后的最终生成概率 (25%)
    private static final int SPAWN_RADIUS = 48;       // 玩家周围 48 格
    private static final int MAX_SPAWN_ATTEMPTS = 10; // 寻找有效位置的次数

    private int spawnTimer = SPAWN_INTERVAL;
    private int spawnChance = 0; // 当前尝试概率，0 表示尚未开始递增

    @Override
    public int tick(ServerLevel level, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnAnimals) return 0;

        if (--this.spawnTimer > 0) {
            return 0;
        }
        this.spawnTimer = SPAWN_INTERVAL;

        // 概率递增逻辑
        if (this.spawnChance < MAX_SPAWN_CHANCE) {
            this.spawnChance = Math.min(this.spawnChance + SPAWN_CHANCE_STEP, MAX_SPAWN_CHANCE);
        }

        RandomSource random = level.getRandom();

        // 1. 尝试概率 (2.5% -> 5% -> 7.5% 的源头)
        if (random.nextInt(100) >= this.spawnChance) {
            return 0;
        }

        // 2. 最终生成概率 (10%)
        if (random.nextDouble() >= FINAL_SPAWN_CHANCE) {
            return 0;
        }

        // 3. 选择一个随机玩家作为生成中心
        if (level.players().isEmpty()) {
            this.spawnChance = 0; // 没有玩家则重置概率
            return 0;
        }
        var player = level.players().get(random.nextInt(level.players().size()));
        Vec3 center = player.position();

        // 4. 尝试寻找有效位置并生成 (早苗或霖之助)
        boolean spawned = trySpawnMerchant(level, center, random);
        if (spawned) {
            this.spawnChance = 0; // 成功后重置概率循环
            return 1;
        }
        return 0;
    }

    private boolean trySpawnMerchant(ServerLevel level, Vec3 center, RandomSource random) {
        for (int i = 0; i < MAX_SPAWN_ATTEMPTS; i++) {
            // 在半径 48 格内随机偏移
            double offsetX = (random.nextDouble() - 0.5) * 2 * SPAWN_RADIUS;
            double offsetZ = (random.nextDouble() - 0.5) * 2 * SPAWN_RADIUS;
            int x = (int) (center.x + offsetX);
            int z = (int) (center.z + offsetZ);

            // 获取地表 Y 坐标（不会生成在洞穴里）
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            // 简单的空间检查
            if (!level.isPositionEntityTicking(pos)) continue;
            if (!level.noCollision(EntityType.WANDERING_TRADER.getAABB(x, y, z))) continue;

            // 随机选择早苗或霖之助（各 50%）
            EntityType<? extends Mob> type = random.nextBoolean()
                    ? ModEntityTypes.KOCHIYA_SANAE.get()
                    : ModEntityTypes.MORICHIKA_RINNOSUKE.get();

            Mob merchant = type.create(level);
            if (merchant == null) continue;

            merchant.moveTo(x + 0.5, y, z + 0.5, random.nextFloat() * 360F, 0);
            merchant.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                    MobSpawnType.EVENT, null, null);

            if (level.addFreshEntity(merchant)) {
                // 写入消失计时器（类似流浪商人的 DespawnDelay）
                merchant.getPersistentData().putInt("MerchantDespawnTimer", 20 * 60 * 40); // 40 分钟
                return true;
            }
        }
        return false;
    }
}