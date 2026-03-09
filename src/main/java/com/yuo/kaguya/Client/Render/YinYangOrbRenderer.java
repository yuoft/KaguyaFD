package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.Entity.YinYangOrbEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class YinYangOrbRenderer extends EntityRenderer<YinYangOrbEntity> {
    private static final ResourceLocation TEXTURE = KaguyaUtils.fa("textures/entity/orb.png");

    public YinYangOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(YinYangOrbEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLight) {
        float size = entity.getSize();

        poseStack.pushPose();
        poseStack.translate(0, 0.1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        RenderType renderType = RenderType.itemEntityTranslucentCull(getTextureLocation(entity));

        VertexConsumer buffer = bufferIn.getBuffer(renderType);
        PoseStack.Pose poseStackLast = poseStack.last();
        Matrix4f pose = poseStackLast.pose();
        Matrix3f normal = poseStackLast.normal();

        vertex(buffer, pose, normal, -size, size, 0, 0, packedLight);
        vertex(buffer, pose, normal, -size, -size, 0, 1, packedLight);
        vertex(buffer, pose, normal, size, -size, 1, 1, packedLight);
        vertex(buffer, pose, normal, size, size, 1, 0, packedLight);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(YinYangOrbEntity entity) {
        return TEXTURE;
    }

    private static void vertex(VertexConsumer bufferIn, Matrix4f pose, Matrix3f normal, double x, double y, float u, float v, int packedLight) {
        bufferIn.vertex(pose, (float) x, (float) y, 0.0F).color(1,1,1, 1.0f).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }
}