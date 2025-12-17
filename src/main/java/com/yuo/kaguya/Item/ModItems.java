package com.yuo.kaguya.Item;

import com.yuo.kaguya.Block.ModBlocks;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Item.Armor.FireRatRobe;
import com.yuo.kaguya.Item.Armor.KappaHelmet;
import com.yuo.kaguya.Item.Armor.MarisaHelmet;
import com.yuo.kaguya.Item.Armor.SuwakoHelmet;
import com.yuo.kaguya.Item.Prpo.RecoveryCharm;
import com.yuo.kaguya.Item.Weapon.*;
import com.yuo.kaguya.Kaguya;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//物品注册管理器
public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Kaguya.MOD_ID);

	public static RegistryObject<Item> bigPotion = ITEMS.register("big_potion", KaguyaMaterialItem::new);
	public static RegistryObject<Item> bomb = ITEMS.register("bomb", KaguyaMaterialItem::new);
	public static RegistryObject<Item> danmakuMaterial = ITEMS.register("danmaku_material", KaguyaMaterialItem::new);
	public static RegistryObject<Item> extend = ITEMS.register("extend", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlAqua = ITEMS.register("hourai_pearl_aqua", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlBlue = ITEMS.register("hourai_pearl_blue", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlGreen = ITEMS.register("hourai_pearl_green", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlOrange = ITEMS.register("hourai_pearl_orange", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlPurple = ITEMS.register("hourai_pearl_purple", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlRed = ITEMS.register("hourai_pearl_red", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlWhite = ITEMS.register("hourai_pearl_white", KaguyaMaterialItem::new);
	public static RegistryObject<Item> houraiPearlYellow = ITEMS.register("hourai_pearl_yellow", KaguyaMaterialItem::new);
	public static RegistryObject<Item> onmyoudama = ITEMS.register("onmyoudama", KaguyaMaterialItem::new);
	public static RegistryObject<Item> smallPotion = ITEMS.register("small_potion", KaguyaMaterialItem::new);
	public static RegistryObject<Item> thPotion = ITEMS.register("th_potion", KaguyaMaterialItem::new);

	//道具
	public static RegistryObject<Item> aja_red_stone = ITEMS.register("aja_red_stone", KaguyaPrpo::new);
	public static RegistryObject<Item> byoukiheiyuMamori = ITEMS.register("byoukiheiyu_mamori", RecoveryCharm::new);
	public static RegistryObject<Item> diffusionAmulet = ITEMS.register("diffusion_amulet", KaguyaPrpo::new);
	public static RegistryObject<Item> hisyaku0 = ITEMS.register("hisyaku_0", KaguyaPrpo::new);
	public static RegistryObject<Item> homingAmulet = ITEMS.register("homing_amulet", KaguyaPrpo::new);
	public static RegistryObject<Item> houtou = ITEMS.register("houtou", KaguyaPrpo::new);
	public static RegistryObject<Item> kinkakuzi = ITEMS.register("kinkakuzi", KaguyaPrpo::new);
	public static RegistryObject<Item> mazinKyoukan = ITEMS.register("mazin_kyoukan", KaguyaPrpo::new);
	public static RegistryObject<Item> mikoStickS = ITEMS.register("miko_stick_s", KaguyaPrpo::new);
	public static RegistryObject<Item> ryuuTama = ITEMS.register("ryuu_tama", KaguyaPrpo::new);
	public static RegistryObject<Item> sakuyaStopWatch = ITEMS.register("sakuya_stop_watch", KaguyaPrpo::new);
	public static RegistryObject<Item> sakuyaWatch = ITEMS.register("sakuya_watch", KaguyaPrpo::new);
	public static RegistryObject<Item> soulTorch = ITEMS.register("soul_torch", KaguyaPrpo::new);
	public static RegistryObject<Item> spiritualStrikeTalisman = ITEMS.register("spiritual_strike_talisman", KaguyaPrpo::new);
	public static RegistryObject<Item> closedThirdEye = ITEMS.register("closed_third_eye", KaguyaPrpo::new);
	public static RegistryObject<Item> cursedDecoyDoll = ITEMS.register("cursed_decoy_doll", KaguyaPrpo::new);
	public static RegistryObject<Item> gapFoldingUmbrellaBase = ITEMS.register("gap_folding_umbrella_base", KaguyaPrpo::new);
	public static RegistryObject<Item> hotokeHachi = ITEMS.register("hotoke_hachi", KaguyaPrpo::new);
	public static RegistryObject<Item> kabenuke = ITEMS.register("kabenuke", KaguyaPrpo::new);
	public static RegistryObject<Item> sukima = ITEMS.register("sukima", KaguyaPrpo::new);
	public static RegistryObject<Item> thirdEye0 = ITEMS.register("third_eye_0", KaguyaPrpo::new);
	public static RegistryObject<Item> pendulum = ITEMS.register("pendulum", KaguyaPrpo::new);
	public static RegistryObject<Item> magicBroom = ITEMS.register("magic_broom", KaguyaPrpo::new);
	public static RegistryObject<Item> uchidenoKoduchi = ITEMS.register("uchideno_koduchi", KaguyaPrpo::new);
	public static RegistryObject<Item> miniHakkero = ITEMS.register("mini_hakkero", KaguyaPrpo::new);
	public static RegistryObject<Item> nuclearControlRod = ITEMS.register("nuclear_control_rod", KaguyaPrpo::new);
	public static RegistryObject<Item> kappaWaterPistol = ITEMS.register("kappa_water_pistol", KaguyaPrpo::new);
	public static RegistryObject<Item> tenguFan = ITEMS.register("tengu_fan", KaguyaPrpo::new);

	//盔甲
	public static RegistryObject<Item> fireRatBobe = ITEMS.register("fire_rat_bobe", FireRatRobe::new);
	public static RegistryObject<Item> kappaHead = ITEMS.register("kappa_head", KappaHelmet::new);
	public static RegistryObject<Item> suwakoHead = ITEMS.register("suwako_head", SuwakoHelmet::new);
	public static RegistryObject<Item> marisaHead = ITEMS.register("marisa_head", MarisaHelmet::new);

	//武器
	public static RegistryObject<Item> silverKnifeWhite = ITEMS.register("silver_knife_white", SilverKnifeItem::new);
	public static RegistryObject<Item> silverKnifeRed = ITEMS.register("silver_knife_red", SilverKnifeItem::new);
	public static RegistryObject<Item> silverKnifeBlue = ITEMS.register("silver_knife_blue", SilverKnifeItem::new);
	public static RegistryObject<Item> silverKnifeGreen = ITEMS.register("silver_knife_green", SilverKnifeItem::new);
	public static RegistryObject<Item> deathScythe = ITEMS.register("death_scythe", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> gungnir = ITEMS.register("gungnir", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> hakureiOharaibou = ITEMS.register("hakurei_oharaibou", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> hakurouken = ITEMS.register("hakurouken", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> hisouSword = ITEMS.register("hisou_sword", HisouSword::new);
	public static RegistryObject<Item> houraiEda = ITEMS.register("hourai_eda", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> icicleSword = ITEMS.register("icicle_sword", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> kaigoStick0 = ITEMS.register("kaigo_stick_0", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> laevateinn = ITEMS.register("laevateinn", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> onbashira = ITEMS.register("onbashira", Onbashira::new);
	public static RegistryObject<Item> roukanSword = ITEMS.register("roukan_sword", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> toyosatomimiSword = ITEMS.register("toyosatomimi_sword", SacredSword::new);
	public static RegistryObject<Item> yuukaParasol = ITEMS.register("yuuka_parasol", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> yuyukoOugi = ITEMS.register("yuyuko_ougi", YuyukoFan::new);

	//食物
	public static RegistryObject<Item> heavenlyPeach = ITEMS.register("heavenly_peach", () -> new KaguyaFood(KaguyaFood.heavenlyPeach));
	public static RegistryObject<Item> ibukihyou = ITEMS.register("ibukihyou", () -> new KaguyaFood(KaguyaFood.ibukihyou));
	public static RegistryObject<Item> koyasugai = ITEMS.register("koyasugai", () -> new KaguyaFood(KaguyaFood.koyasugai));

	//弹幕发射物品
	public static RegistryObject<Item> arrowShot = ITEMS.register("arrow_shot", () -> new DanmakuShotItem(DanmakuType.ARROW_SHOT));
	public static RegistryObject<Item> bigLightShot = ITEMS.register("big_light_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> bigShot = ITEMS.register("big_shot", () -> new DanmakuShotItem(DanmakuType.BIG_BALL));
	public static RegistryObject<Item> butterflyShot = ITEMS.register("butterfly_shot", () -> new DanmakuShotItem(DanmakuType.BUTTER_FLY));
	public static RegistryObject<Item> circleShot = ITEMS.register("circle_shot", () -> new DanmakuShotItem(DanmakuType.RING_BALL));
	public static RegistryObject<Item> crystalShot = ITEMS.register("crystal_shot",() -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> heartShot = ITEMS.register("heart_shot",() -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> knifeShot = ITEMS.register("knife_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> kunaiShot = ITEMS.register("kunai_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> lightShot = ITEMS.register("light_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> mediumShot = ITEMS.register("medium_shot", () -> new DanmakuShotItem(DanmakuType.MEDIUM_BALL));
	public static RegistryObject<Item> ovalShot = ITEMS.register("oval_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> riceShot = ITEMS.register("rice_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> scaleShot = ITEMS.register("scale_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> smallShot = ITEMS.register("small_shot", () -> new DanmakuShotItem(DanmakuType.SMALL_BALL));
	public static RegistryObject<Item> smallStarShot = ITEMS.register("small_star_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> starShot = ITEMS.register("star_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> talismanShot = ITEMS.register("talisman_shot", () -> new DanmakuShotItem(DanmakuType.TINY_BALL));
	public static RegistryObject<Item> tinyShot = ITEMS.register("tiny_shot",() -> new DanmakuShotItem(DanmakuType.TINY_BALL));

	public static RegistryObject<BlockItem> danmakuCraft = ITEMS.register("danmaku_craft",
			() -> new BlockItem(ModBlocks.danmakuCraft.get(), new Properties()));

}
