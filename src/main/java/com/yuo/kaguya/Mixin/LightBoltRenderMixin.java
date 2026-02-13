package com.yuo.kaguya.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.world.entity.LightningBolt;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LightningBoltRenderer.class)
public abstract class LightBoltRenderMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/LightningBolt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
    , at = @At("HEAD"), cancellable = true)
    public void render(LightningBolt bolt, float v, float v1, PoseStack poseStack, MultiBufferSource bufferSource, int i, CallbackInfo ci) {
        boolean equals = "kaguya:color_light_bolt".equals(bolt.getCustomName() == null ? "" : bolt.getCustomName().getString());
        if (equals){
            float[] lvt_7_1_ = new float[8];
            float[] lvt_8_1_ = new float[8];
            float lvt_9_1_ = 0.0F;
            float lvt_10_1_ = 0.0F;
            Random lvt_11_1_ = new Random(bolt.seed);

            for(int lvt_12_1_ = 7; lvt_12_1_ >= 0; --lvt_12_1_) {
                lvt_7_1_[lvt_12_1_] = lvt_9_1_;
                lvt_8_1_[lvt_12_1_] = lvt_10_1_;
                lvt_9_1_ += (float)(lvt_11_1_.nextInt(11) - 5);
                lvt_10_1_ += (float)(lvt_11_1_.nextInt(11) - 5);
            }

            VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());
            Matrix4f matrix4f = poseStack.last().pose();

            for(int k = 0; k < 4; ++k) {
                Random random = new Random(bolt.seed);

                for(int l = 0; l < 3; ++l) {
                    int lvt_16_1_ = 7;
                    int lvt_17_1_ = 0;
                    if (l > 0) {
                        lvt_16_1_ = 7 - l;
                    }

                    if (l > 0) {
                        lvt_17_1_ = lvt_16_1_ - 2;
                    }

                    float lvt_18_1_ = lvt_7_1_[lvt_16_1_] - lvt_9_1_;
                    float lvt_19_1_ = lvt_8_1_[lvt_16_1_] - lvt_10_1_;

                    for(int n = lvt_16_1_; n >= lvt_17_1_; --n) {
                        float lvt_21_1_ = lvt_18_1_;
                        float lvt_22_1_ = lvt_19_1_;
                        if (l == 0) {
                            lvt_18_1_ += (float)(random.nextInt(11) - 5);
                            lvt_19_1_ += (float)(random.nextInt(11) - 5);
                        } else {
                            lvt_18_1_ += (float)(random.nextInt(31) - 15);
                            lvt_19_1_ += (float)(random.nextInt(31) - 15);
                        }

                        float lvt_23_1_ = 0.5F;
                        float lvt_25_1_ = 0.45F;
                        float lvt_27_1_ = 0.1F + (float)k * 0.2F;
                        if (l == 0) {
                            lvt_27_1_ = (float)((double)lvt_27_1_ * ((double)n * 0.1 + 1.0));
                        }

                        float lvt_28_1_ = 0.1F + (float)k * 0.2F;
                        if (l == 0) {
                            lvt_28_1_ *= (float)(n - 1) * 0.1F + 1.0F;
                        }

                        kaguyaFD$quad(matrix4f, buffer, lvt_18_1_, lvt_19_1_, n, lvt_21_1_, lvt_22_1_, lvt_25_1_, lvt_25_1_, lvt_23_1_, lvt_27_1_, lvt_28_1_, false, false, true, false);
                        kaguyaFD$quad(matrix4f, buffer, lvt_18_1_, lvt_19_1_, n, lvt_21_1_, lvt_22_1_, lvt_25_1_, lvt_25_1_, lvt_23_1_, lvt_27_1_, lvt_28_1_, true, false, true, true);
                        kaguyaFD$quad(matrix4f, buffer, lvt_18_1_, lvt_19_1_, n, lvt_21_1_, lvt_22_1_, lvt_25_1_, lvt_25_1_, lvt_23_1_, lvt_27_1_, lvt_28_1_, true, true, false, true);
                        kaguyaFD$quad(matrix4f, buffer, lvt_18_1_, lvt_19_1_, n, lvt_21_1_, lvt_22_1_, lvt_25_1_, lvt_25_1_, lvt_23_1_, lvt_27_1_, lvt_28_1_, false, true, false, false);
                    }
                }
            }
            ci.cancel();
        }
    }

    @Unique
    private static void kaguyaFD$quad(Matrix4f matrix4f, VertexConsumer consumer, float v, float v1, int i, float v2, float v3, float v4, float v5, float v6, float v7, float v8, boolean b, boolean b2, boolean b3, boolean b4) {
        Random random = new Random();
        float r1 = random.nextFloat();
        float g1 = random.nextFloat();
        float b1 = random.nextFloat();
        consumer.vertex(matrix4f, v + (b ? v8 : -v8), (float)(i * 16), v1 + (b2 ? v8 : -v8)).color(r1, g1, b1, 1.0F).endVertex();
        consumer.vertex(matrix4f, v2 + (b ? v7 : -v7), (float)((i + 1) * 16), v3 + (b2 ? v7 : -v7)).color(r1, g1, b1, 1.0F).endVertex();
        consumer.vertex(matrix4f, v2 + (b3 ? v7 : -v7), (float)((i + 1) * 16), v3 + (b4 ? v7 : -v7)).color(r1, g1, b1, 1.0F).endVertex();
        consumer.vertex(matrix4f, v + (b3 ? v8 : -v8), (float)(i * 16), v1 + (b4 ? v8 : -v8)).color(r1, g1, b1, 1.0F).endVertex();
    }
}
