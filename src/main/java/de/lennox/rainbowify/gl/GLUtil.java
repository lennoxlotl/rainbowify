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
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.util.function.Supplier;

public class GLUtil {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static void drawCanvas(MatrixStack matrixStack, Supplier<Shader> shaderSupplier) {
        var width = (float) (MC.getFramebuffer().textureWidth / MC.getWindow().getScaleFactor());
        var height = (float) (MC.getFramebuffer().textureHeight / MC.getWindow().getScaleFactor());

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(shaderSupplier);

        var tessellator = Tessellator.getInstance();
        var bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        Matrix4f matrix = matrixStack.peek().getModel();
        bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 1).next();
        bufferBuilder.vertex(matrix, 0, height, 0).texture(0, 0).next();
        bufferBuilder.vertex(matrix, width, height, 0).texture(1, 0).next();
        bufferBuilder.vertex(matrix, width, 0, 0).texture(1, 1).next();
        tessellator.draw();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

}
