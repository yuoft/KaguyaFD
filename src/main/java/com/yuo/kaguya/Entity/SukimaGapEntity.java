package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Entity.Danmaku.DanmakuColor;
import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.Prpo.GapFoldingUmbrella;
import com.yuo.kaguya.Item.Prpo.SukimaGap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SukimaGapEntity extends Entity {
    public static final EntityType<SukimaGapEntity> TYPE = EntityType.Builder.<SukimaGapEntity>of(SukimaGapEntity::new, MobCategory.MISC)
            .sized(0.75f, 0.75f).clientTrackingRange(6).updateInterval(10).fireImmune().build("gap");
    private static final int maxTick = 20 * 60 * 5; //临时隙间持续时间 5m
    private static final String NBT_COLOR = "kaguya:gapEntity_color";
    private static final String NBT_IS_TICKING = "kaguya:gapEntity_is_ticking";
    private int coolTime; //传送冷却
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(SukimaGapEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> IS_TICKING = SynchedEntityData.defineId(SukimaGapEntity.class, EntityDataSerializers.BOOLEAN); //临时隙间生存时间

    public SukimaGapEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public SukimaGapEntity(Level level, DanmakuColor color, Vec3 pos, float pitch) {
        super(TYPE, level);
        this.setPos(pos);
        this.setYRot(pitch);
        this.setColor(color);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();

        List<Entity> entityList = level.getEntitiesOfClass(Entity.class, this.getBoundingBoxForCulling(), e -> !(e instanceof Player) && !(e instanceof SukimaGapEntity) && e.isAlive());
        if (!entityList.isEmpty() && !isTpCool()) {
            for (Entity entity : entityList) {
                SukimaGapLevelSaveData.tpEntity(entity, this, level);
            }

        }

        if (!level.isClientSide) {
            if (isTicking() && this.tickCount >= maxTick) this.removeData();

            if (this.coolTime > 0) this.coolTime--;
        }else {
            if (this.tickCount % 20 == 0 && level.random.nextFloat() < 0.1f) {
                Vec3 position = position();
                for (int i = 0; i < 20; i++){
                    level.addParticle(ParticleTypes.PORTAL, position.x, position.y + 0.5, position.z, random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
                }
            }
        }
    }

    /**
     * 移除数据并删除实体
     */
    public void removeData(){
        SukimaGapLevelSaveData saveData = SukimaGapLevelSaveData.get(level());
        saveData.removeData(uuid);
        level().playSound(null, this.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);
        this.discard();
    }

    @Override
    public void playerTouch(Player player) {
        if (this.getBoundingBox().intersects(player.getBoundingBox()) && !isTpCool()){
            SukimaGapLevelSaveData.tpEntity(player, this, level());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Level level = level();
        if (stack.getItem() instanceof SukimaGap){ //回收隙间
            if (!level.isClientSide()){
                this.removeData();
                if (!player.getAbilities().instabuild){
                    if (stack.getCount() < 64) {
                        stack.grow(1);
                    } else {
                        ItemStack gapItem = new ItemStack(ModItems.sukimaGap.get(), 1);
                        if (!player.addItem(gapItem)) {
                            ItemEntity itemEntity = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), gapItem);
                            level.addFreshEntity(itemEntity);
                        }
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }else if (stack.getItem() instanceof GapFoldingUmbrella){
            if (!level.isClientSide()){
               if (player.isCrouching()){ //潜行切换隙间颜色
                   this.setColor(this.getColor().next());
               }else {
                   this.removeData();
                   if (!player.getAbilities().instabuild){
                       stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
                   }
               }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.interact(player, hand);
    }

    @Override
    public Component getName() {
        return isTicking() ? Component.translatable("entity.kaguya.gap_0", (maxTick - tickCount) / 20) : super.getName();
    }

    @Override
    public Component getDisplayName() {
        return isTicking() ? Component.translatable("entity.kaguya.gap_0", (maxTick - tickCount) / 20) : super.getDisplayName();
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return true; // 设置为无敌
    }

    @Override
    public boolean isPushable() {
        return true; // 设置为不可推动
    }

    @Override
    public boolean shouldBeSaved() {
        return true; // 实体将被保存到世界文件中
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

    @Override
    public boolean isPickable() {
        return true; //可被选择
    }

    @Override
    public boolean canBeCollidedWith() {
        return false; // 确保可以被碰撞
    }

    public int getCoolTime() {
        return coolTime;
    }

    public void setCoolTime(int coolTime) {
        this.coolTime = coolTime;
    }

    public boolean isTicking() {
        return this.entityData.get(IS_TICKING);
    }

    public void setTicking(boolean ticking) {
        this.entityData.set(IS_TICKING, ticking);
    }

    public boolean isTpCool(){
        return coolTime > 0;
    }

    public void setColor(DanmakuColor color){
        this.entityData.set(COLOR, color.ordinal());
    }

    public DanmakuColor getColor(){
        return DanmakuColor.getColor(this.entityData.get(COLOR));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, -1);
        this.entityData.define(IS_TICKING, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(NBT_COLOR)) {
            this.entityData.set(COLOR, tag.getInt(NBT_COLOR));
            this.entityData.set(IS_TICKING, tag.getBoolean(NBT_IS_TICKING));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt(NBT_COLOR, this.entityData.get(COLOR));
        tag.putBoolean(NBT_IS_TICKING, this.entityData.get(IS_TICKING));
    }
}
