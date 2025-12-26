package com.yuo.kaguya.Client.Model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MarisaHatModel extends HumanoidModel<LivingEntity> {
    private final ModelPart hatBase;
    private final ModelPart hatBase2;
    private final ModelPart hatBase3;
    private final ModelPart hatBase4;
    private final ModelPart brim;
    private final ModelPart ribbonRight;
    private final ModelPart ribbonLeft;
    
    public MarisaHatModel(ModelPart root) {
        super(root);
        
        // 从 root 中获取各个部分
        ModelPart head = root.getChild("head");
        this.hatBase = head.getChild("hatBase");
        this.hatBase2 = hatBase.getChild("hatBase2");
        this.hatBase3 = hatBase.getChild("hatBase3");
        this.hatBase4 = hatBase.getChild("hatBase4");
        this.brim = hatBase.getChild("brim");
        this.ribbonRight = hatBase.getChild("ribbonRight");
        this.ribbonLeft = hatBase.getChild("ribbonLeft");
    }

    // 创建 LayerDefinition
    public static MeshDefinition createLayer(CubeDeformation cubeDeformation, float v , boolean isLegs) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 头部定义
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // 构建帽子
        PartDefinition hatBase = head.addOrReplaceChild("hatBase", 
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8, 4, 8, cubeDeformation),
            PartPose.ZERO);
        
        hatBase.addOrReplaceChild("hatBase2",
            CubeListBuilder.create()
                .texOffs(32, 2)
                .addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6, cubeDeformation),
            PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.2792527F, 0.0F, 0.0F));
        
        hatBase.addOrReplaceChild("hatBase3",
            CubeListBuilder.create()
                .texOffs(48, 16)
                .addBox(-2.0F, -3.0F, -2.0F, 4, 3, 4, cubeDeformation),
            PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, -0.5410521F, 0.0F, 0.0F));
        
        hatBase.addOrReplaceChild("hatBase4",
            CubeListBuilder.create()
                .texOffs(48, 24)
                .addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2, cubeDeformation),
            PartPose.offsetAndRotation(0.0F, -11.0F, 1.0F, -0.8203047F, 0.0F, 0.0F));
        
        hatBase.addOrReplaceChild("brim",
            CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-6.0F, 0.0F, -6.0F, 12, 1, 12, cubeDeformation),
            PartPose.offset(0.0F, -6.0F, 0.0F));
        
        hatBase.addOrReplaceChild("ribbonRight",
            CubeListBuilder.create()
                .texOffs(44, 12)
                .addBox(0.0F, -2.0F, -1.0F, 4, 3, 1, cubeDeformation),
            PartPose.offsetAndRotation(0.5F, -6.0F, -4.0F, 0.0F, 0.0F, -0.3490659F));
        
        hatBase.addOrReplaceChild("ribbonLeft",
            CubeListBuilder.create()
                .texOffs(32, 12)
                .addBox(-4.0F, -2.0F, -1.0F, 4, 3, 1, cubeDeformation),
            PartPose.offsetAndRotation(-0.5F, -6.0F, -4.0F, 0.0F, 0.0F, 0.3490659F));
        
        return meshdefinition;
    }
    
    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}