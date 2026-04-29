package com.yuo.kaguya.Item.Food;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * 燕的子安贝
 */
public class SwallowCowrieShell extends KaguyaFood {
    public SwallowCowrieShell() {
        super(swallowCowrieShell,1, false);
    }

    /**
     * 食用消耗一点耐久
     */
    @Override
    protected ItemStack eat(Level level, LivingEntity living, ItemStack stack) {
        if (stack.isEdible()) {
            level.playSound(null, living.getX(), living.getY(), living.getZ(), living.getEatingSound(stack), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
            this.addEatEffect(stack, level, living);
            if (living instanceof Player player) {
                int experienceLevel = player.experienceLevel;
                if (experienceLevel < 1) {
                    return stack;
                }

                if (!player.isCrouching()){
                    player.giveExperienceLevels(-1);
                    player.getFoodData().eat(2, 0.6f);
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 3, 1));
                }else { //补充至最大饥饿值
                    int needFood = 20 - player.getFoodData().getFoodLevel();
                    int expForFood = (int) Math.ceil(needFood / 2.0); //1exp = 2food
                    if(experienceLevel < expForFood ) {
                        return stack;
                    }
                    player.giveExperienceLevels(-expForFood);
                    player.getFoodData().eat(needFood, 0.6f);
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 3 * expForFood, 3));
                }
            }

            living.gameEvent(GameEvent.EAT);
        }

        return stack;
    }
}
