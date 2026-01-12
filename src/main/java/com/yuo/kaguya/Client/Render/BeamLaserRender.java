package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.BeamLaserEntity;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BeamLaserRender extends EntityRenderer<BeamLaserEntity>{
    public static final ResourceLocation BEAM_LOCATION = KaguyaUtils.def("textures/entity/beacon_beam.png");
    private static final ResourceLocation TEXTURE = KaguyaUtils.fa("textures/entity/star_circle.png");
    public BeamLaserRender(Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BeamLaserEntity t) {
        return null;
    }

    @Override
    public boolean shouldRender(BeamLaserEntity laser, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(BeamLaserEntity laser, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        //获取光束数据
        Vec3 vec3 = laser.getLaserDirection();
        float yaw = (float) (-Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG); //计算水平旋转角
        float pitch = (float) (-Mth.atan2(vec3.y, vec3.horizontalDistance()) * Mth.RAD_TO_DEG) + 90; //计算俯仰角
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
//        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw));
        int length = (int) Math.ceil(laser.getLength());
        // 根据生命周期调整透明度
        float age = laser.getAge() + partialTicks;
        float maxAge = laser.getMaxAge();
        float scale = Mth.clamp(age / maxAge, 0.1f, 1.0f); // 逐渐消失
        float fade = Mth.clamp(1.0f - age / maxAge, 0.1f, 1.0f);
        DanmakuColor danmakuColor = laser.getColor();
        float[] color = danmakuColor.getFloatRgb();
        color[3] = fade;
        long gameTime = laser.level().getGameTime();

        for (int i = 0; i < length; i++){
            renderBeaconBeam(poseStack, bufferSource, partialTicks, gameTime, i, 1, color, scale);
        }
        renderCircle(poseStack, bufferSource, danmakuColor, 1 + scale);
        poseStack.popPose();

        super.render(laser, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    /**
     * 渲染信标光柱效果
     * BeaconBlockEntity.BeaconBeamSection $$10 = (BeaconBlockEntity.BeaconBeamSection)$$7.get($$9);
     * renderBeaconBeam(poseStack, bufferSource, partialTick, gameTime, yh, $$9 == $$7.size() - 1 ? 1024 : $$10.getHeight(), $$10.getColor());
     * @param poseStack 位姿堆栈，用于管理渲染变换（旋转、平移、缩放等）
     * @param bufferSource 多重缓冲源，用于获取顶点缓冲区和渲染类型
     * @param partialTick 部分刻（partialTick），用于平滑动画，取值范围[0.0, 1.0)
     * @param gameTime 游戏时间（gameTime），以tick为单位，用于控制光柱动画
     * @param yh 光柱高度（y轴方向），通常对应信标金字塔层数
     * @param num 光柱段数/细分等级，影响光柱的平滑度
     * @param color 颜色数组，RGBA格式，通常长度为4：[红, 绿, 蓝, 透明度]
     */
    private static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, long gameTime, int yh, int num, float[] color, float scale) {
        renderBeaconBeam(poseStack, bufferSource, BEAM_LOCATION, partialTick, 1.0F, gameTime, yh, num, color, 0.2F * scale, 0.25F * scale);
    }

    /**
     * @param location // 纹理位置 - 信标光束纹理
     * @param partialTick // partialTick
     * @param uScale // 纹理缩放比例（U方向）
     * @param gameTime // gameTime
     * @param height // height 高度
     * @param segments // segments 分段
     * @param color // colors
     * @param bottomR // 光束底部半径（开始半径）
     * @param topR // 光束顶部半径（结束半径）
     */
    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation location, float partialTick, float uScale, long gameTime, int height, int segments, float[] color, float bottomR, float topR) {
        int hs = height + segments;
        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 0.0);
        float time = (float)Math.floorMod(gameTime, 40) + partialTick;
        float tick = segments < 0 ? time : -time;
        float frac = Mth.frac(tick * 0.2F - (float)Mth.floor(tick * 0.1F));
        float r = color[0];
        float g = color[1];
        float b = color[2];
        poseStack.pushPose();
        float $$30;
        float $$31 = bottomR;
        float $$32 = bottomR;
        float $$33;
        float $$34 = -bottomR;
        float $$35;
        float $$36;
        float $$37 = -bottomR;
        float $$40 = -1.0F + frac;
        float $$41 = (float)segments * uScale * (0.5F / bottomR) + $$40;
        renderPart(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(location, false)), r, g, b, 1.0F, height, hs, 0.0F, $$31, $$32, 0.0F, $$34, 0.0F, 0.0F, $$37, 0.0F, 1.0F, $$41, $$40);
        poseStack.popPose();
        $$30 = -topR;
        $$31 = -topR;
        $$32 = topR;
        $$33 = -topR;
        $$34 = -topR;
        $$35 = topR;
        $$36 = topR;
        $$37 = topR;
        $$40 = -1.0F + frac;
        $$41 = (float)segments * uScale + $$40;
        renderPart(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(location, true)), r, g, b, 0.125F, height, hs, $$30, $$31, $$32, $$33, $$34, $$35, $$36, $$37, 0.0F, 1.0F, $$41, $$40);
        poseStack.popPose();
    }

    private static void renderPart(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, int i, int i1, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11, float v12, float v13, float v14, float v15) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        renderQuad(matrix4f, matrix3f, consumer, r, g, b, a, i, i1, v4, v5, v6, v7, v12, v13, v14, v15);
        renderQuad(matrix4f, matrix3f, consumer, r, g, b, a, i, i1, v10, v11, v8, v9, v12, v13, v14, v15);
        renderQuad(matrix4f, matrix3f, consumer, r, g, b, a, i, i1, v6, v7, v10, v11, v12, v13, v14, v15);
        renderQuad(matrix4f, matrix3f, consumer, r, g, b, a, i, i1, v8, v9, v4, v5, v12, v13, v14, v15);
    }

    private static void renderQuad(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float r, float g, float b, float a, int i, int i1, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11) {
        addVertex(matrix4f, matrix3f, consumer, r, g, b, a, i1, v4, v5, v9, v10);
        addVertex(matrix4f, matrix3f, consumer, r, g, b, a, i, v4, v5, v9, v11);
        addVertex(matrix4f, matrix3f, consumer, r, g, b, a, i, v6, v7, v8, v11);
        addVertex(matrix4f, matrix3f, consumer, r, g, b, a, i1, v6, v7, v8, v10);
    }

    private static void addVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float r, float g, float b, float a, int i, float v4, float v5, float u, float v) {
        consumer.vertex(matrix4f, v4, (float)i, v5).color(r, g, b, a).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0F, 1.0f, 0.0F).endVertex();
    }

    /**
     * 激光原点 渲染图
     */
    private static void renderCircle(PoseStack poseStack, MultiBufferSource bufferSource, DanmakuColor color, float scale){
        poseStack.pushPose();
        poseStack.translate(-0.5,0,-0.5);
        poseStack.scale(scale, scale, scale);
        VertexConsumer builder = bufferSource.getBuffer(ModRenderType.HEART_CIRCLE.apply(TEXTURE));
        PlayerCircleRenderer.addVertex(poseStack, builder, color);
        poseStack.popPose();
    }
}
