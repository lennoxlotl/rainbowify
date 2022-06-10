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
import de.lennox.rainbowify.config.Config;
import de.lennox.rainbowify.effect.Effect;
import de.lennox.rainbowify.mixin.interfaces.RainbowifyShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.io.IOException;

import static de.lennox.rainbowify.gl.GLUtil.drawCanvas;

public class RainbowEffect extends Effect {
  private Shader rainbow;
  private GlUniform alpha, time, res;
  private long startTime;

  @Override
  public void init() {
    // Create the shader instance
    try {
      rainbow =
          new Shader(
              new RainbowifyResourceFactory(),
              "rainbowify:rainbow",
              VertexFormats.POSITION_TEXTURE);
    } catch (IOException e) {
      System.err.println(
          "Failed to create rainbow shader. Report this in the discord with the log!");
      e.printStackTrace();
    }
    RainbowifyShader rainbowifyShaderInterface = (RainbowifyShader) rainbow;
    // Create uniforms
    alpha = rainbowifyShaderInterface.customUniform("alpha");
    time = rainbowifyShaderInterface.customUniform("time");
    res = rainbowifyShaderInterface.customUniform("res");
    // Update start time
    startTime = System.currentTimeMillis();
  }

  @Override
  public void draw(MatrixStack stack) {
    boolean enabled =
        (boolean) RainbowifyMod.instance().optionRepository().optionOf("rainbow").value;
    if (!enabled) return;
    // Draw the rainbow
    updateUniforms();
    drawCanvas(MC.getFramebuffer(), stack, () -> rainbow);
  }

  private void updateUniforms() {
    Config.RainbowSpeed rainbowSpeed =
        (Config.RainbowSpeed)
            RainbowifyMod.instance().optionRepository().optionOf("rainbow_speed").value;
    Config.RainbowOpacity rainbowOpacity =
        (Config.RainbowOpacity)
            RainbowifyMod.instance().optionRepository().optionOf("rainbow_opacity").value;
    // Set the uniforms
    alpha.set(fade * rainbowOpacity.opacity());
    time.set((float) (System.currentTimeMillis() - startTime) / rainbowSpeed.time());
    res.set(MC.getWindow().getScaledWidth(), MC.getWindow().getScaledHeight());
  }
}
