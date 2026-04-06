package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.LaserModel;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.DanmakuLaser;
import com.yuo.kaguya.Entity.DanmakuType;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class LaserRenderer extends EntityRenderer<DanmakuLaser> {
    private static final ResourceLocation LASER = KaguyaUtils.fa("textures/entity/laser.png");
    private final LaserModel laserModel;

    public LaserRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.laserModel = new LaserModel(context.bakeLayer(LaserModel.LAYER_LOCATION));
    }

    @Override
    public void render(DanmakuLaser laser, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer buffer = bufferSource.getBuffer(this.laserModel.renderType(getTextureLocation(laser)));
        DanmakuColor color = laser.getColor();
        DanmakuType danmakuType = laser.getDanmakuType();
        float size = 0.5f * danmakuType.getIntSize();

        poseStack.pushPose();
        Vec3 vec3 = laser.getDeltaMovement();
        float yaw = (float) (-Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG);
        float pitch = (float) (-Mth.atan2(vec3.y, vec3.horizontalDistance()) * Mth.RAD_TO_DEG);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.scale(0.5f, 0.5f, size);
        poseStack.translate(0, -1.0f, -0.15f * danmakuType.getIntSize()); //左右 上下 前后

        this.laserModel.renderToBuffer(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY, color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 0.75f);
        poseStack.popPose();

        super.render(laser, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuLaser laser) {
        return LASER;
    }

}