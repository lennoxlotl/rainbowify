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
package de.lennox.rainbowify.mixin.modifications;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.bus.events.ScreenInitEvent;
import de.lennox.rainbowify.bus.events.ScreenResolutionChangeEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
  @Shadow private static MinecraftClient instance;

  @Inject(method = "<init>", at = @At("TAIL"))
  public void init(CallbackInfo ci) {
    RainbowifyMod.instance().init();
  }

  @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
  public void setScreen(Screen screen, CallbackInfo callback) {
    RainbowifyMod.instance()
        .eventBus()
        .postEvent(new ScreenInitEvent(MinecraftClient.getInstance().currentScreen));
  }

  @Inject(
      method = "onResolutionChanged",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/gl/Framebuffer;resize(IIZ)V",
              ordinal = 0))
  public void onResolutionChanged(CallbackInfo i) {
    RainbowifyMod.instance().eventBus().postEvent(new ScreenResolutionChangeEvent());
  }
}
