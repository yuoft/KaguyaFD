package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ModRenderType extends RenderType {
    public ModRenderType(String s, VertexFormat vertexFormat, Mode mode, int i, boolean b, boolean b1, Runnable runnable, Runnable runnable1) {
        super(s, vertexFormat, mode, i, b, b1, runnable, runnable1);
    }

    public static final Function<ResourceLocation, RenderType> HEART_CIRCLE = Util.memoize(res -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(res, false, false))
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderType.NO_CULL)
                .createCompositeState(true);
        return create("kaguya_heart_circle", DefaultVertexFormat.POSITION_COLOR_TEX, Mode.QUADS, 256, true, false, state);
    });
}
