package com.yuo.kaguya.Event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.yuo.kaguya.KaguyaUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Mod.EventBusSubscriber(modid = "kaguya", value = Dist.CLIENT)
public class OutLineEvent {
    private static final Map<BlockPos, BlockHighlightInfo> highlightedBlocks = new HashMap<>();

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        // 清理过期的高亮
        cleanupExpired();

        if (!highlightedBlocks.isEmpty()) {
            // 使用 RenderType.lines() 直接渲染
            renderHighlightedBlocks(event.getPoseStack(), event.getCamera());
        }
    }

    /**
     * 添加方块高亮
     * @param pos 方块坐标
     * @param color 高亮框颜色
     * @param scale 大小
     * @param duration 渲染时间 以毫秒计 1000ms = 1s
     */
    public static void addHighlightBlock(BlockPos pos, int color, float scale, long duration) {
        highlightedBlocks.put(pos, new BlockHighlightInfo(color, scale, duration));
    }

    // 移除高亮
    public static void removeHighlight(BlockPos pos) {
        highlightedBlocks.remove(pos);
    }

    // 清除所有高亮
    public static void clearAll() {
        highlightedBlocks.clear();
    }

    // 使用 Iterator 安全地删除元素
    private static void cleanupExpired() {
        Iterator<Entry<BlockPos, BlockHighlightInfo>> iterator = highlightedBlocks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, BlockHighlightInfo> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    private static void renderHighlightedBlocks(PoseStack poseStack, Camera camera) {
        RenderSystem.depthMask(false); // 关键：禁用深度写入
        RenderSystem.disableCull();    // 禁用面剔除
        RenderSystem.enableBlend();    // 启用混合
        RenderSystem.defaultBlendFunc(); // 设置默认混合函数

        // 设置深度测试函数为 ALWAYS（总是通过）
        GL11.glDepthFunc(GL11.GL_ALWAYS);

        // 设置线宽
        RenderSystem.lineWidth(6.0f);

        // 启用抗锯齿线条
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        try {
            Vec3 cameraPos = camera.getPosition();
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            // 使用 RenderType.lines() 渲染线条
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

            for (Map.Entry<BlockPos, BlockHighlightInfo> entry : highlightedBlocks.entrySet()) {
                BlockPos pos = entry.getKey();
                BlockHighlightInfo info = entry.getValue();

                // 计算缩放后的AABB
                // 稍微扩大一点，确保轮廓在方块表面
                AABB aabb = new AABB(pos).deflate(-0.002);

                // 将AABB稍微向相机方向移动（大约0.001个单位）
                // 这样可以确保轮廓显示在方块前面
                Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                Vec3 toCamera = cameraPos.subtract(blockCenter).normalize();
                double offset = 0.0015; // 稍微大一点的偏移
                aabb = aabb.move(toCamera.scale(-offset));

                // 提取颜色分量并转换为0-1范围
                float r = ((info.color >> 16) & 0xFF) / 255.0f;
                float g = ((info.color >> 8) & 0xFF) / 255.0f;
                float b = (info.color & 0xFF) / 255.0f;
                float alpha = 1.0f;

                // 渲染方块轮廓
                renderAABB(poseStack, vertexConsumer, aabb, r, g, b, alpha);
            }

            bufferSource.endBatch(RenderType.lines());
            poseStack.popPose();

        } finally {
            // 恢复 GL 状态
            RenderSystem.lineWidth(1.0f);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDepthFunc(GL11.GL_LEQUAL); // 恢复默认深度函数

            // 恢复原始渲染状态
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }


    private static void renderAABB(PoseStack poseStack, VertexConsumer vertexConsumer, AABB aabb, float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float minX = (float) aabb.minX;
        float minY = (float) aabb.minY;
        float minZ = (float) aabb.minZ;
        float maxX = (float) aabb.maxX;
        float maxY = (float) aabb.maxY;
        float maxZ = (float) aabb.maxZ;

        // 为每条边单独设置颜色和法线
        // 底部矩形
        addLine(poseMatrix, normalMatrix, vertexConsumer, minX, minY, minZ, maxX, minY, minZ, r, g, b, a, 0, -1, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a, 0, -1, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a, 0, -1, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, minX, minY, maxZ, minX, minY, minZ, r, g, b, a, 0, -1, 0);

        // 顶部矩形
        addLine(poseMatrix, normalMatrix, vertexConsumer, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a, 0, 1, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a, 0, 1, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a, 0, 1, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a, 0, 1, 0);

        // 垂直线
        addLine(poseMatrix, normalMatrix, vertexConsumer, minX, minY, minZ, minX, maxY, minZ, r, g, b, a, -1, 0, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a, 1, 0, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a, 1, 0, 0);
        addLine(poseMatrix, normalMatrix, vertexConsumer, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a, -1, 0, 0);
    }

    private static void addLine(Matrix4f poseMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a, float nx, float ny, float nz) {
        // 添加第一个顶点
        vertexConsumer.vertex(poseMatrix, x1, y1, z1).color(r, g, b, a).normal(normalMatrix, nx, ny, nz).endVertex();

        // 添加第二个顶点
        vertexConsumer.vertex(poseMatrix, x2, y2, z2).color(r, g, b, a).normal(normalMatrix, nx, ny, nz).endVertex();
    }

    public static class BlockHighlightInfo {
        public final int color;
        public final float scale;
        public final long endTime;

        public BlockHighlightInfo(int color, float scale, long duration) {
            this.color = color;
            this.scale = scale;
            this.endTime = System.currentTimeMillis() + duration;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > endTime;
        }
    }
}