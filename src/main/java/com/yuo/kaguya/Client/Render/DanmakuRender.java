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

    @Override
    public void render(DanmakuBase entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        // 获取相关数据
        DanmakuColor color = entity.getColor();
        DanmakuType type = entity.getDanmakuType();

        poseStack.pushPose();
        poseStack.translate(0, 0.1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        renderQuad(bufferIn, poseStack, color, type, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuBase entity) {
        return KaguyaUtils.fa("textures/entity/danmaku/" + entity.getDanmakuType().getName() + ".png");
    }

    private static void renderQuad(MultiBufferSource bufferIn, PoseStack poseStack, DanmakuColor color, DanmakuType type, int packedLightIn){
        ResourceLocation res = KaguyaUtils.fa("textures/entity/danmaku/" + type.getName() + ".png");
        RenderType renderType = RenderType.itemEntityTranslucentCull(res);

        VertexConsumer buffer = bufferIn.getBuffer(renderType);
        PoseStack.Pose poseStackLast = poseStack.last();
        Matrix4f pose = poseStackLast.pose();
        Matrix3f normal = poseStackLast.normal();

        vertex(buffer, pose, normal, -type.getSize(), type.getSize(), 0,0, color, packedLightIn);
        vertex(buffer, pose, normal, -type.getSize(), -type.getSize(), 0,1, color, packedLightIn);
        vertex(buffer, pose, normal, type.getSize(), -type.getSize(), 1,1, color, packedLightIn);
        vertex(buffer, pose, normal, type.getSize(), type.getSize(), 1, 0, color, packedLightIn);
    }

    private static void vertex(VertexConsumer bufferIn, Matrix4f pose, Matrix3f normal, double x, double y, float u, float v, DanmakuColor color, int packedLight) {
        bufferIn.vertex(pose, (float) x, (float) y, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
