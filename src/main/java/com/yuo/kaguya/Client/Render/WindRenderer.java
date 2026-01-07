package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yuo.kaguya.Client.Model.WindChargeModel;
import com.yuo.kaguya.Entity.DanmakuColor;
import com.yuo.kaguya.Entity.WindEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class WindRenderer extends EntityRenderer<WindEntity> {
    private static final ResourceLocation TEXTURE = KaguyaUtils.fa("textures/entity/wind_charge.png");
    private final WindChargeModel model;

    public WindRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new WindChargeModel(context.bakeLayer(WindChargeModel.LAYER_LOCATION));
    }

    @Override
    public void render(WindEntity wind, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        DanmakuColor color = wind.getColor();
        float mul = (wind.tickCount + partialTicks) * 5.0f;
        poseStack.translate(0.0D, -1, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(mul));

        this.model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1,1,1,1f);
        poseStack.popPose();
        super.render(wind, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WindEntity entity) {
        return TEXTURE;
    }
}