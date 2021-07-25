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
package de.lennox.rainbowify.effect.effects;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.RainbowifyResourceFactory;
import de.lennox.rainbowify.config.Config;
import de.lennox.rainbowify.effect.Effect;
import de.lennox.rainbowify.gl.framebuffer.RefreshingWindowBuffer;
import de.lennox.rainbowify.mixin.interfaces.MinecraftShader;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.io.IOException;

import static de.lennox.rainbowify.gl.GLUtil.drawCanvas;

public class BlurEffect extends Effect {

    private Shader blurShader;

    private GlUniform radius;
    private GlUniform direction;
    private GlUniform inSize;

    private Framebuffer framebuffer;

    @Override
    public void init() {
        try {
            blurShader = new Shader(new RainbowifyResourceFactory(), "rainbowify:blur", VertexFormats.POSITION_TEXTURE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        framebuffer = new RefreshingWindowBuffer(MC.getWindow().getFramebufferWidth(), MC.getWindow().getFramebufferHeight());

        blurShader.addSampler("DiffuseSampler", MC.getFramebuffer());
        blurShader.addSampler("DiffuseSampler2", framebuffer);

        MinecraftShader minecraftShaderInterface = (MinecraftShader) blurShader;
        radius = minecraftShaderInterface.customUniform("radius");
        direction = minecraftShaderInterface.customUniform("direction");
        inSize = minecraftShaderInterface.customUniform("InSize");
    }

    @Override
    public void draw(MatrixStack stack) {
        if (!Config.BLUR.value) return;
        MC.getFramebuffer().endWrite();
        framebuffer.beginWrite(false);

        updateUniforms(0);
        drawCanvas(stack, () -> blurShader);

        framebuffer.endWrite();

        MC.getFramebuffer().beginWrite(false);

        updateUniforms(1);
        drawCanvas(stack, () -> blurShader);
    }

    private void updateUniforms(float pass) {
        var width = MC.getFramebuffer().textureWidth / MC.getWindow().getScaleFactor();
        var height = MC.getFramebuffer().textureHeight / MC.getWindow().getScaleFactor();

        Config.BlurAmount blurAmount = RainbowifyMod.instance().optionRepository().enumOption("blur_amount");
        radius.set(Math.max(blurAmount.radius() * (fade * 2), 1));
        direction.set(pass, 1f - pass);
        inSize.set((float) width, (float) height);
    }

}
