package com.yuo.kaguya.Item;

import com.yuo.kaguya.Kaguya;

/**
 * 不会进行数据生成模型的物品
 */
public interface ModNoDataGen {
    default String getModName() {
        return Kaguya.MOD_ID;
    }
}
