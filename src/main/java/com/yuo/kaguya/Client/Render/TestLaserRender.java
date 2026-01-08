package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.BeamLaserEntity;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class TestLaserRender extends EntityRenderer<BeamLaserEntity> {
    public static final ResourceLocation BEAM_LOCATION = KaguyaUtils.def("textures/entity/beacon_beam.png");
    public TestLaserRender(Context context) {
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
        int length = (int) Math.ceil(laser.getLength());
        for (int i = 0; i < length; i++){
            renderBeaconBeam(laser, poseStack, bufferSource, partialTicks, laser.level().getGameTime(), i, 1, DanmakuColor.GREEN.getFloatRgb());
        }

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
    private static void renderBeaconBeam(BeamLaserEntity laser, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, long gameTime, int yh, int num, float[] color) {
        renderBeaconBeam(laser, poseStack, bufferSource, BEAM_LOCATION, partialTick, 1.0F, gameTime, yh, num, color, 0.2F, 0.25F);
    }

    /**
     * @param location // 纹理位置 - 信标光束纹理
     * @param partialTick // partialTick
     * @param uScale // 纹理缩放比例（U方向）
     * @param gameTime // gameTime
     * @param height // height 高度
     * @param segments // segments 分段
     * @param color // colors
     * @param v2 // 光束底部半径（开始半径）
     * @param v3 // 光束顶部半径（结束半径）
     */
    public static void renderBeaconBeam(BeamLaserEntity laser, PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation location, float partialTick, float uScale, long gameTime, int height, int segments, float[] color, float v2, float v3) {
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
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 2.25F - 45.0F));

        float $$30;
        float $$31 = v2;
        float $$32 = v2;
        float $$33;
        float $$34 = -v2;
        float $$35;
        float $$36;
        float $$37 = -v2;
        float $$40 = -1.0F + frac;
        float $$41 = (float)segments * uScale * (0.5F / v2) + $$40;
        renderPart(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(location, false)), r, g, b, 1.0F, height, hs, 0.0F, $$31, $$32, 0.0F, $$34, 0.0F, 0.0F, $$37, 0.0F, 1.0F, $$41, $$40);
        poseStack.popPose();
        $$30 = -v3;
        $$31 = -v3;
        $$32 = v3;
        $$33 = -v3;
        $$34 = -v3;
        $$35 = v3;
        $$36 = v3;
        $$37 = v3;
        $$40 = -1.0F + frac;
        $$41 = (float)segments * uScale + $$40;
        renderPart(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(location, true)), r, g, b, 0.125F, height, hs, $$30, $$31, $$32, $$33, $$34, $$35, $$36, $$37, 0.0F, 1.0F, $$41, $$40);
        poseStack.popPose();
    }

    private static void renderPart(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, int i, int i1, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11, float v12, float v13, float v14, float v15) {
        PoseStack.Pose $$20 = poseStack.last();
        Matrix4f $$21 = $$20.pose();
        Matrix3f $$22 = $$20.normal();
        renderQuad($$21, $$22, consumer, r, g, b, a, i, i1, v4, v5, v6, v7, v12, v13, v14, v15);
        renderQuad($$21, $$22, consumer, r, g, b, a, i, i1, v10, v11, v8, v9, v12, v13, v14, v15);
        renderQuad($$21, $$22, consumer, r, g, b, a, i, i1, v6, v7, v10, v11, v12, v13, v14, v15);
        renderQuad($$21, $$22, consumer, r, g, b, a, i, i1, v8, v9, v4, v5, v12, v13, v14, v15);
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
}
