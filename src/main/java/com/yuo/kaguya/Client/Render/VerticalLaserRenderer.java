package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Entity.DanmakuLaser;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class VerticalLaserRenderer extends EntityRenderer<DanmakuLaser> {
    private static final ResourceLocation TEXTURE = KaguyaUtils.def("textures/entity/beacon_beam.png");

    // 定义模型层
    public static final ModelLayerLocation LASER_LAYER = new ModelLayerLocation(TEXTURE, "main");

    private final ModelPart laserCore;
    private final ModelPart laserGlow;

    public VerticalLaserRenderer(EntityRendererProvider.Context context) {
        super(context);

        // 使用 bakeLayer 获取模型部件
        ModelPart modelPart = context.bakeLayer(LASER_LAYER);
        this.laserCore = modelPart.getChild("core");
        this.laserGlow = modelPart.getChild("glow");
    }

    // 在 ModClientSetup 中注册这个模型定义
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 核心激光（细长圆柱）
        partdefinition.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 16.0F, 1.0F),  // 1x16x1 立方体
                PartPose.ZERO);

        // 光晕层
        partdefinition.addOrReplaceChild("glow", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F),  // 2x16x2 稍大的立方体
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void render(DanmakuLaser entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        // 检查实体是否可见
        if (!entity.isAlive()) return;

        poseStack.pushPose();

        // 直接移动到实体位置（简化版）
        double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

        poseStack.translate(x, y, z);

        // 应用旋转（关键修复）
        applyLaserRotation(poseStack, entity, partialTicks);

        // 根据速度调整激光长度
        float speed = (float) entity.getDeltaMovement().length();
        float length = Mth.clamp(speed * 10.0f, 1.0f, 20.0f);

        // 应用缩放 - Y轴是长度
        poseStack.scale(0.05f, length, 0.05f);

        // 渲染激光
        renderLaserModel(poseStack, buffer, packedLight, entity.tickCount + partialTicks, entity);

        poseStack.popPose();

        // 调用父类方法
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void applyLaserRotation(PoseStack poseStack, DanmakuLaser entity, float partialTicks) {
        // 获取方向向量
        Vec3 movement = entity.getDeltaMovement();

        // 如果速度太小，使用默认方向
        if (movement.lengthSqr() < 0.0001) {
            return;
        }

        Vec3 direction = movement.normalize();

        // 基础方向是垂直向上 (0, 1, 0)
        Vec3 up = new Vec3(0, 1, 0);

        // 检查是否方向相同或相反
        double dot = up.dot(direction);

        // 如果方向基本相同（垂直向上）
        if (dot > 0.9999) {
            return; // 不需要旋转
        }

        // 如果方向基本相反（垂直向下）
        if (dot < -0.9999) {
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            return;
        }

        // 计算旋转轴
        Vec3 axis = up.cross(direction);

        // 确保旋转轴不为零
        if (axis.lengthSqr() > 1.0E-7) {
            axis = axis.normalize();

            // 计算旋转角度
            float angle = (float) Math.acos(Mth.clamp(dot, -1.0F, 1.0F));

            // 创建并应用四元数
            Quaternionf quaternion = new Quaternionf().setAngleAxis(angle, (float) axis.x, (float) axis.y, (float) axis.z);
            poseStack.mulPose(quaternion);
        }
    }

    private void renderLaserModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, float age, DanmakuLaser entity) {

        // 脉动效果
        float pulse = (float) Math.sin(age * 0.3f) * 0.05f + 0.95f;

        // 渲染光晕（先渲染，在底部）
        poseStack.pushPose();
        poseStack.scale(pulse * 1.5f, 1.0f, pulse * 1.5f); // 光晕比核心大

        float lifeRatio = (float) entity.tickCount / (float) entity.getMAX_TICKS_EXISTED();
        float alpha = Mth.lerp(lifeRatio, 0.4f, 0.1f); // 随时间变透明

        // 光晕颜色（橙色）
        float rGlow = 1.0f;
        float gGlow = 0.6f;
        float bGlow = 0.2f;

        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE, true));
        laserGlow.render(poseStack, glowConsumer, 15728880, // 使用最大光照
                OverlayTexture.NO_OVERLAY, rGlow, gGlow, bGlow, alpha);
        poseStack.popPose();

        // 渲染核心激光
        poseStack.pushPose();
        poseStack.scale(pulse, 1.0f, pulse);

        // 核心颜色（红色）
        float rCore = 1.0f;
        float gCore = 0.2f;
        float bCore = 0.1f;
        float aCore = Mth.lerp(lifeRatio, 0.9f, 0.5f);

        VertexConsumer coreConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        laserCore.render(poseStack, coreConsumer, packedLight, OverlayTexture.NO_OVERLAY, rCore, gCore, bCore, aCore);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuLaser entity) {
        return TEXTURE;
    }
}