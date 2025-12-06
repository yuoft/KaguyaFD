package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuButterfly;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;


public class FlyRender extends EntityRenderer<DanmakuButterfly> {
    private static final ResourceLocation FLY_L = RlUtil.fa("textures/entity/butterfly_shot.png");
    private static final ResourceLocation FLY_R = RlUtil.fa("textures/entity/butterfly_shot_r.png");

    public FlyRender(Context context) {
        super(context);
    }

    @Override
    public void render(DanmakuButterfly danmaku, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        super.render(danmaku, entityYaw, partialTicks, poseStack, bufferSource, i);
        int tickCount = danmaku.tickCount;
        double a = 0.5d;
        float mul = 45f * Mth.sin(tickCount / 90f * Mth.PI); //旋转角度

        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.XP.rotationDegrees(45f));
        poseStack.mulPose(Axis.YP.rotationDegrees(mul));

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(FLY_L));
        buffer.color(DanmakuColor.GREEN.getRgb());
        PoseStack.Pose poseStackLast = poseStack.last();
        Matrix4f pose = poseStackLast.pose();
        Matrix3f normal = poseStackLast.normal();

        vertex(buffer, pose, normal, -a, a, 0, 0, i);
        vertex(buffer, pose, normal, -a, -a, 0, 1, i);
        vertex(buffer, pose, normal, a, -a, 1, 1, i);
        vertex(buffer, pose, normal, a, a, 1, 0, i);

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.XP.rotationDegrees(45f));
        poseStack.mulPose(Axis.YP.rotationDegrees(-mul)); //对称旋转

        VertexConsumer buffer0 = bufferSource.getBuffer(RenderType.entityCutoutNoCull(FLY_R));
        PoseStack.Pose poseStackLast0 = poseStack.last();
        Matrix4f pose0 = poseStackLast0.pose();
        Matrix3f normal0 = poseStackLast0.normal();

        vertex(buffer0, pose0, normal0, -a, a, 0, 0, i);
        vertex(buffer0, pose0, normal0, -a, -a, 0, 1, i);
        vertex(buffer0, pose0, normal0, a, -a, 1, 1, i);
        vertex(buffer0, pose0, normal0, a, a, 1, 0, i);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer bufferIn, Matrix4f pose, Matrix3f normal, double x, double y, double texU, double texV, int packedLight) {
        bufferIn.vertex(pose, (float) x, (float) y, 0.0F).color(255, 255, 255, 255).uv((float) texU, (float) texV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    /** 蝶弾 */
    public void renderButterflyShot(float size, int color, int count, float yaw, float pitch, float slope) {
        Tesselator tesselator = Tesselator.getInstance();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);//両面描画
        GL11.glScalef(size, size, size);
        float wingAngle = Mth.sin((float)count / 3.0F) * 45F;
        if(pitch > 90F)
        {
            pitch = 90F - pitch % 90F;
        }
        else if(pitch < -90F)
        {
            pitch = -90F - pitch % 90F;
        }
        GL11.glRotatef( yaw,   0F, 1F, 0F);
        GL11.glRotatef(-pitch, 1F, 0F, 0F);
        GL11.glRotatef( slope, 0F, 0F, 1F);
        GL11.glRotatef(wingAngle, 0.0F, 0.0F, 1.0F);

        color %= 8;
        float minU =  0F / 128F;
        float maxU = 32F / 128F;
        float minV = 0F;
        float maxV = 1F;
        float width = 2.0F;
        float width2 = 1.8F;

        BufferBuilder builder = tesselator.getBuilder();
//        tesselator.startDrawingQuads();
//        builder.normal(0.0F, 1.0F, 0.0F);
//        tesselator.setColorRGBA_F(colorR[color], colorG[color], colorB[color], 1.0F);
//        tesselator.addVertexWithUV( 0.0F, 0.0F,  width, maxU, minV);
//        tesselator.addVertexWithUV(width, 0.0F,  width, minU, minV);
//        tesselator.addVertexWithUV(width, 0.0F, -width, minU, maxV);
//        tesselator.addVertexWithUV( 0.0F, 0.0F, -width, maxU, maxV);
//        tesselator.draw();
//        tesselator.startDrawingQuads();
//        tesselator.setNormal(0.0F, 1.0F, 0.0F);
//        tesselator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 0.4F);
//        tesselator.addVertexWithUV( 0.0F , 0.000F,  width2, maxU, minV);
//        tesselator.addVertexWithUV(width2, 0.000F,  width2, minU, minV);
//        tesselator.addVertexWithUV(width2, 0.000F, -width2, minU, maxV);
//        tesselator.addVertexWithUV( 0.0F , 0.000F, -width2, maxU, maxV);
//        tesselator.draw();
        GL11.glRotatef(-wingAngle * 2F, 0.0F, 0.0F, 1.0F);
        width *= 1.0F;
        width2 *= 1.0F;

//        tesselator.startDrawingQuads();
//        tesselator.setNormal(0.0F, 1.0F, 0.0F);
//        tesselator.setColorRGBA_F(colorR[color], colorG[color], colorB[color], 1.0F);
//        tesselator.addVertexWithUV( 0.0F, 0.0F,  width, maxU, minV);
//        tesselator.addVertexWithUV(-width, 0.0F, width, minU, minV);
//        tesselator.addVertexWithUV(-width, 0.0F, -width, minU, maxV);
//        tesselator.addVertexWithUV( 0.0F, 0.0F, -width, maxU, maxV);
//        tesselator.draw();
//        tesselator.startDrawingQuads();
//        tesselator.setNormal(0.0F, 1.0F, 0.0F);
//        tesselator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 0.4F);
//        tesselator.addVertexWithUV( 0.0F , 0.000F,  width2, maxU, minV);
//        tesselator.addVertexWithUV(-width2, 0.000F, width2, minU, minV);
//        tesselator.addVertexWithUV(-width2, 0.000F, -width2, minU, maxV);
//        tesselator.addVertexWithUV( 0.0F , 0.000F, -width2, maxU, maxV);
//        tesselator.draw();
        GL11.glEnable(GL11.GL_CULL_FACE);//表綿描画
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuButterfly entityDanmaku) {
        return FLY_L;
    }
}
