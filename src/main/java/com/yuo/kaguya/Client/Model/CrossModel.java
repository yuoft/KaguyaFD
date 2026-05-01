package com.yuo.kaguya.Client.Model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yuo.kaguya.Entity.CrossEntity;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class CrossModel extends EntityModel<CrossEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(KaguyaUtils.fa("cross"), "main");
    private final ModelPart sword;

    public CrossModel(ModelPart root) {
        this.sword = root.getChild("sword");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition sword = partdefinition.addOrReplaceChild("sword", CubeListBuilder.create().texOffs(1, 5).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 20.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 1).addBox(-5.0F, 2.0F, 0.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 23.0F, -7.0F, 1.5708F, 0.0F, 3.1416F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(CrossEntity cross, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        sword.yRot = -(float) Math.toRadians(cross.getYRot());
        sword.xRot = (float) Math.toRadians(cross.getXRot() + 90);
        sword.zRot = (float) Math.toRadians(cross.getSyncAngle());
        sword.y =  -5;
        sword.z =  -5;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        sword.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}