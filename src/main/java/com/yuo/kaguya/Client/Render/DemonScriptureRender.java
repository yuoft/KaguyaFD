package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.*;
import com.yuo.kaguya.Entity.DemonScriptureEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class DemonScriptureRender extends EntityRenderer<DemonScriptureEntity> {

    private static final ResourceLocation TEXTURE = KaguyaUtils.fa("textures/entity/mazinkyoukan_texture.png");

    // 自定义双面半透明渲染类型（安全，无报错）
    private static final RenderType RENDER_TYPE = RenderType.create("mazinkyoukan",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false))
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .createCompositeState(false));

    public DemonScriptureRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DemonScriptureEntity entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        // 不需要额外的 translate/rotate，实体位置已经由 EntityMazinkyoukan.tick 设好
        float angle = entity.getYRot() - 57F;
        VertexConsumer consumer = bufferSource.getBuffer(RENDER_TYPE);
        int count = entity.getCount();

        float py = -0.3F;          // 垂直偏移
        float widthY = 0.3F;       // 主体半高

        // 辅助函数：计算每个节的位置
        float[] pos = new float[2];
        pos[0] = -Mth.sin(angle * Mth.DEG_TO_RAD) * 1.6F;
        pos[1] = Mth.cos(angle * Mth.DEG_TO_RAD) * 1.6F;
        float[] nextPos = new float[2];
        nextPos[0] = -Mth.sin((angle + 1) * Mth.DEG_TO_RAD) * 1.6F;
        nextPos[1] = Mth.cos((angle + 1) * Mth.DEG_TO_RAD) * 1.6F;

        // 取手参数（略微放大保证可见）
        float widthY2 = widthY + 0.15F;   // 增加厚度
        float radiusL = 1.6F;            // 左柄半径（原1.55，微调）
        float radiusR = 1.7F;            // 右柄半径（原1.65，微调）

        Matrix4f mat = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // 左取手（卷轴左端）
        float leftAngle = angle - 2;
        float pxLeftMin = -Mth.sin(leftAngle * Mth.DEG_TO_RAD) * radiusL;
        float pzLeftMin = Mth.cos(leftAngle * Mth.DEG_TO_RAD) * radiusL;
        float leftAnglePlus = angle + 2;
        float pxLeftMax = -Mth.sin(leftAnglePlus * Mth.DEG_TO_RAD) * radiusR;
        float pzLeftMax = Mth.cos(leftAnglePlus * Mth.DEG_TO_RAD) * radiusR;

        addQuad(consumer, mat, normal,
                pxLeftMin, py - widthY2, pzLeftMin,
                pxLeftMax, py - widthY2, pzLeftMax,
                pxLeftMax, py + widthY2, pzLeftMax,
                pxLeftMin, py + widthY2, pzLeftMin,
                0.875f, 1.0f, 1.0f,0.875f, 1.0f, 1.0f, 0.5f, 0.5f,  // UV对应四个顶点
                1,1,1,1, light);

        // 主体曲面
        float ticks = entity.tickCount + partialTick;
        float colorRev = Mth.DEG_TO_RAD * 3F;
        float angleStep = angle;
        float px = pos[0], pz = pos[1];
        float npx = nextPos[0], npz = nextPos[1];

        for (int i = 0; i < count; i++) {
            float colorVal = (angleStep + ticks) * colorRev;
            float r = Mth.sin(colorVal);
            float g = Mth.cos(colorVal);
            float b = -Mth.sin(colorVal);
            float uMin = (i % 63) / 64f;
            float uMax = ((i + 1) % 63) / 64f;
            float vMin = ((i / 63f) * 16f) / 32f;
            float vMax = vMin + 0.5f;

            addQuad(consumer, mat, normal,
                    px, py - widthY, pz,
                    npx, py - widthY, npz,
                    npx, py + widthY, npz,
                    px, py + widthY, pz,
                    uMin, uMax, uMax, uMin, vMax, vMax, vMin, vMin,
                    r, g, b, 0.5f, light);

            angleStep += 1;
            px = npx;
            pz = npz;
            npx = -Mth.sin((angleStep + 1) * Mth.DEG_TO_RAD) * 1.6F;
            npz = Mth.cos((angleStep + 1) * Mth.DEG_TO_RAD) * 1.6F;
        }

        // 右取手（卷轴右端）
        float rightAngle = angleStep - 2;
        float pxRightMin = -Mth.sin(rightAngle * Mth.DEG_TO_RAD) * radiusL;
        float pzRightMin = Mth.cos(rightAngle * Mth.DEG_TO_RAD) * radiusL;
        float rightAnglePlus = angleStep + 2;
        float pxRightMax = -Mth.sin(rightAnglePlus * Mth.DEG_TO_RAD) * radiusR;
        float pzRightMax = Mth.cos(rightAnglePlus * Mth.DEG_TO_RAD) * radiusR;

        addQuad(consumer, mat, normal,
                pxRightMin, py - widthY2, pzRightMin,
                pxRightMax, py - widthY2, pzRightMax,
                pxRightMax, py + widthY2, pzRightMax,
                pxRightMin, py + widthY2, pzRightMin,
                1.0f, 0.875f, 0.875f, 1.0f, 1.0f, 1.0f, 0.5f, 0.5f,
                1,1,1,1, light);

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, bufferSource, light);
    }

    // 辅助方法：添加四边形顶点
    private void addQuad(VertexConsumer consumer, Matrix4f mat, Matrix3f normal, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float u0, float u1, float u2, float u3, float v0, float v1, float v2, float v3, float r, float g, float b, float a, int light) {
        consumer.vertex(mat, x0, y0, z0).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(u3, v3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(DemonScriptureEntity entity) {
        return TEXTURE;
    }
}