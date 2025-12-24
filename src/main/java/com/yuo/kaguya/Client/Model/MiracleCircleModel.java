package com.yuo.kaguya.Client.Model;// Made with Blockbench 5.0.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yuo.kaguya.Entity.MiracleCircle;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class MiracleCircleModel extends EntityModel<MiracleCircle> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(RlUtil.fa("miracle_circle"), "main");
	private final ModelPart circle;

	public MiracleCircleModel(ModelPart root) {
		this.circle = root.getChild("circle");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition circle = partdefinition.addOrReplaceChild("circle", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -32.0F, 0.0F, 4.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(MiracleCircle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		circle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}