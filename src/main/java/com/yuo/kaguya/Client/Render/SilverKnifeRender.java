package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.SilverKnifeModel;
import com.yuo.kaguya.Entity.SilverKnifeEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SilverKnifeRender extends EntityRenderer<SilverKnifeEntity> {
    public static final ResourceLocation SILVER_KNIFE = KaguyaUtils.fa("textures/entity/silver_knife.png");
    private final SilverKnifeModel<SilverKnifeEntity> model;

    public SilverKnifeRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SilverKnifeModel<>(context.bakeLayer(SilverKnifeModel.LAYER_LOCATION));
    }

    @Override
    public void render(SilverKnifeEntity silverKnife, float u, float v, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(bufferSource, this.model.renderType(this.getTextureLocation(silverKnife)), false, silverKnife.isFoil());
        float[] colors = silverKnife.getColor().getFloatRgb();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(v, silverKnife.yRotO, silverKnife.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(v, silverKnife.xRotO, silverKnife.getXRot()) + 90.0F));
        this.model.renderToBuffer(poseStack, consumer, i, OverlayTexture.NO_OVERLAY, colors[0], colors[1], colors[2], 1.0F);
        poseStack.popPose();

        super.render(silverKnife, u, v, poseStack, bufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(SilverKnifeEntity silverKnife) {
        return SILVER_KNIFE;
    }
}
