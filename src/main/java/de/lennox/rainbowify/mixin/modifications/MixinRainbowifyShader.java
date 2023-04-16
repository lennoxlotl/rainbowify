/*
 * Copyright (c) 2021-2023 Lennox
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
package de.lennox.rainbowify.mixin.modifications;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import de.lennox.rainbowify.mixin.interfaces.RainbowifyShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@SuppressWarnings("unused")
@Mixin(ShaderProgram.class)
public class MixinRainbowifyShader implements RainbowifyShader {
  private final Map<String, GlUniform> customUniforms = Maps.newHashMap();

  @Shadow
  @Final
  private String name;

  @ModifyArg(
      method = "loadShader",
      at =
      @At(
          value = "INVOKE",
          target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"),
      index = 0)
  private static String renameLoadProgram(String toReplace) {
    // Check if the program is a rainbowify program, if yes do some hacky stuff to fix a minecraft
    // moment
    if (toReplace.contains("rainbowify:")) {
      return "rainbowify:" + toReplace.replace("rainbowify:", "");
    }
    return toReplace;
  }

  @ModifyArg(
      method = "<init>",
      at =
      @At(
          value = "INVOKE",
          target = "net/minecraft/util/Identifier.<init>(Ljava/lang/String;)V"),
      index = 0)
  public String renameInit(String toReplace) {
    // Check if the program is a rainbowify program, if yes do some hacky stuff to fix a minecraft
    // moment
    if (toReplace.contains("rainbowify:")) {
      return "rainbowify:" + toReplace.replace("rainbowify:", "");
    }
    return toReplace;
  }

  @ModifyArg(
      method = "addUniform",
      at = @At(value = "INVOKE", target = "java/util/List.add(Ljava/lang/Object;)Z"))
  public Object renameAddUniform(Object toReplace) {
    // Adds the uniform as a custom uniform if this is a rainbowify shader
    if (toReplace instanceof GlUniform glUniform && this.name.contains("rainbowify:")) {
      customUniforms.put(glUniform.getName(), glUniform);
    }
    return toReplace;
  }

  @Override
  public GlUniform customUniform(String name) {
    RenderSystem.assertOnRenderThread();
    return this.customUniforms.get(name);
  }
}
