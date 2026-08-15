package com.yuo.kaguya.Entity.Mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

//灵梦
public class HakureiReimu extends KFDBaseMob {
    public HakureiReimu(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            LivingEntity target = this.getTarget();
            if (target instanceof Player player) {
                // 如果条件满足（shouldAttackPlayer 返回 false），则清除目标
                if (!shouldAttackPlayer(player)) {
                    this.setTarget(null);
                }
            }
        }
    }

    @Override
    protected boolean shouldAttackPlayer(LivingEntity living) {
        if (living == null || !living.isAlive()) return true;

        // 检查是否穿戴全套金装备
        boolean hasGoldenArmor = isGoldenArmor(living.getItemBySlot(EquipmentSlot.HEAD)) ||
                isGoldenArmor(living.getItemBySlot(EquipmentSlot.CHEST)) ||
                isGoldenArmor(living.getItemBySlot(EquipmentSlot.LEGS)) ||
                isGoldenArmor(living.getItemBySlot(EquipmentSlot.FEET));


        if (!hasGoldenArmor) return true; // 未穿全套金甲，攻击

        // 检查主手或副手是否持有金锭
        ItemStack mainHand = living.getMainHandItem();
        ItemStack offHand = living.getOffhandItem();
        boolean hasGoldIngot = mainHand.getItem() == Items.GOLD_INGOT || offHand.getItem() == Items.GOLD_INGOT;

        return !hasGoldIngot;
    }

    private boolean isGoldenArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item == Items.GOLDEN_HELMET || item == Items.GOLDEN_CHESTPLATE || item == Items.GOLDEN_LEGGINGS || item == Items.GOLDEN_BOOTS;
    }
}
