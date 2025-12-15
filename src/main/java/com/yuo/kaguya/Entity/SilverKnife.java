package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SilverKnife extends AbstractArrow{
    public static final EntityType<SilverKnife> TYPE_RED = EntityType.Builder.<SilverKnife>of(SilverKnife::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("silver_knife_red");
    public static final EntityType<SilverKnife> TYPE_GREEN = EntityType.Builder.<SilverKnife>of(SilverKnife::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("silver_knife_green");
    public static final EntityType<SilverKnife> TYPE_BLUE = EntityType.Builder.<SilverKnife>of(SilverKnife::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("silver_knife_blue");
    public static final EntityType<SilverKnife> TYPE_WHITE = EntityType.Builder.<SilverKnife>of(SilverKnife::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).build("silver_knife_white");
    private final ItemStack item;

    public SilverKnife(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.item = ItemStack.EMPTY;
    }

    public SilverKnife(EntityType<? extends AbstractArrow> entityType, LivingEntity living, Level level, ItemStack item) {
        super(entityType, living, level);
        this.setOwner(living);
        this.setBaseDamage(2.5d);
        this.item = item.copy();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float damage = (float) getBaseDamage();

        Entity owner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, owner == null ? this : owner);
        SoundEvent soundEvent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damageSource, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity living) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(living, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, living);
                }

                this.doPostHurtEffects(living);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float $$8 = 1.0F;
        this.playSound(soundEvent, $$8, 1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    //生存不消失
    @Override
    public void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED) {
            super.tickDespawn();
        }
    }

    //玩家拾取
    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }

    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected ItemStack getPickupItem() {
        EntityType<?> type = this.getType();
        if (type == TYPE_RED) {
            return new ItemStack(ModItems.silverKnifeRed.get());
        }else  if (type == TYPE_GREEN) {
            return new ItemStack(ModItems.silverKnifeGreen.get());
        }else  if (type == TYPE_BLUE) {
            return new ItemStack(ModItems.silverKnifeBlue.get());
        }else return new ItemStack(ModItems.silverKnifeWhite.get());
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    public boolean isFoil() {
        return false;
    }
}
