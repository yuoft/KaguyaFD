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
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Arrow;

public class ArrowShotModel extends EntityModel<DanmakuArrow> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(KaguyaUtils.fa("arrow_shot"), "main");
	private final ModelPart arrow_shot;

	public ArrowShotModel(ModelPart root) {
		this.arrow_shot = root.getChild("arrow_shot");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition arrow_shot = partdefinition.addOrReplaceChild("arrow_shot", CubeListBuilder.create().texOffs(-24, 8).addBox(-3.9762F, 0.0872F, -7.8427F, 8.0F, 0.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-0.5F, -4.0F, -5.0F, 1.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, -8.0F));

		PartDefinition topw_r1 = arrow_shot.addOrReplaceChild("topw_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.5F, -9.0F, 1.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.5F, 4.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition h_r1 = arrow_shot.addOrReplaceChild("h_r1", CubeListBuilder.create().texOffs(-24, 8).addBox(-3.0F, 0.0F, -6.0F, 8.0F, 0.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0238F, -0.9128F, -1.8427F, 0.0F, 0.0F, 1.5708F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(DanmakuArrow entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		arrow_shot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}