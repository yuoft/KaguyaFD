package com.yuo.kaguya.Event;

import com.yuo.kaguya.Kaguya;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Kaguya.MOD_ID)
public class CursedDecoyDollEvents {

    private static final String TIMER_KEY = "CursedDecoyDespawnTimer";
    private static final String FLAG_KEY = "IsCursedDecoyWolf";
    private static final String SPAWN_TIME_KEY = "CursedDecoySpawnTime";

    // ── 到期消失 + 提示主人 ──
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Wolf wolf)) return;
        if (wolf.level().isClientSide()) return;

        CompoundTag nbt = wolf.getPersistentData();
        if (!nbt.getBoolean(FLAG_KEY)) return;
        if (!nbt.contains(TIMER_KEY)) return;

        int timer = nbt.getInt(TIMER_KEY) - 1;
        if (timer <= 0) {
            notifyOwner(wolf, Component.translatable("info.kaguya.cursed_decoy_doll.wolf_clear"));
            wolf.discard();
        } else {
            nbt.putInt(TIMER_KEY, timer);
        }
    }

    // ── 致命伤害时，魔偶狼替死 ──
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (event.isCanceled()) return;

        // 有复活图腾时优先让图腾生效，不抢
        if (player.getMainHandItem().is(Items.TOTEM_OF_UNDYING) || player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        // 只有“会致死”的伤害才触发替死（含吸收生命）
        float totalHealth = player.getHealth() + player.getAbsorptionAmount();
        if (event.getAmount() < totalHealth) return;

        // 查找玩家拥有的魔偶狼（范围 64 格，可调）
        List<Wolf> wolves = player.level().getEntitiesOfClass(Wolf.class,
                player.getBoundingBox().inflate(64.0D),
                w -> w.isAlive()
                        && w.isTame()
                        && player.getUUID().equals(w.getOwnerUUID())
                        && w.getPersistentData().getBoolean(FLAG_KEY)
        );
        if (wolves.isEmpty()) return;

        // 选“最先召唤的”（spawnTime 最小）
        wolves.sort(Comparator.comparingLong(w -> w.getPersistentData().getLong(SPAWN_TIME_KEY)));
        Wolf sacrifice = wolves.get(0);

        // 取消玩家致命伤害
        event.setCanceled(true);

        // 替死反馈
        notifyOwner(sacrifice, Component.translatable("info.kaguya.cursed_decoy_doll.wolf_did"));
        if (sacrifice.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL,
                    sacrifice.getX(), sacrifice.getY() + 0.5, sacrifice.getZ(),
                    20, 0.4, 0.6, 0.4, 0.02);
        }
        sacrifice.level().playSound(null,
                sacrifice.getX(), sacrifice.getY(), sacrifice.getZ(),
                SoundEvents.WOLF_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);

        // 静默移除（无掉落、无死亡事件）
        sacrifice.discard();
    }

    // ── 给主人发消息 ──
    private static void notifyOwner(Wolf wolf, Component message) {
        UUID ownerUUID = wolf.getOwnerUUID();
        if (ownerUUID != null && wolf.level() instanceof ServerLevel serverLevel) {
            Player owner = serverLevel.getPlayerByUUID(ownerUUID);
            if (owner != null) {
                owner.sendSystemMessage(message);
            }
        }
    }
}