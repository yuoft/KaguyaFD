package com.yuo.kaguya.Client.Render.Mob;

import com.github.tartaricacid.touhoulittlemaid.client.animation.HardcodedAnimationManger;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityFairyRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.Mob.BaseMobEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class YSMModelRender extends MobRenderer<BaseMobEntity, BedrockModel<BaseMobEntity>> {
    private static final ResourceLocation DEFAULT_TEXTURE = KaguyaUtils.fa("touhou_little_maid", "textures/entity/empty.png");
    private BedrockModel<Mob> maidModel;
    private MaidModelInfo mainInfo;
    private List<Object> mainAnimations = Lists.newArrayList();
    private boolean modelLoaded = false;

    public static @Nullable Function<Context, IGeoEntityRenderer<Mob>> YSM_ENTITY_MAID_RENDERER;
    private final GeckoEntityMaidRenderer geckoEntityMaidRenderer;
    private @Nullable IGeoEntityRenderer<Mob> ysmMaidRenderer;

    public YSMModelRender(Context context) {
        super(context, new BedrockModel<>(), 0.5F);
        this.geckoEntityMaidRenderer = new GeckoEntityMaidRenderer(context);
        this.initYsmModelRenderer(context);
    }

    private void initYsmModelRenderer(EntityRendererProvider.Context manager) {
        if (YsmCompat.isInstalled() && YSM_ENTITY_MAID_RENDERER != null) {
            IGeoEntityRenderer<Mob> geoEntityRenderer = (IGeoEntityRenderer)YSM_ENTITY_MAID_RENDERER.apply(manager);
            if (geoEntityRenderer != null) {
                this.ysmMaidRenderer = geoEntityRenderer;
                List<GeoLayerRenderer> layerRenderers = this.geckoEntityMaidRenderer.getLayerRenderers();
                Iterator var4 = layerRenderers.iterator();

                while(var4.hasNext()) {
                    GeoLayerRenderer layerRenderer = (GeoLayerRenderer)var4.next();
                    this.ysmMaidRenderer.addGeoLayerRenderer(layerRenderer.copy(this.ysmMaidRenderer));
                }
            }

        }
    }


    @Override
    public void render(BaseMobEntity mob, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        // 加载模型（只在需要时加载一次）
        if (!modelLoaded) {
            CustomPackLoader.MAID_MODELS.getModel(mob.getModelId()).ifPresent(model -> {
                this.maidModel = model;
                modelLoaded = true;
            });
            CustomPackLoader.MAID_MODELS.getInfo(mob.getModelId()).ifPresent(info -> this.mainInfo = info);
            CustomPackLoader.MAID_MODELS.getAnimation(mob.getModelId()).ifPresent((animations) -> this.mainAnimations = animations);
        }

        if (this.maidModel == null || this.mainInfo == null || this.mainAnimations.isEmpty()) {
            // 模型未加载，不渲染
            return;
        }

        poseStack.pushPose();

        this.scale(mob, poseStack, partialTicks);
        poseStack.translate(0, 1.5d, 0);

        // 计算实体的旋转角度
        float lerpBodyRot = Mth.rotLerp(partialTicks, mob.yBodyRotO, mob.yBodyRot);
        float lerpHeadRot = Mth.rotLerp(partialTicks, mob.yHeadRotO, mob.yHeadRot);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;
        float headPitch = Mth.lerp(partialTicks, mob.xRotO, mob.getXRot());
        float lerpedAge = (float)mob.tickCount + partialTicks;

        // 应用实体旋转（关键修正）
        this.setupRotations(mob, poseStack, lerpedAge, lerpBodyRot, partialTicks);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180)); //获取的模型上下颠倒

        ResourceLocation texture = getTextureLocation(mob);
        RenderType renderType = RenderType.entityCutoutNoCull(texture);
        VertexConsumer consumer = bufferSource.getBuffer(renderType);

        float limbSwing = mob.walkAnimation.position(partialTicks);
        float limbSwingAmount = mob.walkAnimation.speed(partialTicks);

        this.maidModel.setAnimations(this.mainAnimations);
        this.maidModel.setupAnim(mob, limbSwing, limbSwingAmount, mob.tickCount + partialTicks, netHeadYaw, headPitch);
        this.maidModel.renderToBuffer(poseStack, consumer, packedLight, getPackedOverlay(mob), 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

//        super.render(reimu, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    //受击红色显示 0：无， 1：红色
    public static int getPackedOverlay(LivingEntity entity) {
        return OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    @Override
    protected void scale(BaseMobEntity mob, PoseStack poseStack, float partialTickTime) {
        float scale = this.mainInfo.getRenderEntityScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    protected void setupRotations(BaseMobEntity mob, PoseStack poseStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(mob, poseStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        if (mob.getVehicle() instanceof Player && !this.mainInfo.isGeckoModel()) {
            poseStack.translate(-0.375, 0.8325, 0.375);
            poseStack.mulPose(Axis.ZN.rotationDegrees(65.0F));
            poseStack.mulPose(Axis.YN.rotationDegrees(-80.0F));
        }

        HardcodedAnimationManger.setupRotations(mob, poseStack, pAgeInTicks, pRotationYaw, pPartialTicks, this.mainInfo.isGeckoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(BaseMobEntity maid) {
        return this.mainInfo == null ? DEFAULT_TEXTURE : this.mainInfo.getTexture();
    }

}