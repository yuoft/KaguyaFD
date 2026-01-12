package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.ArrowShotModel;
import com.yuo.kaguya.Entity.DanmakuArrow;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ArrowShotRender extends EntityRenderer<DanmakuArrow> {
    private static final ResourceLocation ARROW = KaguyaUtils.fa("textures/entity/arrow_shot.png");
    private final ArrowShotModel arrowModel;

    public ArrowShotRender(Context context) {
        super(context);
        this.arrowModel = new ArrowShotModel(context.bakeLayer(ArrowShotModel.LAYER_LOCATION));
    }

    @Override
    public void render(DanmakuArrow arrow, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        DanmakuColor color = arrow.getColor();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.arrowModel.renderType(getTextureLocation(arrow)));

        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0,-1.25f,0);
//        float yaw = Mth.rotLerp(partialTicks, arrow.yRotO, arrow.getYRot()) * (Mth.PI / 180f) - 90 * (Mth.PI / 180f);
//        float pitch = Mth.lerp(partialTicks, arrow.xRotO, arrow.getXRot()) * (Mth.PI / 180f);
//        this.arrowModel.setRotationAngles(pitch, yaw);

        Vec3 vec3 = arrow.getDeltaMovement();
        float yaw = (float) (-Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG); //计算水平旋转角
        float pitch = (float) (-Mth.atan2(vec3.y, vec3.horizontalDistance()) * Mth.RAD_TO_DEG); //计算俯仰角
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));

        this.arrowModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 0.75f);
        poseStack.popPose();

        super.render(arrow, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuArrow entity) {
        return ARROW;
    }
}
