package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.SilverKnifeModel;
import com.yuo.kaguya.Entity.SilverKnife;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SilverKnifeWhiteRender extends EntityRenderer<SilverKnife> {
    public static final ResourceLocation SILVER_KNIFE_WHITE = RlUtil.fa("textures/entity/silver_knife_white.png");
    private final SilverKnifeModel<SilverKnife> model;

    public SilverKnifeWhiteRender(EntityRendererProvider.Context context, ModelLayerLocation layer) {
        super(context);
        this.model = new SilverKnifeModel<>(context.bakeLayer(layer));
    }

    @Override
    public void render(SilverKnife silverKnife, float u, float v, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(v, silverKnife.yRotO, silverKnife.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(v, silverKnife.xRotO, silverKnife.getXRot()) + 90.0F));
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(bufferSource, this.model.renderType(this.getTextureLocation(silverKnife)), false, silverKnife.isFoil());
        this.model.renderToBuffer(poseStack, consumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(silverKnife, u, v, poseStack, bufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(SilverKnife silverKnife) {
        return SILVER_KNIFE_WHITE;
    }
}
