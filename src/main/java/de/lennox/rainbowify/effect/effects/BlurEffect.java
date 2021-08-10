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
import de.lennox.rainbowify.mixin.interfaces.RainbowifyShader;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.io.IOException;

import static de.lennox.rainbowify.gl.GLUtil.drawCanvas;

public class BlurEffect extends Effect {

    private Shader blur;
    private GlUniform radius;
    private GlUniform direction;
    private GlUniform inSize;
    private Framebuffer framebuffer;

    @Override
    public void init() {
        // Create the shader instance
        try {
            blur = new Shader(new RainbowifyResourceFactory(), "rainbowify:blur", VertexFormats.POSITION_TEXTURE);
        } catch (IOException e) {
            System.err.println("Failed to create blur shader. Report this in the discord with the log!");
            e.printStackTrace();
        }
        // Create an auto refreshing frame buffer (auto-resize)
        framebuffer = new RefreshingWindowBuffer(MC.getWindow().getFramebufferWidth(), MC.getWindow().getFramebufferHeight());
        RainbowifyShader rainbowifyShaderInterface = (RainbowifyShader) blur;
        // Create uniforms
        radius = rainbowifyShaderInterface.customUniform("radius");
        direction = rainbowifyShaderInterface.customUniform("direction");
        inSize = rainbowifyShaderInterface.customUniform("InSize");
    }

    @Override
    public void draw(MatrixStack stack) {
        if (!Config.BLUR.value) return;
        // Draw the first pass
        framebuffer.beginWrite(false);
        blur.addSampler("DiffuseSampler", MC.getFramebuffer());
        updateUniforms(0);
        drawCanvas(stack, () -> blur);
        framebuffer.endWrite();
        // Draw the second pass
        MC.getFramebuffer().beginWrite(false);
        blur.addSampler("DiffuseSampler", framebuffer);
        updateUniforms(1);
        drawCanvas(stack, () -> blur);
    }

    private void updateUniforms(float pass) {
        Config.BlurAmount blurAmount = (Config.BlurAmount) RainbowifyMod.instance().optionRepository().optionBy("blur_amount").value;
        Config.RainbowOpacity rainbowOpacity = (Config.RainbowOpacity) RainbowifyMod.instance().optionRepository().optionBy("rainbow_opacity").value;
        // Set the uniforms
        radius.set(Math.max(blurAmount.radius() * (fade * (1 / rainbowOpacity.opacity())), 1));
        direction.set(pass, 1f - pass);
        inSize.set((float) (MC.getFramebuffer().textureWidth / MC.getWindow().getScaleFactor()),
            (float) (MC.getFramebuffer().textureHeight / MC.getWindow().getScaleFactor()));
    }

}
