package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.MiracleCircleModel;
import com.yuo.kaguya.Client.Model.WindChargeModel;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.MiracleCircle;
import com.yuo.kaguya.Entity.WindEntity;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.awt.geom.Path2D;

public class MiracleCircleRenderer extends EntityRenderer<MiracleCircle> {
    private static final ResourceLocation TEXTURE = RlUtil.fa("textures/entity/miracle_circle.png");
    private final MiracleCircleModel model0;
    private final MiracleCircleModel model1;
    private final MiracleCircleModel model2;
    private final MiracleCircleModel model3;
    private final MiracleCircleModel model4;

    public MiracleCircleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model0 = new MiracleCircleModel(context.bakeLayer(MiracleCircleModel.LAYER_LOCATION));
        this.model1 = new MiracleCircleModel(context.bakeLayer(MiracleCircleModel.LAYER_LOCATION));
        this.model2 = new MiracleCircleModel(context.bakeLayer(MiracleCircleModel.LAYER_LOCATION));
        this.model3 = new MiracleCircleModel(context.bakeLayer(MiracleCircleModel.LAYER_LOCATION));
        this.model4 = new MiracleCircleModel(context.bakeLayer(MiracleCircleModel.LAYER_LOCATION));
    }

    @Override
    public void render(MiracleCircle miracleCircle, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        int time = miracleCircle.tickCount;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        float timer = time / 80.0f;
        float h = 0.5f;
        poseStack.pushPose();
        poseStack.translate(0, h, -0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-36f));
        poseStack.scale(1, Math.min(1.0f, timer), 1);
        render(this.model0, poseStack, consumer, packedLight);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, h - 0.1f, 0.25f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-72f));
        poseStack.scale(1, getSca(timer, 1.0f), 1);
        render(this.model1, poseStack, consumer, packedLight);
        poseStack.popPose();
//
//        poseStack.pushPose();
//        poseStack.translate(0, h, 0);
//        poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
//        poseStack.scale(1, getSca(timer, 2.0f), 1);
//        render(this.model2, poseStack, consumer, packedLight);
//        poseStack.popPose();
//
//        poseStack.pushPose();
//        poseStack.translate(0, h, 0);
//        poseStack.mulPose(Axis.ZP.rotationDegrees(-36f));
//        poseStack.scale(1, getSca(timer, 3.0f), 1);
//        render(this.model3, poseStack, consumer, packedLight);
//        poseStack.popPose();
//
//        poseStack.pushPose();
//        poseStack.translate(0, h, 0);
//        poseStack.mulPose(Axis.ZP.rotationDegrees(36f));
//        poseStack.scale(1, getSca(timer, 4.0f), 1);
//        render(this.model4, poseStack, consumer, packedLight);
//        poseStack.popPose();
        super.render(miracleCircle, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MiracleCircle entity) {
        return TEXTURE;
    }

    private float getSca(float timer, float delta) {
        return timer < delta ? 0 : Math.min(1.0f, timer - delta);
    }

    private void render(MiracleCircleModel model, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
    }
}