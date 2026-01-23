package com.yuo.kaguya.Data;

import com.google.gson.JsonObject;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Item.ModColorItemUtils;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.Prpo.SukimaGap;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ModDataRecipes extends RecipeProvider {
    public ModDataRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        //材料
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.bigPotion.get(), 1)
                .define('x', ModItems.smallPotion.get())
                .pattern("xxx").pattern("xxx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.bigPotion.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.danmakuMaterial.get(), 64).requires(Items.GLOWSTONE_DUST).requires(Items.GUNPOWDER)
                .unlockedBy("has_item", has(ModItems.danmakuMaterial.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.extend.get(), 1)
                .define('x', ModItems.smallPotion.get()).define('y', Items.TOTEM_OF_UNDYING)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.extend.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlAqua.get(), 1)
                .define('x', Items.ICE).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlAqua.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlBlue.get(), 1)
                .define('x', Items.LAPIS_LAZULI).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlBlue.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlGreen.get(), 1)
                .define('x', Items.EMERALD).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlGreen.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlOrange.get(), 1)
                .define('x', Items.BLAZE_POWDER).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlOrange.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlPurple.get(), 1)
                .define('x', Items.OBSIDIAN).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlPurple.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlRed.get(), 1)
                .define('x', Items.REDSTONE).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlRed.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlWhite.get(), 1)
                .define('x', Items.IRON_INGOT).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlWhite.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.houraiPearlYellow.get(), 1)
                .define('x', Items.GLOWSTONE_DUST).define('y', Items.ENDER_PEARL)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.houraiPearlYellow.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.onmyoudama.get(), 1)
                .define('x', Items.REDSTONE).define('y', Items.QUARTZ).define('z', Items.ENDER_PEARL)
                .pattern("xxy").pattern("xzy").pattern("xyy")
                .unlockedBy("has_item", has(ModItems.onmyoudama.get())).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.hisouSword.get(), 1)
                .define('x', Items.FIRE_CHARGE).define('y', Items.GLOWSTONE).define('z', Items.BLAZE_POWDER)
                .define('m', Items.DIAMOND_SWORD).define('n', Items.BLAZE_ROD)
                .pattern(" xy").pattern("zmx").pattern("nz ")
                .unlockedBy("has_item", has(ModItems.hisouSword.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.yuyukoOugi.get(), 1)
                .define('x', Items.REDSTONE).define('y', Items.DIAMOND).define('z', Items.LAPIS_LAZULI)
                .define('m', Items.STICK).define('n', Items.PAPER).define('o', ModItems.soulTorch.get())
                .pattern("xyz").pattern("mnm").pattern("omo")
                .unlockedBy("has_item", has(ModItems.yuyukoOugi.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.koyasugai.get(), 1)
                .define('x', Items.GHAST_TEAR).define('y', Items.FEATHER).define('z', Items.EGG)
                .define('m', Items.WHEAT)
                .pattern("xxx").pattern("yzy").pattern("mmm")
                .unlockedBy("has_item", has(ModItems.koyasugai.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ibukihyou.get(), 1)
                .define('x', Items.PAPER).define('y', Items.LAPIS_BLOCK).define('z', Items.NETHER_WART)
                .pattern("xyy").pattern("yzy").pattern("yyx")
                .unlockedBy("has_item", has(ModItems.ibukihyou.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.danmakuCraft.get(), 1)
                .define('x', ModItems.danmakuMaterial.get()).define('y', Items.CRAFTING_TABLE)
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.danmakuCraft.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.fireRatBobe.get(), 1)
                .define('x', Items.BLAZE_POWDER).define('y', Items.DIAMOND).define('z', Items.LEATHER_CHESTPLATE)
                .pattern("xyx").pattern("xzx").pattern("x x")
                .unlockedBy("has_item", has(ModItems.fireRatBobe.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.kappaHead.get(), 1)
                .define('x', Items.LILY_PAD).define('y', Items.WATER_BUCKET).define('z', Items.SLIME_BALL)
                .define('m', Items.LEATHER_HELMET)
                .pattern("xyx").pattern("zmz").pattern("   ")
                .unlockedBy("has_item", has(ModItems.kappaHead.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.marisaHead.get(), 1)
                .define('x', Items.OBSIDIAN).define('y', Items.WHITE_WOOL).define('z', Items.BLACK_WOOL)
                .define('m', Items.DIAMOND_HELMET)
                .pattern(" x ").pattern("ymy").pattern("zzz")
                .unlockedBy("has_item", has(ModItems.marisaHead.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.suwakoHead.get(), 1)
                .define('x', Items.ENDER_EYE).define('y', Items.LEATHER_HELMET)
                .pattern("x x").pattern(" y ").pattern("   ")
                .unlockedBy("has_item", has(ModItems.suwakoHead.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.silverKnife.get(), 1)
                .define('x', Items.IRON_INGOT).define('y', Items.STICK)
                .pattern(" x ").pattern("y  ").pattern("   ")
                .unlockedBy("has_item", has(ModItems.silverKnife.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.onbashira.get(), 1)
                .define('x', Items.PAPER).define('y', ItemTags.LOGS)
                .pattern("xyx").pattern(" y ").pattern(" y ")
                .unlockedBy("has_item", has(ModItems.onbashira.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.toyosatomimiSword.get(), 1)
                .define('x', Items.GLOWSTONE).define('y', Items.IRON_INGOT).define('z', Items.GOLD_INGOT)
                .pattern("xyx").pattern("xyx").pattern("xzx")
                .unlockedBy("has_item", has(ModItems.toyosatomimiSword.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.deathScythe.get(), 1)
                .define('x', Items.IRON_INGOT).define('y', Items.SOUL_SAND)
                .pattern("xxx").pattern(" y ").pattern("y  ")
                .unlockedBy("has_item", has(ModItems.deathScythe.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.remorseRod.get(), 1)
                .define('x', Items.INK_SAC).define('y', ItemTags.PLANKS).define('z', Items.BONE_MEAL)
                .define('m', Items.STICK)
                .pattern("xyz").pattern("xyz").pattern(" m ")
                .unlockedBy("has_item", has(ModItems.remorseRod.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.roukanSword.get(), 1)
                .define('x', Items.IRON_INGOT).define('y', ModItems.soulTorch.get()).define('z', Items.IRON_SWORD)
                .pattern("  x").pattern("yx ").pattern("z  ")
                .unlockedBy("has_item", has(ModItems.roukanSword.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.mikoStick.get(), 1)
                .define('x', Items.FEATHER).define('y', Items.PAPER).define('z', Items.STICK)
                .pattern(" xy").pattern(" z ").pattern("z  ")
                .unlockedBy("has_item", has(ModItems.mikoStick.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.miniHakkero.get(), 1)
                .define('x', Items.IRON_INGOT).define('y', Items.BLAZE_ROD).define('z', Items.FLINT_AND_STEEL)
                .pattern("xyx").pattern("yzy").pattern("xyx")
                .unlockedBy("has_item", has(ModItems.miniHakkero.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.hotokeHachi.get(), 1)
                .define('x', Items.DIAMOND).define('y', Items.OBSIDIAN)
                .pattern("xyx").pattern("y y").pattern("xyx")
                .unlockedBy("has_item", has(ModItems.hotokeHachi.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.thirdEye.get(), 1)
                .define('x', Items.RED_DYE).define('y', Items.ENDER_EYE)
                .pattern(" x ").pattern("xyx").pattern(" x ")
                .unlockedBy("has_item", has(ModItems.thirdEye.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.kabenuke.get(), 1)
                .define('x', Items.GOLD_INGOT).define('y', Items.STICK).define('z', Items.ENDER_PEARL)
                .pattern("xyy").pattern("z  ").pattern("   ")
                .unlockedBy("has_item", has(ModItems.kabenuke.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.closedThirdEye.get(), 1)
                .define('x', Items.OBSIDIAN).define('y', ModItems.thirdEye.get())
                .pattern("xxx").pattern("xyx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.closedThirdEye.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.bloodOnmyoudama.get(), 1)
                .define('x', ModItems.houraiPearlRed.get()).define('y', ModItems.onmyoudama.get()).define('z', ModItems.houraiPearlWhite.get())
                .pattern("xzx").pattern("xyz").pattern("zxz")
                .unlockedBy("has_item", has(ModItems.bloodOnmyoudama.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.mazinKyoukan.get(), 1)
                .define('x', Items.LAPIS_BLOCK).define('y', Items.REDSTONE_BLOCK)
                .pattern("xyx").pattern("xyx").pattern("xyx")
                .unlockedBy("has_item", has(ModItems.mazinKyoukan.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.sukimaGap.get(), 1)
                .define('x', Items.OBSIDIAN).define('y', Items.RED_DYE).define('z', Items.ENDER_EYE)
                .pattern(" xy").pattern("xzx").pattern("yx ")
                .unlockedBy("has_item", has(ModItems.sukimaGap.get())).save(consumer);
        spawnColorRecipe(consumer, ModItems.sukimaGap.get());
        spawnColorRecipe(consumer, ModItems.gapFoldingUmbrella.get());
        spawnColorRecipe(consumer, ModItems.silverKnife.get());

        //弹幕
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.crystalShot.get(), 4)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern(" x ").pattern("xxx").pattern("   ")
                .unlockedBy("has_item", has(ModItems.crystalShot.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.tinyShot.get(), 1).requires(ModItems.danmakuMaterial.get())
                .unlockedBy("has_item", has(ModItems.tinyShot.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.smallShot.get(), 1).requires(ModItems.danmakuMaterial.get())
                .requires(ModItems.danmakuMaterial.get()).unlockedBy("has_item", has(ModItems.smallShot.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.mediumShot.get(), 2).requires(ModItems.danmakuMaterial.get())
                .requires(ModItems.danmakuMaterial.get()).requires(ModItems.danmakuMaterial.get()).requires(ModItems.danmakuMaterial.get())
                .unlockedBy("has_item", has(ModItems.mediumShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.bigShot.get(), 3)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern("xxx").pattern("xxx").pattern("xxx")
                .unlockedBy("has_item", has(ModItems.bigShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.circleShot.get(), 3)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern(" x ").pattern("xxx").pattern(" x ")
                .unlockedBy("has_item", has(ModItems.circleShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.smallStarShot.get(), 5)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern(" x ").pattern("xxx").pattern("x x")
                .unlockedBy("has_item", has(ModItems.smallStarShot.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.starShot.get(), 1).requires(ModItems.danmakuMaterial.get())
                .requires(ModItems.danmakuMaterial.get()).unlockedBy("has_item", has(ModItems.starShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.scaleShot.get(), 4)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern(" x ").pattern("x x").pattern("x x")
                .unlockedBy("has_item", has(ModItems.scaleShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.butterflyShot.get(), 2)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern("x x").pattern(" x ").pattern("x x")
                .unlockedBy("has_item", has(ModItems.butterflyShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.lightShot.get(), 1)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern("x x").pattern("   ").pattern("x x")
                .unlockedBy("has_item", has(ModItems.lightShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.knifeShot.get(), 2)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern("  x").pattern("xx ").pattern("xx ")
                .unlockedBy("has_item", has(ModItems.knifeShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.kunaiShot.get(), 2)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern(" xx").pattern(" xx").pattern("x  ")
                .unlockedBy("has_item", has(ModItems.kunaiShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.heartShot.get(), 2)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern("x x").pattern("xxx").pattern(" x ")
                .unlockedBy("has_item", has(ModItems.heartShot.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.bigLightShot.get(), 1).requires(ModItems.lightShot.get())
                .requires(ModItems.lightShot.get()).unlockedBy("has_item", has(ModItems.bigLightShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.riceShot.get(), 2)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern(" x ").pattern("x  ").pattern("   ")
                .unlockedBy("has_item", has(ModItems.riceShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.talismanShot.get(), 1)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern("x  ").pattern("x  ").pattern("   ")
                .unlockedBy("has_item", has(ModItems.talismanShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ovalShot.get(), 1)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern(" xx").pattern("xxx").pattern("xx ")
                .unlockedBy("has_item", has(ModItems.ovalShot.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.arrowShot.get(), 1)
                .define('x', ModItems.danmakuMaterial.get())
                .pattern("x  ").pattern("x  ").pattern("x  ")
                .unlockedBy("has_item", has(ModItems.arrowShot.get())).save(consumer);
    }


    /**
     * 生成物品染色配方
     */
    private void spawnColorRecipe(Consumer<FinishedRecipe> consumer, Item item) {
        for (DanmakuColor danmakuColor : DanmakuColor.values()) {
            DyeItem dye = DanmakuColor.getDyeItemFromColor(danmakuColor);
            String recipeId = item.getDescriptionId() + "_" + danmakuColor.getName();
            ItemStack stack = ModColorItemUtils.createColoredStack(item, danmakuColor);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, stack.getItem()).requires(item).requires(dye)
                    .unlockedBy("has_umbrella", has(item))
                    .unlockedBy("has_dye", has(dye))
                    .save(getConsumer(consumer, item, danmakuColor), KaguyaUtils.fa(recipeId));
        }
        // 使用水桶清除颜色
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item).requires(item).requires(Items.WATER_BUCKET)
                .unlockedBy("has_umbrella", has(item))
                .save(consumer, KaguyaUtils.fa("clear_" + item.getDescriptionId()));
    }

    /**
     * 获取自定义Consumer<FinishedRecipe>
     */
    private Consumer<FinishedRecipe> getConsumer(Consumer<FinishedRecipe> consumer, Item item, DanmakuColor dyeColor){
        return recipe -> consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                json.addProperty("category", RecipeCategory.MISC.getFolderName());
                recipe.serializeRecipeData(json);

                // 修改结果部分
                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
//                        resultJson.addProperty("count", 1);

                JsonObject nbtJson = new JsonObject();
                nbtJson.addProperty(ModColorItemUtils.NBT_COLOR, dyeColor.getName());
                resultJson.add("nbt", nbtJson);

                json.add("result", resultJson);
            }

            @Override
            public ResourceLocation getId() {
                return recipe.getId();
            }

            @Override
            public RecipeSerializer<?> getType() {
                return recipe.getType();
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return recipe.serializeAdvancement();
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return recipe.getAdvancementId();
            }
        });
    }
}
