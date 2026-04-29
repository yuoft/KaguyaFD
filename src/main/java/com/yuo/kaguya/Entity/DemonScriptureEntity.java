package com.yuo.kaguya.Entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class DemonScriptureEntity extends Entity {
    public Player user;
    public int count;          // 已经绘制的“节”数 / 使用时间
    public float turnAngle;
    public int befUseCount;

    private static final EntityDataAccessor<Integer> DATA_COUNT = SynchedEntityData.defineId(DemonScriptureEntity.class, EntityDataSerializers.INT);

    public DemonScriptureEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.setSize(2.0F, 2.0F);
    }

    public DemonScriptureEntity(Level level, Player player) {
        this(ModEntityTypes.MAZINKYOUKAN.get(), level);
        this.user = player;
        this.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        this.setYRot(player.getYRot());
        this.setXRot(0);
        this.count = 0;
        this.befUseCount = 0;
        this.turnAngle = 0F;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_COUNT, 0);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && (user == null || user.isRemoved())) {
            this.discard();
            return;
        }
        super.tick();

        if (user != null) {
            // 如果玩家不再使用物品，则触发效果并消失
            if (!user.isUsingItem()) {
                int time = count;
                time /= 3;
                if (!level().isClientSide) {
                    int removeLevel = (int)((float)time / 8.0F) + 1;
                    if (user.experienceLevel - removeLevel < 0) {
                        time += (user.experienceLevel - removeLevel) * 8;
                    }
                    user.giveExperienceLevels(-removeLevel);
                    user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, time * 10, 4));
                    user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time * 10, 0));
                    user.addEffect(new MobEffectInstance(MobEffects.JUMP, time * 10, 1));
                    user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, time * 10, 0));
                }
                this.discard();
                return;
            } else {
                // 跟随玩家
                Vec3 look = user.getLookAngle();
                double distance = 0.5;
                this.setPos(user.getX() + look.x * distance,
                        user.getY() + user.getEyeHeight() + look.y * distance,
                        user.getZ() + look.z * distance);
                this.setYRot(user.getYRot());
                this.setXRot(0);
            }
        }

        count++;
        if (count > 114) count = 114;
        else if (count > 50) {
            turnAngle += 2.88F;
        }
        this.entityData.set(DATA_COUNT, count);
    }

    public int getCount() {
        return this.entityData.get(DATA_COUNT);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(this.getX() - width/2)
                .setMinY(this.getY())
                .setMinZ(this.getZ() - width/2)
                .setMaxX(this.getX() + width/2)
                .setMaxY(this.getY() + height)
                .setMaxZ(this.getZ() + width/2));
    }
}