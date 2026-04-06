package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.CrossModel;
import com.yuo.kaguya.Entity.CrossEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CrossRender extends EntityRenderer<CrossEntity> {
    private static final ResourceLocation CROSS = KaguyaUtils.fa("textures/entity/cross.png");
    private final CrossModel crossModel;

    public CrossRender(Context context) {
        super(context);
        this.crossModel = new CrossModel(context.bakeLayer(CrossModel.LAYER_LOCATION));
    }

    @Override
    public void render(CrossEntity cross, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0,1.0f,0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f));

        int rotationTypeNum = cross.getRotationTypeNum();
        if (rotationTypeNum == 1) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.translate(0,-0.9f,-0.9f);
        }

        VertexConsumer buffer = bufferSource.getBuffer(this.crossModel.renderType(this.getTextureLocation(cross)));
        this.crossModel.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

        poseStack.popPose();

        super.render(cross, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrossEntity cross) {
        return CROSS;
    }
}
