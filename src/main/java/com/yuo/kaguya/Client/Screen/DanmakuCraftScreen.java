package com.yuo.kaguya.Client.Screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yuo.kaguya.Menu.DanmakuCraftMenu;
import com.yuo.kaguya.RlUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DanmakuCraftScreen extends AbstractContainerScreen<DanmakuCraftMenu> {
    public static final ResourceLocation RES = RlUtil.fa("textures/gui/danmaku_craft.png");
    protected final int textureWidth = 256;
    protected final int textureHeight = 164;

    public DanmakuCraftScreen(DanmakuCraftMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = textureWidth;
        this.imageHeight = textureHeight;
        this.inventoryLabelY = this.imageHeight - 82;
        this.titleLabelX = this.imageWidth - 52;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, RES);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RES, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
