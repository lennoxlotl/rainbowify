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
package de.lennox.rainbowify.gl.framebuffer;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.event.Subscription;
import de.lennox.rainbowify.event.events.ScreenResolutionChangeEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.WindowFramebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

public class RefreshingWindowBuffer extends WindowFramebuffer {
  private static final MinecraftClient MC = MinecraftClient.getInstance();

  // Refreshes the framebuffer size if the minecraft window got resized
  @SuppressWarnings("unused")
  public final Subscription<ScreenResolutionChangeEvent> screenResolutionSubscription =
      event ->
          resize(
              MC.getWindow().getFramebufferWidth(), MC.getWindow().getFramebufferHeight(), false);

  public RefreshingWindowBuffer(int width, int height) {
    super(width, height);
    // Create the subscription for the buffer callback
    RainbowifyMod.instance().eventBus().createSubscription(this);
  }

  public void check(int width, int height) {
    if (this.textureWidth != width || this.textureHeight != height) {
      this.resize(Math.max(width, 1), Math.max(height, 1), false);
      // Set the texture filter and wrapping
      setTexFilter(GL_LINEAR);
      beginRead();
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
      endRead();
    }
  }
}
