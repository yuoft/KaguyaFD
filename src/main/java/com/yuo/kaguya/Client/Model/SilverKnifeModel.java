package com.yuo.kaguya.Client.Model;// Made with Blockbench 5.0.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yuo.kaguya.Entity.SilverKnifeEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SilverKnifeModel<T extends SilverKnifeEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(KaguyaUtils.fa("silver_knife"), "main");
	private final ModelPart body;

	public SilverKnifeModel(ModelPart root) {
		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 0).addBox(-4.0F, 5.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 3).addBox(1.0F, 5.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 6).addBox(-1.0F, -4.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(SilverKnifeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}