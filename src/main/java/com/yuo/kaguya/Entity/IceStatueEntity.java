package com.yuo.kaguya.Entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class IceStatueEntity extends LivingEntity {
    public static final EntityType<IceStatueEntity> TYPE = EntityType.Builder.of(IceStatueEntity::new, MobCategory.CREATURE)
            .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).build("frozen_statue");

    private static final EntityDataAccessor<String> TRAPPED_ENTITY_TYPE = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<CompoundTag> TRAPPED_ENTITY_DATA = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_WIDTH = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_HEIGHT = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_SCALE = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CRACK_AMOUNT = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FROZEN_TIME = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> MAID_MODEL_ID = SynchedEntityData.defineId(IceStatueEntity.class, EntityDataSerializers.STRING);

    private EntityDimensions frozenSize = EntityDimensions.fixed(0.5F, 0.5F);

    public IceStatueEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    /**
     * 从原实体创建冰冻雕像
     * @param parent   被冰冻的实体
     * @param duration 冰冻持续时间（刻），-1 表示永久
     */
    public static IceStatueEntity buildFrozenStatue(LivingEntity parent, int duration, String modelId) {
        IceStatueEntity statue = ModEntityTypes.FROZEN_STATUE.get().create(parent.level());
        CompoundTag entityTag = new CompoundTag();

        try {
            if (!(parent instanceof Player)) {
                parent.saveWithoutId(entityTag);
            }
        } catch (Exception e) {
            System.out.println("Failed to save entity data: " + e);
        }

        statue.setTrappedTag(entityTag);
        statue.setTrappedEntityTypeString(ForgeRegistries.ENTITY_TYPES.getKey(parent.getType()).toString());
        statue.setTrappedEntityWidth(parent.getBbWidth());
        statue.setTrappedHeight(parent.getBbHeight());
        statue.setTrappedScale(parent.getScale());
        statue.setCrackAmount(0);
        statue.setFrozenTime(duration);
        if (!"".equals(modelId)) {
            statue.setMaidModelId(modelId);
        }

        // 设置冰块的生命值
        if (duration == -1) {  // 永久冰块
            statue.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
            statue.setHealth(20.0f);
        } else {               // 限时冰块，继承原实体的生命值
            double maxHealth = parent.getMaxHealth();
            double currentHealth = parent.getHealth();
            statue.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
            statue.setHealth((float) currentHealth);
        }

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
        // 移除生物基础逻辑
    }

    @Override
    public void aiStep() {
        // 移除AI
    }

    @Override
    public void tick() {
        super.tick();
        // 锁定旋转
        this.setYRot(this.yBodyRot);
        this.yHeadRot = this.getYRot();
        updateDimensions();

        if (!level().isClientSide) {
            int remaining = getFrozenTime();
            if (remaining > 0) {
                setFrozenTime(remaining - 1);
                if (getFrozenTime() == 0) {
                    // 时间到，自然融化，恢复原实体
                    restoreOriginalEntity();
                    this.discard();
                    return;
                }
            }

            // 生命值归零时破碎（已在hurt中处理，此处再检查一次）
            if (this.getHealth() <= 0 || this.getCrackAmount() >= 10) {
                if (this.getFrozenTime() > 0) { // 限时冰块破碎时恢复实体
                    restoreOriginalEntity();
                }
                this.playSound(SoundEvents.GLASS_BREAK);
                this.discard();
            }
        }
    }

    /**
     * 恢复被冰冻的实体（仅限非玩家实体）
     */
    private void restoreOriginalEntity() {
        if (level().isClientSide) return;
        EntityType<?> type = getTrappedEntityType();
        if (type == EntityType.PLAYER) return; // 玩家不恢复

        CompoundTag tag = getTrappedTag().copy();

        // 冰块的生命值和最大生命值（限时冰块的最大生命值 = 原实体的最大生命值）
        float iceHealth = this.getHealth();
        float iceMaxHealth = this.getMaxHealth();
        float healthRatio = iceHealth / iceMaxHealth;  // 冰块剩余生命比例

        // 创建新实体
        Entity restored = type.create(level());
        if (restored == null) return;

        // 加载保存的 NBT（包含了原实体的所有数据，包括装备、属性等）
        restored.load(tag);

        // 确保新实体的最大生命值与原实体一致（冰块的最大生命值就是原实体的最大生命值）
        if (restored instanceof LivingEntity living) {
            // 直接设置最大生命值属性
            living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(iceMaxHealth);
            // 按比例设置当前生命值
            double newHealth = iceMaxHealth * healthRatio;
            if (newHealth < 0.5) newHealth = 0.5;
            living.setHealth((float) newHealth);

            // 同步其他可能被覆盖的重要属性（例如盔甲、手持物品等已在 load 中处理）
            // 注意：load 可能会覆盖我们刚刚设置的 MaxHealth，因此必须在 load 之后设置
        }

        // 设置位置和旋转
        restored.setPos(this.getX(), this.getY(), this.getZ());
        restored.setYRot(this.getYRot());
        restored.setXRot(this.getXRot());
        if (restored instanceof LivingEntity living) {
            living.yBodyRot = this.yBodyRot;
            living.yHeadRot = this.yHeadRot;
        }

        level().addFreshEntity(restored);
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
        this.entityData.define(FROZEN_TIME, -1);
        this.entityData.define(MAID_MODEL_ID, "");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return false;
        if (this.isInvulnerableTo(source)) return false;
        if (this.getHealth() <= 0) return false;

        // 计算实际伤害
        float newHealth = this.getHealth() - amount;
        if (newHealth < 0) newHealth = 0;
        this.setHealth(newHealth);

        // 更新裂纹等级（0-10）
        int crack = (int) ((1 - newHealth / this.getMaxHealth()) * 10);
        crack = Math.min(10, Math.max(0, crack));
        this.setCrackAmount(crack);

        // 播放受击效果
        this.playSound(SoundEvents.GLASS_HIT, 1.0F, 1.0F);

        // 如果生命值归零，破碎逻辑将在 tick() 中处理
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return false;  // 允许受伤
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    // ========== Getter / Setter ==========
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
        if (this.getFrozenTime() > 0)
            amount = 0;
        this.entityData.set(CRACK_AMOUNT, Math.min(10, Math.max(0, amount)));
    }

    public int getFrozenTime() {
        return this.entityData.get(FROZEN_TIME);
    }

    public void setFrozenTime(int ticks) {
        this.entityData.set(FROZEN_TIME, ticks);
    }

    public String getMaidModelId() {
        return this.entityData.get(MAID_MODEL_ID);
    }

    public void setMaidModelId(String id) {
        this.entityData.set(MAID_MODEL_ID, id);
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
        tag.putInt("FrozenTime", this.getFrozenTime());
        tag.putString("MaidModelId", this.getMaidModelId());
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
        this.setFrozenTime(tag.getInt("FrozenTime"));
        this.setMaidModelId(tag.getString("MaidModelId"));
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
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {}

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}