package com.yuo.kaguya.Client.Model;// Made with Blockbench 5.0.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yuo.kaguya.Entity.DanmakuButterfly;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ButterFlyModel extends EntityModel<DanmakuButterfly> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(KaguyaUtils.fa("butterfly"), "main");
	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public ButterFlyModel(ModelPart root) {
		this.leftWing = root.getChild("leftWing");
		this.rightWing = root.getChild("rightWing");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition leftWing = partdefinition.addOrReplaceChild("leftWing", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 16.0F));

		PartDefinition left_r1 = leftWing.addOrReplaceChild("left_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-25.0F, -40.0F, 0.0F, 32.0F, 64.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 0.0F, -8.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition rightWing = partdefinition.addOrReplaceChild("rightWing", CubeListBuilder.create(), PartPose.offset(1.0F, 24.0F, 8.0F));

		PartDefinition right_r1 = rightWing.addOrReplaceChild("right_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-31.0F, -40.0F, 0.0F, 32.0F, 64.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(30.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public void setupAnim(DanmakuButterfly entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float mul = 45f * Mth.sin(entity.tickCount / 22.5f * Mth.PI); //旋转角度
		leftWing.zRot = mul * Mth.DEG_TO_RAD;
		rightWing.zRot = -mul * Mth.DEG_TO_RAD;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		leftWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rightWing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}