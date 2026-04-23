package com.yuo.kaguya.Entity;

import com.yuo.kaguya.Effect.ModEffects;
import com.yuo.kaguya.Item.Weapon.DanmakuDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DanmakuBase extends ThrowableProjectile {
    public static final EntityType<DanmakuBase> TYPE = EntityType.Builder.<DanmakuBase>of(DanmakuBase::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(10).noSave().build("danmaku");

    protected int MAX_TICKS_EXISTED; //生命周期
    protected static final int DEF_MAX_TICKS_EXISTED = 200; //生命周期
    protected DanmakuType danmakuType;
    protected DanmakuColor danmakuColor;
    protected boolean isDanmakuPierce; //是否在击中实体后销毁
    protected boolean isSpawnFlower; //是否在落在草地后生成花
    private boolean isDiffusionSpawned; //是否由扩散效果生成
    private boolean isHomingTarget; //是否可以追踪目标
    private LivingEntity homingTarget;          // 追踪目标
    private static final int HOMING_COOLDOWN = 4; // 每4 tick更新一次方向
    protected static final EntityDataAccessor<Integer> DANMAKU_TYPE = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.INT); //弹幕类型--圆，方，星。。。
    protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.INT); //弹幕颜色
    protected static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.FLOAT); //弹幕攻击伤害
    protected static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(DanmakuBase.class, EntityDataSerializers.FLOAT); //重力

    public DanmakuBase(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        if (danmakuType == null)
            this.danmakuType = DanmakuType.TINY_BALL;
    }

    public DanmakuBase(EntityType<? extends ThrowableProjectile> entityType, Level level, LivingEntity living) {
        super(entityType, living, level);
        if (danmakuType == null)
            this.danmakuType = DanmakuType.TINY_BALL;
    }

    public DanmakuBase(Level level, LivingEntity living, DanmakuType danmakuType, DanmakuColor danmakuColor) {
        this(TYPE, level, living);
        this.danmakuType = danmakuType;
        this.danmakuColor = danmakuColor;
        this.setDanmakuType(this.danmakuType);
        this.setColor(this.danmakuColor);
        this.setGravityVelocity(0);
        this.setMaxTicksExisted(DEF_MAX_TICKS_EXISTED);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result); // 调用父类方法

        Entity entity = result.getEntity();
        if (entity == this || entity == this.getOwner()) {
            return;
        }
        Level level = this.level();

        if (entity instanceof LivingEntity target) {
            DamageSource damageSource;
            if (this.getOwner() instanceof LivingEntity owner){
                damageSource = DanmakuDamageTypes.danmaku(target, owner);
            }else {
                damageSource = this.damageSources().generic();
            }

            if (this.getOwner() instanceof LivingEntity owner) {
                // 设置最后攻击者
                target.setLastHurtByMob(owner);
                // 应用伤害
                if (target.hurt(damageSource, getDamage())) {
                    // 触发玩家攻击成功事件（如果攻击者是玩家）
                    if (target != owner && target instanceof Player && owner instanceof ServerPlayer && !this.isSilent()) {
                        ((ServerPlayer)owner).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                    }

                    if (!level.isClientSide) {
                        this.knockbackTarget(target);
                        this.doDamageEffects(owner, target);
                        if (!isDanmakuPierce()) {
                            if (!isDiffusionSpawned() && owner.isAlive()) {
                                MobEffectInstance effect = owner.getEffect(ModEffects.diffusion.get());
                                if (effect != null) {
                                    int extraCount = Math.min((effect.getAmplifier() + 1) * 2, 20); //限制buff X级
                                    spawnDiffusionDanmaku(owner, level, extraCount);
                                }
                            }
                            this.discard();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        BlockPos pos = result.getBlockPos();
        Level level = level();
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() == Blocks.GRASS_BLOCK && this.isSpawnFlower){
                BlockPos above = pos.above();
                DanmakuColor color = this.getColor();
                BlockState flowerByColor = getFlowerByColor(color);
                level.setBlockAndUpdate(above, flowerByColor);
                this.discard();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.level();

        // 追踪逻辑
        if (!level.isClientSide) {
            if (this.tickCount % HOMING_COOLDOWN == 0 && isHomingTarget()){
                if (!this.hasHomingTarget()){ //没目标
                    //重新追踪一个目标
                    LivingEntity target = findHomingTarget(this, this.tickCount < HOMING_COOLDOWN ? 16 : 8);
                    if (target != null) {
                        this.setHomingTarget(target);
                    }
                }else applyHomingBehavior(level);
            }
        }

        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 57.2957763671875));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * 57.2957763671875));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        if (this.tickCount >= getMAX_TICKS_EXISTED()) {
            this.discard();
        }
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        double inflate = (this.getDanmakuType().getSize() - 0.2d) / 2;
        if (this.getDanmakuType().getName().contains("laser")){ //激光模型box
            inflate = -0.05d;
        }
        return new AABB(-0.125, -0.125, -0.125, 0.125, 0.125, 0.125).inflate(inflate).move(this.position());
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DANMAKU_TYPE, DanmakuType.TINY_BALL.ordinal());
        this.entityData.define(COLOR, DanmakuColor.GREEN.ordinal());
        this.entityData.define(DAMAGE, DanmakuType.TINY_BALL.getDamage());
        this.entityData.define(GRAVITY, 0f); //默认无重力
    }

    //重力
    @Override
    protected float getGravity() {
        return this.entityData.get(GRAVITY);
    }

    public DanmakuBase setGravityVelocity(float gravity) {
        this.entityData.set(GRAVITY, gravity);
        return this;
    }

    public void setMaxTicksExisted(int ticksExisted) {
        MAX_TICKS_EXISTED = ticksExisted;
    }

    public int getMAX_TICKS_EXISTED() {
        return MAX_TICKS_EXISTED == 0 ? DEF_MAX_TICKS_EXISTED : MAX_TICKS_EXISTED;
    }

    public DanmakuType getDanmakuType() {
        return DanmakuType.getType(this.entityData.get(DANMAKU_TYPE));
    }

    public DanmakuBase setDanmakuType(DanmakuType type) {
        this.entityData.set(DANMAKU_TYPE, type.ordinal());
        this.setDamage(type.getDamage());
        return this;
    }

    public DanmakuColor getColor() {
        return DanmakuColor.getColor(this.entityData.get(COLOR));
    }

    public DanmakuBase setColor(DanmakuColor color) {
        this.entityData.set(COLOR, color.ordinal());
        return this;
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public DanmakuBase setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
        return this;
    }

    public boolean isDanmakuPierce() {
        return isDanmakuPierce;
    }

    public void setDanmakuPierce(boolean danmakuPierce) {
        isDanmakuPierce = danmakuPierce;
    }

    public boolean isSpawnFlower() {
        return isSpawnFlower;
    }

    public void setSpawnFlower(boolean spawnFlower) {
        isSpawnFlower = spawnFlower;
    }

    public boolean isDiffusionSpawned() {
        return isDiffusionSpawned;
    }

    public void setDiffusionSpawned(boolean diffusionSpawned) {
        isDiffusionSpawned = diffusionSpawned;
    }

    public boolean isHomingTarget() {
        return isHomingTarget;
    }

    public void setHomingTarget(boolean homingTarget) {
        isHomingTarget = homingTarget;
    }

    public void setHomingTarget(LivingEntity target) {
        this.homingTarget = target;
    }

    public boolean hasHomingTarget() {
        return homingTarget != null && homingTarget.isAlive();
    }

    /**
     * 根据弹幕颜色在草地上生成对应的花
     */
    private BlockState getFlowerByColor(DanmakuColor color) {
        BlockState state;
        switch (color) {
            case WHITE:
                state = Blocks.LILY_OF_THE_VALLEY.defaultBlockState();
                break; // 白色 -> 铃兰
            case ORANGE:
                state = Blocks.ORANGE_TULIP.defaultBlockState();
                break; // 橙色 -> 橙色郁金香
            case MAGENTA:
                state = Blocks.ALLIUM.defaultBlockState();
                break; //绒球葱
            case LIGHT_BLUE:
                state = Blocks.BLUE_ORCHID.defaultBlockState();
                break;  // 淡蓝色 -> 兰花
            case YELLOW:
                state = Blocks.DANDELION.defaultBlockState();
                break; // 黄色 -> 蒲公英
            case PINK:
                state = Blocks.PINK_TULIP.defaultBlockState();
                break; // 粉红色郁金香

            // 淡灰色 -> 滨菊 或 蓝花美耳草 或 白色郁金香
            case LIGHT_GRAY: {
                int r = random.nextInt(3);
                if (r == 0) state = Blocks.OXEYE_DAISY.defaultBlockState();    // 滨菊
                else if (r == 1) state = Blocks.AZURE_BLUET.defaultBlockState(); // 蓝花美耳草
                else state = Blocks.WHITE_TULIP.defaultBlockState();          // 白色郁金香
            }
            break;
            case BLUE:
                state = Blocks.CORNFLOWER.defaultBlockState();
                break;  // 蓝色 -> 矢车菊
            case GREEN:
                state = Blocks.FERN.defaultBlockState();
                break;    // 蕨

            // 红色 -> 红色郁金香 或 虞美人
            case RED: {
                int r = random.nextInt(2);
                if (r == 0) state = Blocks.RED_TULIP.defaultBlockState();    // 红色郁金香
                else state = Blocks.POPPY.defaultBlockState();    // 虞美人
            }
            break;
            case BLACK:
                state = Blocks.WITHER_ROSE.defaultBlockState();
                break; // 黑色 -> 凋零玫瑰

            default:
                state = Blocks.DEAD_BUSH.defaultBlockState();
                break; // 默认 -> 枯萎灌木
        }
        return state;
    }

    /**
     * 弹幕击退
     */
    protected void knockbackTarget(LivingEntity target) {
        double d0 = Math.max(0.0, 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(0.2 * d0);
        if (vec3.lengthSqr() > 0.0) {
            target.push(vec3.x, 0.1, vec3.z);
        }
    }

    /**
     * 触发效果（如火焰、附加buff等）
     */
    protected void doDamageEffects(LivingEntity owner, LivingEntity target) {

    }

    /**
     * 向四周随机方向发射扩散弹幕
     * @param owner 弹幕所有者（用于设置新弹幕的所有者）
     * @param count 要生成的弹幕数量
     */
    private void spawnDiffusionDanmaku(LivingEntity owner, Level level, int count) {
        Vec3 pos = this.position();
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            // 创建与当前弹幕相同类型和颜色的新弹幕
            DanmakuBase newDanmaku = new DanmakuBase(level, owner, this.danmakuType, this.danmakuColor);
            newDanmaku.setPos(pos);
            newDanmaku.setDamage(this.getDamage());
            newDanmaku.setGravityVelocity(this.getGravity());
            newDanmaku.setDanmakuPierce(this.isDanmakuPierce());
            newDanmaku.setSpawnFlower(this.isSpawnFlower());
            newDanmaku.setMaxTicksExisted(this.getMAX_TICKS_EXISTED());
            newDanmaku.setOwner(this.getOwner());
            // 标记为由扩散生成，防止二次扩散
            newDanmaku.setDiffusionSpawned(true);

            // 生成随机方向（球面均匀分布）
            double theta = random.nextDouble() * 2 * Math.PI;      // 水平角
            double phi = Math.acos(2 * random.nextDouble() - 1);   // 垂直角（保证球面均匀）

            double dx = Math.sin(phi) * Math.cos(theta);
            double dy = Math.sin(phi) * Math.sin(theta);
            double dz = Math.cos(phi);

            // 设置速度（速度大小与原弹幕当前速度一致或使用固定值）
            float speed = (float) this.getDeltaMovement().length() / 2;
            if (speed < 0.1f) speed = DanmakuShootHelper.VAL_DEF / 4; // 若原弹幕速度过小，使用默认速度

            newDanmaku.setDeltaMovement(dx * speed, dy * speed, dz * speed);

            DanmakuShootHelper.addEntityAndSound(level, owner, newDanmaku);
        }

        // 播放一个音效提示（可选）
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, owner.getSoundSource(), 0.5f, 1.2f);
    }

    /**
     * 执行追踪行为：调整弹幕速度方向指向目标
     */
    private void applyHomingBehavior(Level level) {
        if (homingTarget == null || !homingTarget.isAlive()) {
            homingTarget = null;
            return;
        }

        // 目标位置（瞄准身体中心偏上）
        Vec3 targetPos = homingTarget.position().add(0, homingTarget.getBbHeight() * 0.5, 0);
        Vec3 myPos = this.position();
        Vec3 direction = targetPos.subtract(myPos).normalize();
        double randomFactor = 0.05; // 5% 随机偏移
        direction = direction.add((
                level.random.nextDouble() - 0.5) * randomFactor,
                (level.random.nextDouble() - 0.5) * randomFactor,
                (level.random.nextDouble() - 0.5) * randomFactor).normalize();

        // 当前速度大小
        double currentSpeed = this.getDeltaMovement().length();
        // 设置新的速度方向，速度大小保持不变（或可稍微增加追踪力度）
        this.setDeltaMovement(direction.scale(currentSpeed));

        // 可选：增加一点视觉反馈（追踪尾迹粒子）
        level.addParticle(ParticleTypes.END_ROD, myPos.x, myPos.y, myPos.z, 0, 0, 0);
    }

    /**
     * 寻找适合追踪的敌对目标
     * @param danmaku 玩家
     * @param maxDistance 最大搜索距离
     * @return 最近的符合条件的生物，若无则返回 null
     */
    public LivingEntity findHomingTarget(DanmakuBase danmaku, double maxDistance) {
        Level level = this.level();
        Entity owner = danmaku.getOwner();
        LivingEntity closestTarget = null;
        if (owner instanceof LivingEntity living) {
            AABB searchBox = danmaku.getBoundingBox().inflate(maxDistance);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != living && e.isAlive() && e instanceof Enemy);

            double closestDistanceSqr = Double.MAX_VALUE;

            for (LivingEntity entity : entities) {
                // 1. 刚发射时的目标必须直接可见（无方块遮挡）
                if (this.tickCount <= HOMING_COOLDOWN && !living.hasLineOfSight(entity)) continue;

                // 2. 计算距离（使用平方距离，避免 sqrt）
                double distSqr = entity.distanceToSqr(danmaku);
                if (distSqr > maxDistance * maxDistance) continue;

                if (distSqr < closestDistanceSqr) {
                    closestDistanceSqr = distSqr;
                    closestTarget = entity;
                }
            }
        }

        return closestTarget;
    }
}
