package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.IceStatueEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class IceStatueRender extends EntityRenderer<IceStatueEntity> {
    // 冰裂纹纹理 (0-9)
    protected static final ResourceLocation[] CRACK_STAGES = new ResourceLocation[]{
            KaguyaUtils.def("textures/block/destroy_stage_0.png"),
            KaguyaUtils.def("textures/block/destroy_stage_1.png"),
            KaguyaUtils.def("textures/block/destroy_stage_2.png"),
            KaguyaUtils.def("textures/block/destroy_stage_3.png"),
            KaguyaUtils.def("textures/block/destroy_stage_4.png"),
            KaguyaUtils.def("textures/block/destroy_stage_5.png"),
            KaguyaUtils.def("textures/block/destroy_stage_6.png"),
            KaguyaUtils.def("textures/block/destroy_stage_7.png"),
            KaguyaUtils.def("textures/block/destroy_stage_8.png"),
            KaguyaUtils.def("textures/block/destroy_stage_9.png")};

    // 冰材质
    private static final ResourceLocation ICE_TEXTURE = KaguyaUtils.def("textures/block/ice.png");

    // 缓存模型和虚拟实体
    private final Map<String, EntityModel<LivingEntity>> modelCache = new HashMap<>();
    private final Map<String, LivingEntity> entityCache = new HashMap<>();
    private final EntityRendererProvider.Context context;

    public IceStatueRender(EntityRendererProvider.Context context) {
        super(context);
        this.context = context;
        this.shadowRadius = 0.5F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IceStatueEntity entity) {
        return ICE_TEXTURE;
    }

    @Override
    public void render(IceStatueEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        // 获取实体类型
        EntityType<LivingEntity> trappedType = (EntityType<LivingEntity>) entity.getTrappedEntityType();
        String typeKey = entity.getTrappedEntityTypeString();

        // 获取模型
        EntityModel<LivingEntity> model = getModelForType(entity, trappedType, typeKey);
        if (model == null) return;

        // 获取虚拟实体（用于动画）
        LivingEntity fakeEntity = getFakeEntity(entity, trappedType, typeKey);

        // 设置模型属性
        setupModel(entity, model, fakeEntity, partialTicks);

        // 渲染冰雕主体
        renderStatue(entity, model, poseStack, buffer, packedLight, partialTicks);

        // 渲染冰裂纹（如果有）
        if (entity.getCrackAmount() > 0) {
            renderCracks(entity, model, poseStack, buffer, packedLight, partialTicks);
        }
    }

    /**
     * 获取实体的模型
     */
    private EntityModel<LivingEntity> getModelForType(IceStatueEntity entity, EntityType<?> type, String typeKey) {
        // 从缓存获取
        if (modelCache.containsKey(typeKey)) {
            return modelCache.get(typeKey);
        }

        EntityModel<LivingEntity> model = null;

        // 获取原版渲染器的模型
        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(type);
        if (renderer instanceof RenderLayerParent<?, ?> layerParent) {
            model = (EntityModel<LivingEntity>) layerParent.getModel();
        } else if (type == EntityType.PLAYER) { // 特殊处理玩家
            model = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        }

        // 缓存模型
        if (model != null) {
            modelCache.put(typeKey, model);
        }

        return model;
    }

    /**
     * 获取虚拟实体（用于动画）
     */
    private LivingEntity getFakeEntity(IceStatueEntity entity, EntityType<LivingEntity> type, String typeKey) {
        if (entityCache.containsKey(typeKey)) {
            return entityCache.get(typeKey);
        }

        LivingEntity fakeEntity = type.create(Minecraft.getInstance().level);
        if (fakeEntity != null) {
            try {
                fakeEntity.load(entity.getTrappedTag());
            } catch (Exception e) {
                System.out.println("Failed to load entity " + typeKey + " from " + entity.getTrappedTag());
            }
            entityCache.put(typeKey, fakeEntity);
        }

        return fakeEntity;
    }

    /**
     * 设置模型属性
     */
    private void setupModel(IceStatueEntity entity, EntityModel<LivingEntity> model, LivingEntity fakeEntity, float partialTicks) {
        model.young = entity.isBaby();
        model.riding = entity.isPassenger();
        model.attackTime = entity.getAttackAnim(partialTicks);

        // 复制动画状态
        model.setupAnim(fakeEntity, 0, 0, entity.tickCount + partialTicks, 0, 0);
    }

    /**
     * 渲染冰雕
     */
    private void renderStatue(IceStatueEntity entity, EntityModel<?> model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks) {

        poseStack.pushPose();

        // 应用缩放
        float scale = entity.getTrappedScale();
        poseStack.scale(scale, scale, scale);

        // 移动到正确位置
        poseStack.translate(0, 1.5, 0);

        // 应用旋转
        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // 获取渲染类型（半透明冰材质）
        RenderType renderType = RenderType.entityTranslucent(getTextureLocation(entity));
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

        // 渲染模型
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.75F); // 稍微透明

        poseStack.popPose();
    }

    /**
     * 渲染冰裂纹
     */
    private void renderCracks(IceStatueEntity entity, EntityModel<?> model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks) {

        int crackAmount = Mth.clamp(entity.getCrackAmount() - 1, 0, CRACK_STAGES.length - 1);
        ResourceLocation crackTexture = CRACK_STAGES[crackAmount];

        poseStack.pushPose();

        // 应用相同的变换
        float scale = entity.getTrappedScale();
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, 1.5, 0);

        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        // 使用叠加渲染类型
        RenderType crackRenderType = ModRenderType.getIceCrackRenderType(crackTexture);
        VertexConsumer crackConsumer = buffer.getBuffer(crackRenderType);

        // 渲染裂纹（在冰雕上方）
        model.renderToBuffer(poseStack, crackConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);

        poseStack.popPose();
    }
}
