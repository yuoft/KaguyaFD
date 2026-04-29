package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.Danmaku.DanmakuColor;
import com.yuo.kaguya.Entity.SukimaGapEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Random;

public class SukimaGapRender extends EntityRenderer<SukimaGapEntity> {

    private static final ResourceLocation TEXTURE = KaguyaUtils.fa("textures/entity/sukima_gap.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);

    public SukimaGapRender(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(SukimaGapEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // 上下漂浮动画
        float yOffset = Mth.sin((entity.tickCount + partialTicks) / 20F * Mth.PI) * 0.1F;
        poseStack.translate(0, 0.5F + yOffset, 0);

        // Y轴旋转面向玩家
        poseStack.mulPose(Axis.YP.rotationDegrees(180F - entityYaw));

        // 获取颜色枚举（用于飘带颜色）
        DanmakuColor danColor = entity.getColor();
        float r = danColor.getFloatRed();
        float g = danColor.getFloatGreen();
        float b = danColor.getFloatBlue();

        // ========== 随机张开动画（不依赖颜色） ==========
        // 使用实体的随机种子（基于UUID）使每个间隙动画不同步
        Random rand = new Random(entity.getUUID().getLeastSignificantBits());
        // 动画时间参数：周期约 80~200 刻随机，持续约 20~40 刻正弦脉冲
        int totalAlive = entity.tickCount;
        // 计算一个不规则波形：多个正弦波叠加，使其看起来随机触发
        double t1 = totalAlive * 0.03;           // 慢速波
        double t2 = totalAlive * 0.12;
        double noise = Math.sin(t1) * Math.sin(t2) * 0.8 + 0.5;
        // 让脉冲随机出现：只有 noise 大于某个动态阈值时才触发缩放
        double threshold = 0.7 + Math.sin(totalAlive * 0.023) * 0.2; // 阈值也在波动
        float openRate;
        if (noise > threshold) {
            // 缩放强度线性映射到 [0.2, 1.0]
            openRate = (float) ((noise - threshold) / (1.0 - threshold) * 0.8 + 0.2);
        } else {
            openRate = 0.0F;
        }
        // 可选：随机的最大缩放幅度（0.5~1.5）
        float scaleFactor = 0.8F + rand.nextFloat() * 0.7F;
        openRate *= scaleFactor;

        // 角点缩放（张开效果）
        float xLeft = 1.0F - openRate * 0.5F;      // 从1缩到0.5
        float yTop = -0.75F + openRate * 0.5F;     // 从-0.75升到-0.25
        float xRight = -1.0F + openRate * 0.5F;    // 从-1升到-0.5
        float yBottom = 0.75F - openRate * 0.5F;   // 从0.75降到0.25
        // 确保边界合理（防止翻转）
        xLeft = Math.max(0.2F, Math.min(1.0F, xLeft));
        yTop = Math.max(-1.0F, Math.min(-0.2F, yTop));
        xRight = Math.max(-1.0F, Math.min(-0.2F, xRight));
        yBottom = Math.max(0.2F, Math.min(1.0F, yBottom));

        VertexConsumer consumer = bufferSource.getBuffer(RENDER_TYPE);
        Matrix4f mat = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // 主体四边形（正面纹理）
        addQuad(consumer, mat, normal, -1.0F, -0.75F, 0.0F, xLeft, yTop, 0.0F, 1.0F, 0.75F, 0.0F, xRight, yBottom, 0.0F, 0.00F, 0.75F, 0.75F, 0.00F, 1.00F, 1.00F, 0.00F, 0.00F, 1.0F, 1.0F, 1.0F, 1.0F, packedLight);

        // 黑色半透明背层（同样缩放）
        addQuad(consumer, mat, normal, xLeft, yTop, -0.0009F, -1.0F, -0.75F, -0.0009F, xRight, yBottom, -0.0009F, 1.0F, 0.75F, -0.0009F, 0.75F, 0.00F, 0.00F, 0.75F, 1.00F, 1.00F, 0.00F, 0.00F, 0.0F, 0.0F, 0.0F, 0.7F, packedLight);

        // ----- 飘带渲染（不受缩放影响） -----
        // 右上飘带
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(30F));
        Matrix4f matRibbon = poseStack.last().pose();
        addQuad(consumer, matRibbon, normal, 1.00F, -0.2F, 0.001F, 1.25F, -0.2F, 0.001F, 1.25F, 0.3F, 0.001F, 1.00F, 0.3F, 0.001F, 0.75F, 1.00F, 1.00F, 0.75F, 1.00F, 1.00F, 0.00F, 0.00F, r, g, b, 0.7F, packedLight);
        addQuad(consumer, matRibbon, normal, 1.00F, 0.3F, -0.001F, 1.25F, 0.3F, -0.001F, 1.25F, -0.2F, -0.001F, 1.00F, -0.2F, -0.001F, 0.75F, 1.00F, 1.00F, 0.75F, 0.00F, 0.00F, 1.00F, 1.00F, 0.0F, 0.0F, 0.0F, 0.7F, packedLight);
        poseStack.popPose();

        // 左下飘带
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(30F));
        matRibbon = poseStack.last().pose();
        addQuad(consumer, matRibbon, normal, -1.30F, -0.3F, 0.001F, -1.05F, -0.3F, 0.001F, -1.05F, 0.2F, 0.001F, -1.30F, 0.2F, 0.001F, 0.75F, 1.00F, 1.00F, 0.75F, 1.00F, 1.00F, 0.00F, 0.00F, r, g, b, 0.7F, packedLight);
        addQuad(consumer, matRibbon, normal, -1.30F, 0.2F, -0.001F, -1.05F, 0.2F, -0.001F, -1.05F, -0.3F, -0.001F, -1.30F, -0.3F, -0.001F, 0.75F, 1.00F, 1.00F, 0.75F, 0.00F, 0.00F, 1.00F, 1.00F, 0.0F, 0.0F, 0.0F, 0.7F, packedLight);
        poseStack.popPose();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    // 辅助四边形绘制（保持不变）
    private void addQuad(VertexConsumer consumer, Matrix4f mat, Matrix3f normal, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float u0, float u1, float u2, float u3, float v0, float v1, float v2, float v3, float r, float g, float b, float a, int light) {
        consumer.vertex(mat, x0, y0, z0).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(u3, v3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(SukimaGapEntity entity) {
        return TEXTURE;
    }
}