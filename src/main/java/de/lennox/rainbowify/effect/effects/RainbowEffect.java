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
import de.lennox.rainbowify.mixin.interfaces.MinecraftShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.io.IOException;

import static de.lennox.rainbowify.gl.GLUtil.*;

public class RainbowEffect extends Effect {

    private Shader rainbowShader;

    private GlUniform alphaU;
    private GlUniform timeU;
    private GlUniform resolutionU;

    private long startTime;

    @Override
    public void init() {
        startTime = System.currentTimeMillis();
        try {
            rainbowShader = new Shader(new RainbowifyResourceFactory(), "rainbowify:rainbow", VertexFormats.POSITION_TEXTURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MinecraftShader minecraftShaderInterface = (MinecraftShader) rainbowShader;
        alphaU = minecraftShaderInterface.customUniform("alpha");
        timeU = minecraftShaderInterface.customUniform("time");
        resolutionU = minecraftShaderInterface.customUniform("res");
    }

    @Override
    public void draw(MatrixStack stack) {
        updateUniforms();
        drawCanvas(stack, () -> rainbowShader);
    }

    private void updateUniforms() {
        Config.RainbowSpeed rainbowSpeed = RainbowifyMod.instance().optionRepository().enumOption("rainbow_speed");
        alphaU.set(fade);
        timeU.set((float) (System.currentTimeMillis() - startTime) / rainbowSpeed.time());
        resolutionU.set(MC.getWindow().getScaledWidth(), MC.getWindow().getScaledHeight());
    }


}
