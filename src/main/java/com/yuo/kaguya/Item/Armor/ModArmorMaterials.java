package com.yuo.kaguya.Item.Armor;

import com.yuo.endless.Endless;
import com.yuo.endless.Items.EndlessItems;
import com.yuo.kaguya.Kaguya;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

//盔甲材料类
public enum ModArmorMaterials implements ArmorMaterial {
	//---------材质---耐久值----------护甲值-------附魔能力--------音效----------------------盔甲韧性- 击退抗性-修复材料
	FIRE_RAT(Kaguya.MOD_ID + ":" + "fire_rat", 999, new int[] { 3, 6 , 8, 3 }, 12, SoundEvents.ARMOR_EQUIP_DIAMOND, 2, 0,
			() -> Ingredient.of(Items.BLAZE_ROD)),
	KAPPA(Kaguya.MOD_ID + ":" + "kappa", 999, new int[] { 1, 2 , 3, 1 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5f, 0,
			() -> Ingredient.of(Items.LILY_PAD)),
	MARISA(Kaguya.MOD_ID + ":" + "marisa", 199, new int[] { 4, 7 , 9, 4 }, 10, SoundEvents.ARMOR_EQUIP_NETHERITE, 3, 0.2f,
			() -> Ingredient.of(Items.OBSIDIAN)),
	SUWAKO(Kaguya.MOD_ID + ":" + "suwako", 99, new int[] { 2, 5 , 6, 2 }, 11, SoundEvents.ARMOR_EQUIP_IRON, 1, 0,
			() -> Ingredient.of(Items.ENDER_EYE));

	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantmentValue;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyLoadedValue<Ingredient> repairIngredient;

	ModArmorMaterials(String pName, int pDurabilityMultiplier, int[] pSlotProtections, int pEnchantmentValue, SoundEvent pSound, float pToughness, float pKnockbackResistance, Supplier pRepairIngredient) {
		this.name = pName;
		this.durabilityMultiplier = pDurabilityMultiplier;
		this.slotProtections = pSlotProtections;
		this.enchantmentValue = pEnchantmentValue;
		this.sound = pSound;
		this.toughness = pToughness;
		this.knockbackResistance = pKnockbackResistance;
		this.repairIngredient = new LazyLoadedValue(pRepairIngredient);
	}

	@Override
	public int getDurabilityForType(Type pSlot) {
		return HEALTH_PER_SLOT[pSlot.getSlot().getIndex()] * this.durabilityMultiplier;
	}

	@Override
	public int getDefenseForType(Type pSlot) {
		return this.slotProtections[pSlot.getSlot().getIndex()];
	}

	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	public SoundEvent getEquipSound() {
		return this.sound;
	}

	public Ingredient getRepairIngredient() {
		return (Ingredient)this.repairIngredient.get();
	}

	public String getName() {
		return this.name;
	}

	public float getToughness() {
		return this.toughness;
	}

	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}

}
