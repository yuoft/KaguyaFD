package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.BeamLaserEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BeamLaserRenderer extends EntityRenderer<BeamLaserEntity> {
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    public BeamLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BeamLaserEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        if (entity.getLength() <= 0) return;

        poseStack.pushPose();

        // 获取光束数据
        Vec3 startPos = entity.getStartPos();
        Vec3 direction = entity.getLaserDirection().normalize();
        double length = entity.getLength();

        // 计算旋转角度
        float yaw = (float) Math.atan2(-direction.x, direction.z) * Mth.RAD_TO_DEG;
        float pitch = (float) Math.asin(direction.y) * Mth.RAD_TO_DEG;

        Camera camera = this.entityRenderDispatcher.camera;
        Vec3 cameraPosition = camera.getPosition();

        // 移动到起点
        poseStack.translate(startPos.x - cameraPosition.x,
                startPos.y - cameraPosition.y,
                startPos.z - cameraPosition.z);

        // 应用旋转
        poseStack.mulPose(Axis.YP.rotation(yaw));      // 水平旋转
        poseStack.mulPose(Axis.XP.rotation(-pitch));   // 俯仰旋转

        // 根据生命周期调整透明度
        float age = entity.getAge() + partialTicks;
        float maxAge = entity.getMaxAge();
        float fade = 1.0f - age / maxAge; // 逐渐消失
        fade = Mth.clamp(fade, 0.0f, 1.0f);

        // 渲染光束
        renderBeam(poseStack, buffer, length, fade, packedLight);

        poseStack.popPose();
    }

    private void renderBeam(PoseStack poseStack, MultiBufferSource buffer, double length, float alpha, int packedLight) {

        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityTranslucent(BEAM_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        // 光束半径
        float radius = 0.25f;

        // 颜色（红色光束）
        float red = 1.0f;
        float green = 0.2f;
        float blue = 0.1f;

        // 构建光束的四边形（圆柱体近似）
        for (int i = 0; i < 4; i++) {
            float angle = i * (float) Math.PI / 2;
            float nextAngle = (i + 1) * (float) Math.PI / 2;

            float x1 = (float) Math.cos(angle) * radius;
            float z1 = (float) Math.sin(angle) * radius;
            float x2 = (float) Math.cos(nextAngle) * radius;
            float z2 = (float) Math.sin(nextAngle) * radius;

            // 顶部四边形
            vertexBuilder.vertex(matrix, x1, 0, z1).color(red, green, blue, alpha).uv(0, 0).overlayCoords(0, 10).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

            vertexBuilder.vertex(matrix, x2, 0, z2).color(red, green, blue, alpha).uv(1, 0).overlayCoords(0, 10).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

            vertexBuilder.vertex(matrix, x2, (float) length, z2).color(red, green, blue, alpha * 0.7f) // 末端稍暗
                    .uv(1, 1).overlayCoords(0, 10).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();

            vertexBuilder.vertex(matrix, x1, (float) length, z1).color(red, green, blue, alpha * 0.7f).uv(0, 1).overlayCoords(0, 10).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        }

        // 渲染光束核心（更亮的内部）
        renderBeamCore(poseStack, buffer, length, alpha);
    }

    private void renderBeamCore(PoseStack poseStack, MultiBufferSource buffer, double length, float alpha) {

        VertexConsumer coreBuilder = buffer.getBuffer(RenderType.LINES);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        // 核心颜色（更亮的红色）
        float red = 1.0f;
        float green = 0.5f;
        float blue = 0.3f;

        // 绘制光束中心线
        coreBuilder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).normal(pose.normal(), 0, 1, 0).endVertex();

        coreBuilder.vertex(matrix, 0, (float) length, 0).color(red, green, blue, alpha * 0.5f).normal(pose.normal(), 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BeamLaserEntity entity) {
        return BEAM_TEXTURE;
    }
}