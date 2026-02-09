package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Entity.DanmakuBase;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class DanmakuRender extends EntityRenderer<DanmakuBase> {

    public DanmakuRender(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    private static void renderQuad(MultiBufferSource bufferIn, PoseStack poseStack, DanmakuColor color, DanmakuType type, int packedLightIn) {
        ResourceLocation res = KaguyaUtils.fa("textures/entity/danmaku/" + type.getName() + ".png");
        RenderType renderType = RenderType.itemEntityTranslucentCull(res);

        VertexConsumer buffer = bufferIn.getBuffer(renderType);
        PoseStack.Pose poseStackLast = poseStack.last();
        Matrix4f pose = poseStackLast.pose();
        Matrix3f normal = poseStackLast.normal();

        vertex(buffer, pose, normal, -type.getSize(), type.getSize(), 0, 0, color, packedLightIn);
        vertex(buffer, pose, normal, -type.getSize(), -type.getSize(), 0, 1, color, packedLightIn);
        vertex(buffer, pose, normal, type.getSize(), -type.getSize(), 1, 1, color, packedLightIn);
        vertex(buffer, pose, normal, type.getSize(), type.getSize(), 1, 0, color, packedLightIn);
    }

    private static void renderGlowQuad(MultiBufferSource bufferIn, PoseStack poseStack, DanmakuColor color, DanmakuType type) {
        ResourceLocation res = KaguyaUtils.fa("textures/entity/danmaku/" + type.getName() + ".png");

        // 使用发光渲染类型
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(res));
        PoseStack.Pose poseStackLast = poseStack.last();
        Matrix4f pose = poseStackLast.pose();
        Matrix3f normal = poseStackLast.normal();

        // 使用更高的亮度值（15是最大亮度）
        int glowLight = 15728880; // 对应亮度15

        // 让颜色更亮
        float glowFactor = 1.25f;
        float red = Math.min(1.0f, color.getFloatRed() * glowFactor);
        float green = Math.min(1.0f, color.getFloatGreen() * glowFactor);
        float blue = Math.min(1.0f, color.getFloatBlue() * glowFactor);

        vertexGlow(buffer, pose, normal, -type.getSize(), type.getSize(), 0, 0, red, green, blue, glowLight);
        vertexGlow(buffer, pose, normal, -type.getSize(), -type.getSize(), 0, 1, red, green, blue, glowLight);
        vertexGlow(buffer, pose, normal, type.getSize(), -type.getSize(), 1, 1, red, green, blue, glowLight);
        vertexGlow(buffer, pose, normal, type.getSize(), type.getSize(), 1, 0, red, green, blue, glowLight);
    }

    private static void vertex(VertexConsumer bufferIn, Matrix4f pose, Matrix3f normal, double x, double y, float u, float v, DanmakuColor color, int packedLight) {
        bufferIn.vertex(pose, (float) x, (float) y, 0.0F).color(color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 1.0f).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void vertexGlow(VertexConsumer bufferIn, Matrix4f pose, Matrix3f normal, double x, double y, float u, float v, float r, float g, float b, int packedLight) {
        bufferIn.vertex(pose, (float) x, (float) y, 0.0F).color(r, g, b, 0.8f).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public void render(DanmakuBase entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        DanmakuColor color = entity.getColor();
        DanmakuType type = entity.getDanmakuType();

        poseStack.pushPose();
        poseStack.translate(0, 0.1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        // 渲染主体
        renderQuad(bufferIn, poseStack, color, type, packedLightIn);

        // 渲染发光层（使用更高的亮度）
        if (isLightType(type))
            renderGlowQuad(bufferIn, poseStack, color, type);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuBase entity) {
        return KaguyaUtils.fa("textures/entity/danmaku/" + entity.getDanmakuType().getName() + ".png");
    }

    private static boolean isLightType(DanmakuType type){
        return type == DanmakuType.LIGHT_BIG_BALL || type == DanmakuType.LIGHT_MEDIUM_BALL;
    }
}
