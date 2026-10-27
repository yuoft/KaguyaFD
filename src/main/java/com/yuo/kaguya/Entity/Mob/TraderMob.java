package com.yuo.kaguya.Entity.Mob;

import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.yuo.kaguya.Entity.ModEntityTypes;
import com.yuo.kaguya.Item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 可交易的中立生物
 */
public class TraderMob extends ModNeutralMob implements Merchant {
    @Nullable
    private MerchantOffers offers;
    @Nullable
    private Player tradingPlayer;
    private int villagerXp;

    public TraderMob(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isTrading()) {
            // 停止导航（移动）
            this.getNavigation().stop();
            // 将水平运动速度归零
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            // 清除攻击目标，防止它走开去攻击
            this.setTarget(null);
        }

        if (level().isClientSide) return;

        // ─── 早苗特殊能力：中立时治疗周围生物 ───
        if (this.getType() == ModEntityTypes.KOCHIYA_SANAE.get() && !this.isTrading() && this.getRemainingPersistentAngerTime() <= 0
                && this.getTarget() == null) {

            Level level = this.level();
            if (tickCount % 20 == 0) {
                AABB aabb = this.getBoundingBox().inflate(6.0);
                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, e -> e != this && e.isAlive());
                for (LivingEntity entity : entities) {
                    if (entity.getHealth() < entity.getMaxHealth()) {
                        entity.heal(4.0F);                         // 恢复 1 点生命（半颗心）
                    }
                }
            }
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && this.getRemainingPersistentAngerTime() <= 0) {
            this.updateOffers();
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), this.getVillagerData());
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.stopTrading();
    }

    @javax.annotation.Nullable
    public Entity changeDimension(ServerLevel serverLevel, ITeleporter teleporter) {
        this.stopTrading();
        return super.changeDimension(serverLevel, teleporter);
    }

    protected void stopTrading() {
        this.setTradingPlayer((Player) null);
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateOffers();
        }
        return this.offers;
    }

    @Override
    public void overrideOffers(@Nullable MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        // 交易完成，增加商人经验
        this.villagerXp += offer.getXp();
        this.playSound(this.getNotifyTradeSound(), 1.0f, 1.0f);
        int exp = 3 + this.random.nextInt(5);

        if (offer.shouldRewardExp()) { //玩家经验
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), exp));
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        // 更新交易界面时调用（通常用于刷新）
    }

    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }

    @Override
    public void overrideXp(int xp) {
        this.villagerXp = xp;
    }

    @Override
    public boolean showProgressBar() {
        return false; // 不显示经验条
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return InitSounds.MAID_ITEM_GET.get();
    }

    public int getVillagerData() {
        // 返回交易等级（普通村民和流浪商人固定为1）
        return 1;
    }

    /**
     * 刷新交易列表
     */
    private void updateOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
        } else {
            this.offers.clear();
        }
        if (this.getType() == ModEntityTypes.KOCHIYA_SANAE.get()) {
            // 早苗：金苹果 -> 病平愈守
            ItemStack input = new ItemStack(ModItems.danmakuMaterial.get(), 16);
            ItemStack output = new ItemStack(ModItems.byoukiheiyuMamori.get());
            this.offers.add(0, getMerchantOffer(input, output, 10, 5, 0.05f));
            this.offers.add(1, getMerchantOffer(new ItemStack(Items.TOTEM_OF_UNDYING), new ItemStack(ModItems.cursedDecoyDoll.get()), 1, 20, 0));
        } else if (this.getType() == ModEntityTypes.MORICHIKA_RINNOSUKE.get()) {
            // 霖之助：TNT -> 灵击符
            ItemStack talisman = new ItemStack(ModItems.spiritualStrikeTalisman.get());
            this.offers.add(0, getMerchantOffer(new ItemStack(ModItems.danmakuMaterial.get(), 16), talisman, 8, 3, 0.03f));
            this.offers.add(1, getMerchantOffer(new ItemStack(ModItems.smallPotion.get(), 22), talisman, 8, 3, 0.03f));
            this.offers.add(2, getMerchantOffer(new ItemStack(Items.NETHER_STAR), new ItemStack(ModItems.magicBroom.get()), 2, 6, 0.03f));
            this.offers.add(3, getMerchantOffer(new ItemStack(Items.GOLD_INGOT, 2), new ItemStack(Items.ENDER_PEARL), 6, 3, 0.03f));
            this.offers.add(4, getMerchantOffer(new ItemStack(Items.LAPIS_BLOCK), new ItemStack(ModItems.thPotion.get(), 4), 12, 2, 0.03f));
            this.offers.add(5, getMerchantOffer(new ItemStack(Items.REDSTONE_BLOCK), new ItemStack(ModItems.smallPotion.get(), 2), 12, 2, 0.03f));
        }
    }

    /**
     * 添加一个新交易项
     *
     * @param maxUses         最大交易次数
     * @param villagerXp      商人获取经验
     * @param priceMultiplier 涨降价幅度
     */
    private MerchantOffer getMerchantOffer(ItemStack intputA, ItemStack intputB, ItemStack output, int maxUses, int villagerXp, float priceMultiplier) {
        return new MerchantOffer(intputA, intputB, output, 0, maxUses, villagerXp, priceMultiplier);
    }

    /**
     * 添加一个新交易项
     *
     * @param maxUses         最大交易次数
     * @param villagerXp      商人获取经验
     * @param priceMultiplier 涨降价幅度
     */
    private MerchantOffer getMerchantOffer(ItemStack intputA, ItemStack output, int maxUses, int villagerXp, float priceMultiplier) {
        return new MerchantOffer(intputA, ItemStack.EMPTY, output, 0, maxUses, villagerXp, priceMultiplier);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("VillagerXp", this.villagerXp);
        if (this.offers != null) {
            tag.put("Offers", this.offers.createTag());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("VillagerXp")) {
            this.villagerXp = tag.getInt("VillagerXp");
        }
        if (tag.contains("Offers")) {
            this.offers = new MerchantOffers(tag.getCompound("Offers"));
        }
    }
}