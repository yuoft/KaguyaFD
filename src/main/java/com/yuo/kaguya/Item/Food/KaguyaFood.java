package com.yuo.kaguya.Item.Food;

import com.mojang.datafixers.util.Pair;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KaguyaFood extends Item {
    private final boolean isMcFood;

    public KaguyaFood(FoodProperties foodProperties) {
        super(new Properties().stacksTo(64).food(foodProperties));
        this.isMcFood = true;
    }

    public KaguyaFood(int maxDamage, FoodProperties foodProperties, boolean isMcFood) {
        super(new Properties().durability(maxDamage).food(foodProperties));
        this.isMcFood = isMcFood;
    }

    public KaguyaFood(FoodProperties foodProperties, int maxStackSize, boolean isMcFood) {
        super(new Properties().stacksTo(maxStackSize).food(foodProperties));
        this.isMcFood = isMcFood;
    }

    public static FoodProperties heavenlyPeach = new FoodProperties.Builder().nutrition(10).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 15, 2), 1.0f).build();
    public static FoodProperties ibukiGourd = new FoodProperties.Builder().nutrition(1).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 10, 0), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 20 * 10, 0), 1.0f).build();
    public static FoodProperties swallowCowrieShell = new FoodProperties.Builder().nutrition(10).saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 20 * 30, 1), 1.0f).build();
    public static FoodProperties iceStick = new FoodProperties.Builder().nutrition(1).saturationMod(0.6f).fast().build();

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        Item item = stack.getItem();
        if (item == ModItems.koyasugai.get()){
            components.add(Component.translatable("text.kaguya.koyasugai.1"));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.isEdible()) {
            if (player.canEat(itemstack.getFoodProperties(player).canAlwaysEat())) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        return this.isEdible() ? isMcFood() ? living.eat(level, stack) : this.eat(level, living, stack) : stack;
    }

    protected ItemStack eat(Level level, LivingEntity living, ItemStack stack){
        return stack;
    }

    /**
     * 添加对应buff
     */
    protected void addEatEffect(ItemStack stack, Level level, LivingEntity living) {
        Item item = stack.getItem();
        if (item.isEdible()) {

            for (Pair<MobEffectInstance, Float> mobEffectInstanceFloatPair : stack.getFoodProperties(living).getEffects()) {
                if (!level.isClientSide && ((Pair<?, ?>) mobEffectInstanceFloatPair).getFirst() != null && level.random.nextFloat() < (Float) ((Pair<?, ?>) mobEffectInstanceFloatPair).getSecond()) {
                    living.addEffect(new MobEffectInstance((MobEffectInstance) ((Pair<?, ?>) mobEffectInstanceFloatPair).getFirst()));
                }
            }
        }
    }

    public boolean isMcFood() {
        return isMcFood;
    }
}
