package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GoldRaftEntity extends Boat {
    public static final EntityType<GoldRaftEntity> TYPE = EntityType.Builder.<GoldRaftEntity>of(GoldRaftEntity::new, MobCategory.MISC)
            .sized(1.375F, 0.5625F).clientTrackingRange(10).build("gold_raft");

    public GoldRaftEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    public GoldRaftEntity(Level level) {
        super(TYPE, level);
    }

    @Override
    public @NotNull Item getDropItem() {
        return ModItems.kinkakuzi.get();
    }
}
