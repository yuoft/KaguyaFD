package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.ArrowShotModel;
import com.yuo.kaguya.Entity.DanmakuArrow;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ArrowShotRender extends EntityRenderer<DanmakuArrow> {
    private static final ResourceLocation ARROW = RlUtil.fa("textures/entity/arrow_shot.png");
    private final ArrowShotModel model;

    public ArrowShotRender(Context context) {
        super(context);
        this.model = new ArrowShotModel(context.bakeLayer(ArrowShotModel.LAYER_LOCATION));
    }

    @Override
    public void render(DanmakuArrow butterfly, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(butterfly, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        DanmakuColor color = butterfly.getColor();

        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0,-1.25f,0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-(180.0F - entityYaw)));
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(getTextureLocation(butterfly)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuArrow entity) {
        return ARROW;
    }
}
