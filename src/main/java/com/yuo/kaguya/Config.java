package com.yuo.kaguya;

import com.yuo.kaguya.Item.Prpo.NazrinPendulum;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {
    public static ForgeConfigSpec SERVER_CONFIG;
    public static ServerConfig SERVER;
    private static final Logger LOGGER = LogManager.getLogger(Kaguya.MOD_ID);

    static {
        {
            final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
            SERVER_CONFIG = specPair.getRight();
            SERVER = specPair.getLeft();
        }
    }

    public static Set<String> customOreColors = new HashSet<>();

    public static void loadConfig(){
        getCustomSingularities(SERVER.custonOreColors.get(), customOreColors);
    }


    /**
     * 通过配置文件获取自定义矿石颜色映射
     * @param list 字符串列表
     * @param set 颜色集合
     */
    private static void getCustomSingularities(List<? extends String> list, Set<String> set){
        int size = list.size();
        if (size == 0) return;
        if (size % 2 != 0){
            LOGGER.error("Error OreColor definition for [String size]");
        }else {
            for (int i = 0; i < list.size(); i += 2){
                String s = list.get(i);
                if (!isTypeFlag(s)) {
                    LOGGER.error("Error ore name definition [a-z_0-9] for [{}]", list.get(i));
                    continue;
                }
                String s1 = list.get(i + 1);
                if (!s1.matches("^color:0x[a-z0-9]{6}$")) {
                    LOGGER.error("Error color code definition >>16 for [{}]", s1);
                    continue;
                }
                String[] split = s.split(":");
                String[] split1 = s1.split(":");
                NazrinPendulum.addOre(split[0], Integer.parseInt(split1[1]));
                set.add(split[1]);
            }
        }
    }

    /**
     * 判断字符串是否符合要求
     * @param s 字符串
     * @return 符合
     */
    private static boolean isTypeFlag(String s){
        //字符串结构是否正确
        if (s.matches("^name:[a-z_]+$") && !s.matches(":_")){
            String[] split = s.split(":");
            return !NazrinPendulum.ORE_COLORS.containsKey(split[1]); //名称是否冲突
        }
        return false;
    }

    public static class ServerConfig{

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> custonOreColors; //自定义矿石颜色

        public ServerConfig(ForgeConfigSpec.Builder builder){
            builder.comment("Custom ore color.For matching with ore perspective" +
                    " eg:[{\"name:silver_ore\",\"color:0x112233\"},{...}]").push("oreColors");
            this.custonOreColors = buildConfig(builder, "Custom Ore Color List", "Custom ore outline color list");
            builder.pop();
        }
    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.DoubleValue buildDouble(ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.ConfigValue<List<? extends String>> buildConfig(ForgeConfigSpec.Builder builder, String name, String comment){
        return builder.comment(comment).translation(name).defineList(name, Collections.emptyList(), s -> s instanceof String && KaguyaUtils.tayParse((String) s) != null);
    }

}
