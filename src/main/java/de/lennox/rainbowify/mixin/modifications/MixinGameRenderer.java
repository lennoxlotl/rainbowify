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

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.event.events.DrawWorldEvent;
import de.lennox.rainbowify.event.events.GlintShaderEvent;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(value = GameRenderer.class, priority = 500)
public class MixinGameRenderer {
  @Shadow
  private static ShaderProgram renderTypeGlintDirectProgram;

  @Shadow
  private static ShaderProgram renderTypeArmorEntityGlintProgram;

  @Shadow
  private static ShaderProgram renderTypeArmorGlintProgram;

  @Inject(method = "renderWorld", at = @At("HEAD"))
  public void onRenderWorld(CallbackInfo ci) {
    RainbowifyMod.instance().eventBus().publish(new DrawWorldEvent());
  }

  @Inject(method = "preloadPrograms", at = @At("RETURN"))
  public void preLoadShaders(ResourceFactory factory, CallbackInfo ci) {
    // Preload the shaders once minecraft does it
    RainbowifyMod.instance().preShaderLoad();
  }

  @Inject(method = "getRenderTypeGlintDirectProgram", at = @At("HEAD"), cancellable = true)
  private static void directGlintShader(CallbackInfoReturnable<ShaderProgram> cir) {
    GlintShaderEvent event = new GlintShaderEvent(renderTypeGlintDirectProgram);
    RainbowifyMod.instance().eventBus().publish(event);
    // Override the shader
    cir.setReturnValue(event.shader());
  }

  @Inject(method = "getRenderTypeArmorEntityGlintProgram", at = @At("HEAD"), cancellable = true)
  private static void armorEntityGlintShader(CallbackInfoReturnable<ShaderProgram> cir) {
    GlintShaderEvent event = new GlintShaderEvent(renderTypeArmorEntityGlintProgram);
    // Override the shader
    RainbowifyMod.instance().eventBus().publish(event);
    cir.setReturnValue(event.shader());
  }

  @Inject(method = "getRenderTypeArmorGlintProgram", at = @At("HEAD"), cancellable = true)
  private static void armorGlintShader(CallbackInfoReturnable<ShaderProgram> cir) {
    GlintShaderEvent event = new GlintShaderEvent(renderTypeArmorGlintProgram);
    // Override the shader
    RainbowifyMod.instance().eventBus().publish(event);
    cir.setReturnValue(event.shader());
  }
}
