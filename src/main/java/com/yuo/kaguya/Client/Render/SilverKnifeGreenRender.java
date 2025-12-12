package com.yuo.kaguya.Client.Render;

import com.yuo.kaguya.Entity.SilverKnife;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SilverKnifeGreenRender extends SilverKnifeWhiteRender {
    public static final ResourceLocation SILVER_KNIFE = RlUtil.fa("textures/entity/silver_knife_green.png");

    public SilverKnifeGreenRender(EntityRendererProvider.Context context, ModelLayerLocation layer) {
        super(context, layer);
    }

    @Override
    public ResourceLocation getTextureLocation(SilverKnife silverKnife) {
       return SILVER_KNIFE;
    }
}
