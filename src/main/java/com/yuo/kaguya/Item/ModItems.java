package com.yuo.kaguya.Item;

import com.yuo.kaguya.Kaguya;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//物品注册管理器
public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Kaguya.MOD_ID);

	public static RegistryObject<Item> aja_red_stone = ITEMS.register("aja_red_stone", KaguyaItem::new);
	public static RegistryObject<Item> byoukiheiyuMamori = ITEMS.register("byoukiheiyu_mamori", KaguyaItem::new);
	public static RegistryObject<Item> diffusionAmulet = ITEMS.register("diffusion_amulet", KaguyaItem::new);
	public static RegistryObject<Item> hisyaku0 = ITEMS.register("hisyaku_0", KaguyaItem::new);
	public static RegistryObject<Item> homingAmulet = ITEMS.register("homing_amulet", KaguyaItem::new);
	public static RegistryObject<Item> houtou = ITEMS.register("houtou", KaguyaItem::new);
	public static RegistryObject<Item> kinkakuzi = ITEMS.register("kinkakuzi", KaguyaItem::new);
	public static RegistryObject<Item> mazinKyoukan = ITEMS.register("mazin_kyoukan", KaguyaItem::new);
	public static RegistryObject<Item> mikoStickS = ITEMS.register("miko_stick_s", KaguyaItem::new);
	public static RegistryObject<Item> ryuuTama = ITEMS.register("ryuu_tama", KaguyaItem::new);
	public static RegistryObject<Item> sakuyaStopWatch = ITEMS.register("sakuya_stop_watch", KaguyaItem::new);
	public static RegistryObject<Item> sakuyaWatch = ITEMS.register("sakuya_watch", KaguyaItem::new);
	public static RegistryObject<Item> soulTorch = ITEMS.register("soul_torch", KaguyaItem::new);
	public static RegistryObject<Item> spiritualStrikeTalisman = ITEMS.register("spiritual_strike_talisman", KaguyaItem::new);

	public static RegistryObject<Item> hinezumi = ITEMS.register("hinezumi", () -> new KaguyaArmor(ArmorMaterials.DIAMOND, Type.HELMET));
	public static RegistryObject<Item> kappaCap = ITEMS.register("kappa_cap", () -> new KaguyaArmor(ArmorMaterials.DIAMOND, Type.HELMET));
	public static RegistryObject<Item> kerobou = ITEMS.register("kerobou", () -> new KaguyaArmor(ArmorMaterials.DIAMOND, Type.HELMET));
	public static RegistryObject<Item> marisaHat = ITEMS.register("marisa_hat", () -> new KaguyaArmor(ArmorMaterials.DIAMOND, Type.HELMET));

	public static RegistryObject<Item> deathScythe = ITEMS.register("death_scythe", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> gungnir = ITEMS.register("gungnir", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> hakureiOharaibou = ITEMS.register("hakurei_oharaibou", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> hakurouken = ITEMS.register("hakurouken", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> hisouSword = ITEMS.register("hisou_sword", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> houraiEda = ITEMS.register("hourai_eda", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> icicleSword = ITEMS.register("icicle_sword", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> kaigoStick0 = ITEMS.register("kaigo_stick_0", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> laevateinn = ITEMS.register("laevateinn", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> onbashira = ITEMS.register("onbashira", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> roukanSword = ITEMS.register("roukan_sword", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> toyosatomimiSword = ITEMS.register("toyosatomimi_sword", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> yuukaParasol = ITEMS.register("yuuka_parasol", () -> new KaguyaSword(Tiers.DIAMOND));
	public static RegistryObject<Item> yuyukoOugi = ITEMS.register("yuyuko_ougi", () -> new KaguyaSword(Tiers.DIAMOND));

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

	public static RegistryObject<Item> heavenlyPeach = ITEMS.register("heavenly_peach", () -> new KaguyaFood(KaguyaFood.heavenlyPeach));
	public static RegistryObject<Item> ibukihyou = ITEMS.register("ibukihyou", () -> new KaguyaFood(KaguyaFood.ibukihyou));
	public static RegistryObject<Item> koyasugai = ITEMS.register("koyasugai", () -> new KaguyaFood(KaguyaFood.koyasugai));

	public static RegistryObject<Item> arrowShot = ITEMS.register("arrow_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> bigBightShot = ITEMS.register("big_bight_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> bigShot = ITEMS.register("big_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> butterflyShot = ITEMS.register("butterfly_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> circleShot = ITEMS.register("circle_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> crystalShot = ITEMS.register("crystal_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> heartShot = ITEMS.register("heart_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> knifeShot = ITEMS.register("knife_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> kunaiShot = ITEMS.register("kunai_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> lightShot = ITEMS.register("light_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> mediumShot = ITEMS.register("medium_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> ovalShot = ITEMS.register("oval_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> riceShot = ITEMS.register("rice_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> scaleShot = ITEMS.register("scale_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> smallShot = ITEMS.register("small_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> smallStarShot = ITEMS.register("small_star_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> starShot = ITEMS.register("star_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> talismanShot = ITEMS.register("talisman_shot", DanmakuShotItem::new);
	public static RegistryObject<Item> tinyShot = ITEMS.register("tiny_shot", DanmakuShotItem::new);

//	public static RegistryObject<BlockItem> customSapling = ITEMS.register("custom_sapling",
//			() -> new CustomSapling(OreCropBlocks.customSapling.get(), GROUP));

}
