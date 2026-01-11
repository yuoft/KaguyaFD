package com.yuo.kaguya.Client.Model;// Made with Blockbench 5.0.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yuo.kaguya.Entity.DanmakuArrow;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ArrowShotModel extends EntityModel<DanmakuArrow> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(KaguyaUtils.fa("arrow_shot"), "main");
	private final ModelPart arrow_shot;

	public ArrowShotModel(ModelPart root) {
		this.arrow_shot = root.getChild("arrow_shot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition arrow_shot = partdefinition.addOrReplaceChild("arrow_shot", CubeListBuilder.create().texOffs(0, 8).addBox(-4.0F, -24.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 16).addBox(-4.0F, -16.0F, 0.0F, 8.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, -12.0F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}


	@Override
	public void setupAnim(DanmakuArrow entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	public void setupAnim(float yRot, float xRot) {
		this.arrow_shot.yRot = yRot;
		this.arrow_shot.xRot = xRot;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		arrow_shot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}