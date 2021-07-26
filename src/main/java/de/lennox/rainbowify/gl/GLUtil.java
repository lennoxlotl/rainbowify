/*
 * Copyright (c) 2021 Lennox
 *
 * This file is part of rainbowify.
 *
 * rainbowify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rainbowify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with rainbowify.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.lennox.rainbowify.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.util.function.Supplier;

public class GLUtil {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static void drawCanvas(MatrixStack matrixStack, Supplier<Shader> shader) {
        var width = (float) (MC.getFramebuffer().textureWidth / MC.getWindow().getScaleFactor());
        var height = (float) (MC.getFramebuffer().textureHeight / MC.getWindow().getScaleFactor());
        // Set GL caps
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // Set the shader
        RenderSystem.setShader(shader);
        var tessellator = Tessellator.getInstance();
        var bufferBuilder = tessellator.getBuffer();
        // Draw the canvas
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        Matrix4f matrix = matrixStack.peek().getModel();
        bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 1).next();
        bufferBuilder.vertex(matrix, 0, height, 0).texture(0, 0).next();
        bufferBuilder.vertex(matrix, width, height, 0).texture(1, 0).next();
        bufferBuilder.vertex(matrix, width, 0, 0).texture(1, 1).next();
        tessellator.draw();
        // Reset GL caps
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd, 0);
    }

    private static void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float f = (float) (colorStart >> 24 & 255) / 255.0F;
        float g = (float) (colorStart >> 16 & 255) / 255.0F;
        float h = (float) (colorStart >> 8 & 255) / 255.0F;
        float i = (float) (colorStart & 255) / 255.0F;
        float j = (float) (colorEnd >> 24 & 255) / 255.0F;
        float k = (float) (colorEnd >> 16 & 255) / 255.0F;
        float l = (float) (colorEnd >> 8 & 255) / 255.0F;
        float m = (float) (colorEnd & 255) / 255.0F;
        bufferBuilder.vertex(matrix, (float) endX, (float) startY, (float) z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float) startX, (float) startY, (float) z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float) startX, (float) endY, (float) z).color(k, l, m, j).next();
        bufferBuilder.vertex(matrix, (float) endX, (float) endY, (float) z).color(k, l, m, j).next();
    }

}
