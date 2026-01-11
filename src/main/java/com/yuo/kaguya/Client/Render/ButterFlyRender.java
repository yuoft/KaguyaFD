package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.ButterFlyModel;
import com.yuo.kaguya.Entity.DanmakuButterfly;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ButterFlyRender extends EntityRenderer<DanmakuButterfly> {
    private static final ResourceLocation FLY = KaguyaUtils.fa("textures/entity/butterfly.png");
    private final ButterFlyModel flyModel;

    public ButterFlyRender(Context context) {
        super(context);
        this.flyModel = new ButterFlyModel(context.bakeLayer(ButterFlyModel.LAYER_LOCATION));
    }

    @Override
    public void render(DanmakuButterfly butterfly, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(butterfly, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        DanmakuColor color = butterfly.getColor();

        poseStack.pushPose();
        poseStack.scale(0.25f, 0.25f, 0.25f);
        poseStack.translate(0,-0.5f,0);
//        poseStack.mulPose(Axis.YP.rotationDegrees(-(180.0F - entityYaw)));
        float yaw = Mth.rotLerp(partialTicks, butterfly.yRotO, butterfly.getYRot()) * (Mth.PI / 180f);
        float pitch = Mth.lerp(partialTicks, butterfly.xRotO, butterfly.getXRot()) * (Mth.PI / 180f);
        // 设置模型动画
        this.flyModel.setupAnim(butterfly, 0, 0, butterfly.tickCount + partialTicks, yaw, pitch);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.flyModel.renderType(getTextureLocation(butterfly)));
        this.flyModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 0.75f);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuButterfly entity) {
        return FLY;
    }
}
