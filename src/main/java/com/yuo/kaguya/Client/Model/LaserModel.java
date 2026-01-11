package com.yuo.kaguya.Client.Model;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yuo.kaguya.Entity.DanmakuLaser;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LaserModel extends EntityModel<DanmakuLaser> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(KaguyaUtils.fa("laser"), "main");
	private final ModelPart laser;

	public LaserModel(ModelPart root) {
		this.laser = root.getChild("laser");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition laser = partdefinition.addOrReplaceChild("laser", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition h_r1 = laser.addOrReplaceChild("h_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -16.0F, 0.0F, 4.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition w_r1 = laser.addOrReplaceChild("w_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -16.0F, 0.0F, 4.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -1.5708F, 0.0F, 1.5708F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(DanmakuLaser entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	public void setupAnim(float yRot, float xRot) {
		this.laser.yRot = yRot;
		this.laser.xRot = xRot;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		laser.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}