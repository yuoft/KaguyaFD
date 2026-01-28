package com.yuo.kaguya.Item;

import com.yuo.kaguya.Kaguya;

/**
 * 可以在弹幕工作台染色物品
 */
public interface ModColorCraftItem {
    default String getModColorName() {
        return Kaguya.MOD_ID;
    }
}
