package com.yuo.kaguya.Entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class IceStatueEntity extends LivingEntity {
    public static final EntityType<IceStatueEntity> TYPE = EntityType.Builder.<IceStatueEntity>of(IceStatueEntity::new, MobCategory.CREATURE)
            .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).build("frozen_statue");

    private static final EntityDataAccessor<String> TRAPPED_ENTITY_TYPE = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<CompoundTag> TRAPPED_ENTITY_DATA = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_WIDTH = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_HEIGHT = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_SCALE = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CRACK_AMOUNT = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.INT); // 冰裂纹程度 0-10

    private EntityDimensions frozenSize = EntityDimensions.fixed(0.5F, 0.5F);

    public IceStatueEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)        // 冰雕生命值
                .add(Attributes.MOVEMENT_SPEED, 0.0D)   // 无法移动
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D); // 完全抗击退
    }

    /**
     * 从原实体创建冰冻雕像
     */
    public static IceStatueEntity buildFrozenStatue(LivingEntity parent) {
        IceStatueEntity statue = ModEntityTypes.FROZEN_STATUE.get().create(parent.level());
        CompoundTag entityTag = new CompoundTag();

        try {
            // 保存实体数据（不包括玩家）
            if (!(parent instanceof Player)) {
                parent.saveWithoutId(entityTag);
            }
        } catch (Exception e) {
            System.out.println("create error");
        }

        statue.setTrappedTag(entityTag);
        statue.setTrappedEntityTypeString(ForgeRegistries.ENTITY_TYPES.getKey(parent.getType()).toString());
        statue.setTrappedEntityWidth(parent.getBbWidth());
        statue.setTrappedHeight(parent.getBbHeight());
        statue.setTrappedScale(parent.getScale());
        statue.setCrackAmount(0);

        // 设置位置和旋转
        statue.setPos(parent.getX(), parent.getY(), parent.getZ());
        statue.setYRot(parent.getYRot());
        statue.setXRot(parent.getXRot());
        statue.yBodyRot = parent.yBodyRot;
        statue.yHeadRot = parent.yHeadRot;

        return statue;
    }

    @Override
    public void push(@NotNull Entity entityIn) {
        // 禁止被推动
    }

    @Override
    public void baseTick() {
        //移除生物基础逻辑
    }

    @Override
    public void aiStep() {
        //移除Ai
    }

    @Override
    public void tick() {
        super.tick();

        // 锁定旋转
        this.setYRot(this.yBodyRot);
        this.yHeadRot = this.getYRot();
        updateDimensions();

        if (!level().isClientSide){
            //防止假死
            if (this.getCrackAmount() > 10 || this.getHealth() <= 0){
                playSound(SoundEvents.GLASS_BREAK);
                this.discard();
            }
        }
    }

    /**
     * 更新实体尺寸 差距大于0.01
     */
    private void updateDimensions() {
        if (Math.abs(this.getBbWidth() - getTrappedWidth()) > 0.01 || Math.abs(this.getBbHeight() - getTrappedHeight()) > 0.01) {
            double prevX = this.getX();
            double prevZ = this.getZ();
            this.frozenSize = EntityDimensions.scalable(getTrappedWidth(), getTrappedHeight());
            this.refreshDimensions();
            this.setPos(prevX, this.getY(), prevZ);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRAPPED_ENTITY_TYPE, "minecraft:pig");
        this.entityData.define(TRAPPED_ENTITY_DATA, new CompoundTag());
        this.entityData.define(TRAPPED_ENTITY_WIDTH, 0.5F);
        this.entityData.define(TRAPPED_ENTITY_HEIGHT, 0.5F);
        this.entityData.define(TRAPPED_ENTITY_SCALE, 1F);
        this.entityData.define(CRACK_AMOUNT, 0);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean hurt(DamageSource source, float v) {
        return false;
    }

    public EntityType<?> getTrappedEntityType() {
        String str = getTrappedEntityTypeString();
        return EntityType.byString(str).orElse(EntityType.PIG);
    }

    public String getTrappedEntityTypeString() {
        return this.entityData.get(TRAPPED_ENTITY_TYPE);
    }

    public void setTrappedEntityTypeString(String string) {
        this.entityData.set(TRAPPED_ENTITY_TYPE, string);
    }

    public CompoundTag getTrappedTag() {
        return this.entityData.get(TRAPPED_ENTITY_DATA);
    }

    public void setTrappedTag(CompoundTag tag) {
        this.entityData.set(TRAPPED_ENTITY_DATA, tag);
    }

    public float getTrappedWidth() {
        return this.entityData.get(TRAPPED_ENTITY_WIDTH);
    }

    public void setTrappedEntityWidth(float width) {
        this.entityData.set(TRAPPED_ENTITY_WIDTH, width);
    }

    public float getTrappedHeight() {
        return this.entityData.get(TRAPPED_ENTITY_HEIGHT);
    }

    public void setTrappedHeight(float height) {
        this.entityData.set(TRAPPED_ENTITY_HEIGHT, height);
    }

    public float getTrappedScale() {
        return this.entityData.get(TRAPPED_ENTITY_SCALE);
    }

    public void setTrappedScale(float scale) {
        this.entityData.set(TRAPPED_ENTITY_SCALE, scale);
    }

    public int getCrackAmount() {
        return this.entityData.get(CRACK_AMOUNT);
    }

    public void setCrackAmount(int amount) {
        this.entityData.set(CRACK_AMOUNT, Math.min(10, Math.max(0, amount)));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CrackAmount", this.getCrackAmount());
        tag.putFloat("FrozenWidth", this.getTrappedWidth());
        tag.putFloat("FrozenHeight", this.getTrappedHeight());
        tag.putFloat("FrozenScale", this.getTrappedScale());
        tag.putString("FrozenEntityType", this.getTrappedEntityTypeString());
        tag.put("FrozenEntityTag", this.getTrappedTag());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setCrackAmount(tag.getInt("CrackAmount"));
        this.setTrappedEntityWidth(tag.getFloat("FrozenWidth"));
        this.setTrappedHeight(tag.getFloat("FrozenHeight"));
        this.setTrappedScale(tag.getFloat("FrozenScale"));
        this.setTrappedEntityTypeString(tag.getString("FrozenEntityType"));

        if (tag.contains("FrozenEntityTag")) {
            this.setTrappedTag(tag.getCompound("FrozenEntityTag"));
        }
    }

    @Override
    public boolean isInvulnerable() {
        return true; // 无敌
    }

    @Override
    public boolean isPushable() {
        return false; // 不可推动
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true; // 可以在水下呼吸
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose poseIn) {
        return frozenSize;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return ImmutableList.of();
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {
        // 无装备
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
