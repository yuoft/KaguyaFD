package com.yuo.kaguya.Client.Model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class SuwakoHatModel extends HumanoidModel<LivingEntity> {
    private final ModelPart hatBase;
    private final ModelPart eyeright;
    private final ModelPart eyeleft;
    private final ModelPart brim;
    
    public SuwakoHatModel(ModelPart root) {
        super(root);
        
        // 从 root 中获取各个部分
        ModelPart head = root.getChild("head");
        this.hatBase = head.getChild("hatBase");
        this.eyeright = hatBase.getChild("eyeright");
        this.eyeleft = hatBase.getChild("eyeleft");
        this.brim = hatBase.getChild("brim");
    }
    
    // 创建 LayerDefinition
    public static MeshDefinition createLayer(CubeDeformation cubeDeformation, float v , boolean isLegs) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        // 头部定义
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        
        // 构建帽子
        PartDefinition hatBase = head.addOrReplaceChild("hatBase", 
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -9.0F, -4.0F, 8, 4, 8, cubeDeformation),
            PartPose.ZERO);
        
        hatBase.addOrReplaceChild("eyeright",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, cubeDeformation),
            PartPose.offset(-4.0F, -9.0F, -4.0F));
        
        hatBase.addOrReplaceChild("eyeleft",
            CubeListBuilder.create()
                .texOffs(24, 0)
                .addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, cubeDeformation),
            PartPose.offset(4.0F, -9.0F, -4.0F));
        
        hatBase.addOrReplaceChild("brim",
            CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-6.0F, -1.0F, -6.0F, 12, 1, 12, cubeDeformation),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        return meshdefinition;
    }
    
    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}