package com.yuo.kaguya.Item.Weapon;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.yuo.kaguya.Entity.IceStatueEntity;
import com.yuo.kaguya.Item.KaguyaWeapon;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

/**
 * 冰剑
 */
public class IceSword extends KaguyaWeapon {
    public IceSword() {
        super(Tiers.DIAMOND, new Properties().durability(30));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity owner) {
        if (!(owner instanceof Player)) return super.hurtEnemy(stack, target, owner);
        if (target instanceof IceStatueEntity) return super.hurtEnemy(stack, target, owner);

        // 冰冻 10 秒 = 200 刻，若需要永久则传 -1
        int freezeDuration = 200;
        if (target.getHealth() <= 0 || !target.isAlive()) return super.hurtEnemy(stack, target, owner);

        String modelId = "";
        if (target instanceof IMaid maid){
            modelId = maid.getModelId();
        }
        IceStatueEntity iceStatue = IceStatueEntity.buildFrozenStatue(target, freezeDuration, modelId);

        if (target instanceof Player) {
            // 注意：这里依然会杀死玩家，若不想杀死请修改此处逻辑（例如仅定身并隐藏原玩家）
            target.hurt(DanmakuDamageTypes.danmaku(owner), Float.MAX_VALUE);
        } else {
            target.remove(RemovalReason.KILLED);
        }

        if (!owner.level().isClientSide) {
            owner.playSound(SoundEvents.GLASS_BREAK);
            owner.level().addFreshEntity(iceStatue);
            return true;
        }

        return super.hurtEnemy(stack, target, owner);
    }
}
