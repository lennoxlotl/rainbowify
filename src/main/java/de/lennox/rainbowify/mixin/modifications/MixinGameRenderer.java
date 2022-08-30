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
package de.lennox.rainbowify.mixin.modifications;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.event.events.GlintShaderEvent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
  @Shadow
  private static Shader renderTypeGlintDirectShader;

  @Shadow
  private static Shader renderTypeArmorEntityGlintShader;

  @Shadow
  private static Shader renderTypeArmorGlintShader;

  @Inject(method = "preloadShaders", at = @At("RETURN"))
  public void preLoadShaders(ResourceFactory factory, CallbackInfo ci) {
    // Preload the shaders once minecraft does it
    RainbowifyMod.instance().preShaderLoad();
  }

  @Inject(method = "getRenderTypeGlintDirectShader", at = @At("HEAD"), cancellable = true)
  private static void directGlintShader(CallbackInfoReturnable<Shader> cir) {
    GlintShaderEvent event = new GlintShaderEvent(renderTypeGlintDirectShader);
    RainbowifyMod.instance().eventBus().publish(event);
    // Override the shader
    cir.setReturnValue(event.shader());
  }

  @Inject(method = "getRenderTypeArmorEntityGlintShader", at = @At("HEAD"), cancellable = true)
  private static void armorEntityGlintShader(CallbackInfoReturnable<Shader> cir) {
    GlintShaderEvent event = new GlintShaderEvent(renderTypeArmorEntityGlintShader);
    // Override the shader
    RainbowifyMod.instance().eventBus().publish(event);
    cir.setReturnValue(event.shader());
  }

  @Inject(method = "getRenderTypeArmorGlintShader", at = @At("HEAD"), cancellable = true)
  private static void armorGlintShader(CallbackInfoReturnable<Shader> cir) {
    GlintShaderEvent event = new GlintShaderEvent(renderTypeArmorGlintShader);
    // Override the shader
    RainbowifyMod.instance().eventBus().publish(event);
    cir.setReturnValue(event.shader());
  }
}
