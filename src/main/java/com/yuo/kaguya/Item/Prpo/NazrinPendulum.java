package com.yuo.kaguya.Item.Prpo;

import com.yuo.kaguya.Event.OutLineEvent;
import com.yuo.kaguya.Item.KaguyaPrpo;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class NazrinPendulum extends KaguyaPrpo {
    // 矿石颜色映射表
    public static final Map<String, Integer> ORE_COLORS = new HashMap<>();

    static {
        // 初始化颜色映射
        initOreColors();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        BlockPos centerPos = player.getOnPos();
        int box = 16; //矿物透视范围
        AABB aabb = new AABB(centerPos.offset(-box, -box, -box), centerPos.offset(box, box, box));

        if (!level.isClientSide) {
            // 方法1：使用 toList() 复制所有坐标
            Stream<BlockPos> posStream = BlockPos.betweenClosedStream(aabb);
            posStream.map(BlockPos::immutable)  // 关键：将 mutable BlockPos 转换为 immutable
                    .forEach(pos -> {
                        BlockState state = level.getBlockState(pos);
                        if (isOre(state)) {
                            int color = getColorForOre(state);
                            OutLineEvent.addHighlightBlock(pos, color, 1.0f, 20 * 1000);
                        }
                    });
        }

        player.playSound(SoundEvents.PLAYER_LEVELUP);
        player.getCooldowns().addCooldown(this, 20 * 10);
        return super.use(level, player, hand);
    }

    private static void initOreColors() {
        // Minecraft 1.20 实际矿石颜色（RGB 值，不含 Alpha）

        // 煤炭相关
        ORE_COLORS.put("minecraft:coal_ore", 0x444444);          // 暗灰色
        ORE_COLORS.put("minecraft:deepslate_coal_ore", 0x333333); // 更深灰色

        // 铁相关
        ORE_COLORS.put("minecraft:iron_ore", 0xD8D8D8);          // 浅灰色
        ORE_COLORS.put("minecraft:deepslate_iron_ore", 0xA0A0A0); // 中灰色

        // 铜相关
        ORE_COLORS.put("minecraft:copper_ore", 0xB87333);        // 铜橙色
        ORE_COLORS.put("minecraft:deepslate_copper_ore", 0x9C5A2B); // 深铜色

        // 金相关
        ORE_COLORS.put("minecraft:gold_ore", 0xFFD700);          // 金色
        ORE_COLORS.put("minecraft:deepslate_gold_ore", 0xD4AF37); // 深金色
        ORE_COLORS.put("minecraft:nether_gold_ore", 0xFFD700);   // 下界金（同金色）

        // 红石相关
        ORE_COLORS.put("minecraft:redstone_ore", 0xFF0000);      // 红色
        ORE_COLORS.put("minecraft:deepslate_redstone_ore", 0xCC0000); // 深红色

        // 绿宝石相关
        ORE_COLORS.put("minecraft:emerald_ore", 0x00CC00);       // 绿色
        ORE_COLORS.put("minecraft:deepslate_emerald_ore", 0x00AA00); // 深绿色

        // 钻石相关
        ORE_COLORS.put("minecraft:diamond_ore", 0x00FFFF);       // 青蓝色
        ORE_COLORS.put("minecraft:deepslate_diamond_ore", 0x00CCCC); // 深青蓝色

        // 青金石相关
        ORE_COLORS.put("minecraft:lapis_ore", 0x4169E1);         // 蓝色
        ORE_COLORS.put("minecraft:deepslate_lapis_ore", 0x3159D1); // 深蓝色

        // 石英
        ORE_COLORS.put("minecraft:nether_quartz_ore", 0xFFFFFF); // 白色

        // 远古残骸
        ORE_COLORS.put("minecraft:ancient_debris", 0x8B4513);    // 深棕色

        // 其他原版方块
        ORE_COLORS.put("minecraft:glowstone", 0xFFBC5E);         // 荧石 - 橙黄色
        ORE_COLORS.put("minecraft:amethyst_block", 0x9A5CC6);    // 紫水晶 - 紫色
        ORE_COLORS.put("minecraft:budding_amethyst", 0x9A5CC6);  // 紫水晶母岩

    }

    public static void addOre(String ore, int color) {
        if (!ORE_COLORS.containsKey(ore)) {
            ORE_COLORS.put(ore, color);
        }
    }

    /**
     * 是否是矿物
     */
    private boolean isOre(BlockState state){
        return ORE_COLORS.containsKey(getBlockId(state));
    }

    /**
     * 获取矿物颜色
     */
    private int getColorForOre(BlockState state) {
        String blockId = getBlockId(state);

        // 首先尝试从映射表获取
        if (ORE_COLORS.containsKey(blockId)) {
            return ORE_COLORS.get(blockId);
        }

        // 如果没有精确匹配，尝试模糊匹配
        for (Map.Entry<String, Integer> entry : ORE_COLORS.entrySet()) {
            if (blockId.contains(entry.getKey().replace("minecraft:", "").split("_")[0])) {
                return entry.getValue();
            }
        }

        // 默认颜色
        return 0xFFFFFF;
    }

    /**
     * 获取方块ID
     */
    private String getBlockId(BlockState state) {
        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return registryName != null ? registryName.toString() : "";
    }
}
