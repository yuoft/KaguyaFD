package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.GapModel;
import com.yuo.kaguya.Entity.GapEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class GapRender extends EntityRenderer<GapEntity> {
    private static final ResourceLocation GAP = KaguyaUtils.fa("textures/entity/gap.png");
    private final GapModel gapModel;

    public GapRender(Context context) {
        super(context);
        this.gapModel = new GapModel(context.bakeLayer(GapModel.LAYER_LOCATION));
    }

    @Override
    public void render(GapEntity gap, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer buffer = bufferSource.getBuffer(this.gapModel.renderType(getTextureLocation(gap)));
        float[] color = gap.getColor().getFloatRgb();
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        Direction direction = gap.getDirection();
        if (direction == Direction.EAST || direction == Direction.WEST) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }
        this.gapModel.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 0.8f);
        poseStack.popPose();

        super.render(gap, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GapEntity entity) {
        return GAP;
    }
}
