package com.yuo.kaguya.Data;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Blocks;
import org.jetbrains.annotations.NotNull;

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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.silverKnifeWhite.get(), 1)
                .define('x', Items.IRON_INGOT).define('y', Items.STICK)
                .pattern(" x ").pattern("y  ").pattern("   ")
                .unlockedBy("has_item", has(ModItems.silverKnifeWhite.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.silverKnifeRed.get(), 1).requires(ModItems.silverKnifeWhite.get())
                .requires(Items.RED_DYE).unlockedBy("has_item", has(ModItems.silverKnifeRed.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.silverKnifeGreen.get(), 1).requires(ModItems.silverKnifeWhite.get())
                .requires(Items.GREEN_DYE).unlockedBy("has_item", has(ModItems.silverKnifeGreen.get())).save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.silverKnifeBlue.get(), 1).requires(ModItems.silverKnifeWhite.get())
                .requires(Items.BLUE_DYE).unlockedBy("has_item", has(ModItems.silverKnifeBlue.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.onbashira.get(), 1)
                .define('x', Items.PAPER).define('y', ItemTags.LOGS)
                .pattern("xyx").pattern(" y ").pattern(" y ")
                .unlockedBy("has_item", has(ModItems.onbashira.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.toyosatomimiSword.get(), 1)
                .define('x', Items.GLOWSTONE).define('y', Items.IRON_INGOT).define('z', Items.GOLD_INGOT)
                .pattern("xyx").pattern("xyx").pattern("xzx")
                .unlockedBy("has_item", has(ModItems.toyosatomimiSword.get())).save(consumer);

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
}
