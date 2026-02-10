package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class KinkakuJiEntity extends ThrowableItemProjectile {
    public static final EntityType<KinkakuJiEntity> TYPE = EntityType.Builder.<KinkakuJiEntity>of(KinkakuJiEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("kinkaku_ji");

    public KinkakuJiEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public KinkakuJiEntity(Level level, LivingEntity living) {
        super(TYPE, living, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.kinkakuzi.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity living){
            Vec3 vec3 = result.getLocation();
            BlockPos pos = BlockPos.containing(vec3);
            Level level = this.level();

            //创建船实体
            GoldRaftEntity goldBoat = new GoldRaftEntity(level);
            goldBoat.setPos(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
            level.addFreshEntity(goldBoat);
            living.hurt(damageSources().anvil(this), (float) this.getDeltaMovement().length() * 5f);
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        Direction direction = result.getDirection();
        BlockPos pos = result.getBlockPos().relative(direction);
        Level level = this.level();

        GoldRaftEntity goldBoat = new GoldRaftEntity(level);
        goldBoat.setPos(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
        level.addFreshEntity(goldBoat);
        this.discard();
    }
}
