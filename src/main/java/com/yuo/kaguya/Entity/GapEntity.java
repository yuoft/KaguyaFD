package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import com.yuo.kaguya.Item.Prpo.SukimaGap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GapEntity extends Entity {
    public static final EntityType<GapEntity> TYPE = EntityType.Builder.<GapEntity>of(GapEntity::new, MobCategory.MISC)
            .sized(0.5f, 0.5f).clientTrackingRange(6).updateInterval(10).fireImmune().build("gap");
    private static final int maxTick = 800;
    private static final String NBT_COLOR = "kaguya:gapEntity_color";
    private int coolTime; //传送冷却
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(GapEntity.class, EntityDataSerializers.INT);

    public GapEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public GapEntity(Level level, DyeColor color, BlockPos pos, float pitch) {
        super(TYPE, level);
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.setYRot(pitch);

    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();

        List<LivingEntity> entityList = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBoxForCulling(), e -> !(e instanceof Player) && e.isAlive());
        if (!entityList.isEmpty() && !isTpCool()) {
            for (LivingEntity living : entityList) {
                KaguyaLevelSaveData.tpEntity(living, this, level);
            }

        }

        if (!level.isClientSide) {
//            if (this.tickCount >= maxTick) this.removeData();

            if (this.coolTime > 0) this.coolTime--;
        }
    }

    /**
     * 移除数据并删除实体
     */
    public void removeData(){
        KaguyaLevelSaveData saveData = KaguyaLevelSaveData.get(level());
        saveData.removePos(uuid);
        this.discard();
    }

    @Override
    public void playerTouch(Player player) {
        if (!isTpCool()){
            KaguyaLevelSaveData.tpEntity(player, this, level());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof SukimaGap){ //回收隙间
            Level level = level();
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

            player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE);
        }
        return super.interact(player, hand);
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
    public boolean isPickable() {
        return true; //可被选择
    }

    @Override
    public boolean canBeCollidedWith() {
        return false; // 确保可以被碰撞
    }

//    @Override
//    public boolean canCollideWith(Entity entity) {
//        return true; //开启实体碰撞
//    }

    //碰撞
    @Override
    protected @NotNull AABB makeBoundingBox() {
        Direction direction = this.getDirection();
        AABB aabb = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        switch (direction) {
            case NORTH, SOUTH: aabb = new AABB(-0.5, 0.0, -0.1, 0.5, 1.0, 0.1).move(this.position());break;
            case EAST, WEST: aabb = new AABB(-0.1, 0.0, -0.5, 0.1, 1.0, 0.5).move(this.position());break;
            default: break;
        }
        return aabb;
    }

    public int getCoolTime() {
        return coolTime;
    }

    public void setCoolTime(int coolTime) {
        this.coolTime = coolTime;
    }

    public boolean isTpCool(){
        return coolTime > 0;
    }

    public void setColor(DyeColor color){
        this.entityData.set(COLOR, color.getId());
    }

    public DyeColor getColor(){
        Integer colorId = this.entityData.get(COLOR);
        return DyeColor.byId(colorId);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(NBT_COLOR)) {
            this.entityData.set(COLOR, tag.getInt(NBT_COLOR));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt(NBT_COLOR, this.entityData.get(COLOR));
    }
}
