package com.yuo.kaguya.Client.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuo.kaguya.Item.Weapon.WindStick;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class PlayerCircleRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEXTURE = KaguyaUtils.fa("textures/entity/heart_circle.png");
    private final PlayerRenderer render;

    public PlayerCircleRenderer(PlayerRenderer render) {
        super(render);
        this.render = render;
    }

    @Override
    public void render(@NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, @NotNull AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.isUsingItem() && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof WindStick) {
            matrix.pushPose();
            render.getModel().jacket.translateAndRotate(matrix);
            float hUp = Math.min(KaguyaUtils.getChargeRatio(72000, player.getUseItemRemainingTicks(), 160), 1.0f) * 0.75f;
            double yShift = -0.498;
            if (player.isCrouching()) {
                matrix.mulPose(Axis.XP.rotationDegrees(-28.64789F));
                yShift = -0.44;
            }
            matrix.mulPose(Axis.ZP.rotationDegrees(180));
            matrix.scale(3, 3, 3);
            matrix.translate(-0.5, yShift + hUp, -0.5);
            VertexConsumer builder = renderer.getBuffer(ModRenderType.HEART_CIRCLE.apply(TEXTURE));
            Matrix4f matrix4f = matrix.last().pose();
            builder.vertex(matrix4f, 0, 0, 0).color(0, 255, 0, 255).uv(0, 0).endVertex();
            builder.vertex(matrix4f, 0, 0, 1).color(0, 255, 0, 255).uv(0, 1).endVertex();
            builder.vertex(matrix4f, 1, 0, 1).color(0, 255, 0, 255).uv(1, 1).endVertex();
            builder.vertex(matrix4f, 1, 0, 0).color(0, 255, 0, 255).uv(1, 0).endVertex();
            matrix.popPose();
        }
    }
}