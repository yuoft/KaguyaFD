package com.yuo.kaguya;

import net.minecraft.resources.ResourceLocation;

public class RlUtil {
    public static ResourceLocation fa(String path){
        return new ResourceLocation(Kaguya.MOD_ID, path);
    }

    public static ResourceLocation def(String path){
        return new ResourceLocation(path);
    }
}
