/*
 * Copyright (c) 2021-2022 Lennox
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
import de.lennox.rainbowify.effect.Effect;
import de.lennox.rainbowify.gl.framebuffer.RefreshingWindowBuffer;
import de.lennox.rainbowify.mixin.interfaces.RainbowifyShader;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.io.IOException;

import static de.lennox.rainbowify.gl.GLUtil.drawCanvas;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

/**
 * The blur effect which adds a blur backdrop to the gui
 *
 * @author Lennox
 * @since 1.0.0
 */
public class BlurEffect extends Effect {
  private static final int[] POWERS_OF_TWO = new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
  private final RefreshingWindowBuffer[] buffers = new RefreshingWindowBuffer[6];
  private ShaderProgram down, up;
  private GlUniform downOffset, downInSize;
  private GlUniform upOffset, upInSize;

  @Override
  public void init() {
    // Create the shader instance
    try {
      down =
          new ShaderProgram(
              new RainbowifyResourceFactory(), "rainbowify:down", VertexFormats.POSITION_TEXTURE);
      up =
          new ShaderProgram(
              new RainbowifyResourceFactory(), "rainbowify:up", VertexFormats.POSITION_TEXTURE);
    } catch (IOException e) {
      System.err.println("Failed to create blur shader. Report this in the discord with the log!");
      e.printStackTrace();
    }
    // Create an auto refreshing frame buffer (auto-resize)
    for (int i = 0; i < buffers.length; i++) {
      RefreshingWindowBuffer buffer = buffers[i];
      if (buffer != null) {
        buffer.delete();
      }
      // Set the buffer
      buffers[i] = buffer = new RefreshingWindowBuffer(1, 1);
      buffer.setTexFilter(GL_LINEAR);
      buffer.beginRead();
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
      buffer.endRead();
    }
    RainbowifyShader downShader = (RainbowifyShader) down;
    RainbowifyShader upShader = (RainbowifyShader) up;
    // Create uniforms
    downInSize = downShader.customUniform("InSize");
    downOffset = downShader.customUniform("offset");
    upInSize = upShader.customUniform("InSize");
    upOffset = upShader.customUniform("offset");
  }

  @Override
  public void draw(MatrixStack stack) {
    boolean enabled = (boolean) RainbowifyMod.instance().optionRepository().optionOf("blur").value;
    if (!enabled) return;
    int iterations =
        (int) RainbowifyMod.instance().optionRepository().optionOf("blur_iterations").value;
    // Refresh all buffers
    for (int i = 0; i < buffers.length; i++) {
      int scale = POWERS_OF_TWO[i];
      buffers[i].check(MC.getWindow().getWidth() / scale, MC.getWindow().getHeight() / scale);
    }
    // Down-sample the main buffer
    for (int i = 0; i < iterations; i++) {
      RefreshingWindowBuffer framebuffer = buffers[i + 1];
      // Clear the buffer
      framebuffer.clear(false);
      framebuffer.beginWrite(true);
      updateDownUniforms(framebuffer);
      down.addSampler("DiffuseSampler", i == 0 ? MC.getFramebuffer() : buffers[i]);
      // Draw the canvas
      drawCanvas(MC.getFramebuffer(), stack, () -> down);
    }

    // Up-sample the buffer
    for (int i = iterations; i > 0; i--) {
      RefreshingWindowBuffer framebuffer = buffers[i - 1];
      // Clear the buffer
      if (i == 1) {
        MC.getFramebuffer().beginWrite(true);
      } else {
        framebuffer.clear(false);
        framebuffer.beginWrite(true);
      }
      updateUpUniforms(framebuffer);
      up.addSampler("DiffuseSampler", buffers[i]);
      // Draw the canvas
      drawCanvas(MC.getFramebuffer(), stack, () -> up);
    }
  }

  /**
   * Updates the down-sampling uniform
   *
   * @param framebuffer The framebuffer
   * @since 1.2.0
   */
  private void updateDownUniforms(Framebuffer framebuffer) {
    int blurAmount =
        (int) RainbowifyMod.instance().optionRepository().optionOf("blur_amount").value;
    // Set the uniforms
    downInSize.set((float) framebuffer.textureWidth, (float) framebuffer.textureHeight);
    downOffset.set(blurAmount * fade);
  }

  /**
   * Updates the up-sampling uniform
   *
   * @param framebuffer The framebuffer
   * @since 1.2.0
   */
  private void updateUpUniforms(Framebuffer framebuffer) {
    int blurAmount =
        (int) RainbowifyMod.instance().optionRepository().optionOf("blur_amount").value;
    // Set the uniforms
    upInSize.set((float) framebuffer.textureWidth, (float) framebuffer.textureHeight);
    upOffset.set(blurAmount * fade);
  }
}
