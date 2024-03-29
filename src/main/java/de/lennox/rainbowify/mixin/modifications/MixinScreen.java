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
import de.lennox.rainbowify.event.events.ScreenBackgroundDrawEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {
  @Shadow
  public int width;
  @Shadow
  public int height;
  @Shadow
  @Nullable
  protected MinecraftClient client;

  @Shadow
  @Final
  protected Text title;

  @Shadow
  protected TextRenderer textRenderer;

  @SuppressWarnings("unused")
  @Inject(
      method = "renderBackground",
      at = @At("HEAD"),
      cancellable = true)
  public void renderBackground(DrawContext context, CallbackInfo callback) {
    if (this.client != null && this.client.world != null) {
      boolean enabled =
          (boolean) RainbowifyMod.instance().optionRepository().optionOf("enabled").value;
      if (enabled) {
        // Publish the screen background event
        RainbowifyMod.instance().eventBus().publish(new ScreenBackgroundDrawEvent(context));
        callback.cancel();
      }
    }
  }
}
