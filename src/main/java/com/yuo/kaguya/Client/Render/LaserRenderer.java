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
        poseStack.scale(1, size,1);
        poseStack.translate(0, -1.25, 0);
        float yaw = Mth.rotLerp(partialTicks, laser.yRotO, laser.getYRot()) * (Mth.PI / 180f);
        float pitch = Mth.lerp(partialTicks, laser.xRotO, laser.getXRot()) * (Mth.PI / 180f);
        this.laserModel.setupAnim(pitch, yaw);
        this.laserModel.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 0.75f);
        poseStack.popPose();

        super.render(laser, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DanmakuLaser laser) {
        return LASER;
    }

}