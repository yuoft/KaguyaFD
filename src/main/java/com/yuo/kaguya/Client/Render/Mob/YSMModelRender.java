package com.yuo.kaguya.Client.Render.Mob;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yuo.kaguya.Entity.Mob.KFDBaseMob;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class YSMModelRender extends MobRenderer<KFDBaseMob, BedrockModel<KFDBaseMob>> {
    private static final ResourceLocation DEFAULT_TEXTURE = KaguyaUtils.fa("touhou_little_maid", "textures/entity/empty.png");

    private final EntityMaidRenderer maidRenderer;

    public YSMModelRender(Context context) {
        super(context, new BedrockModel<>(), 0.5F);
        this.maidRenderer = new EntityMaidRenderer(context);
        this.addLayer(new YSMHeldItemLayer(this, context.getItemInHandRenderer()));
    }


    @Override
    public void render(KFDBaseMob mob, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        maidRenderer.render(mob, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(KFDBaseMob maid) {
        return DEFAULT_TEXTURE;
    }

}