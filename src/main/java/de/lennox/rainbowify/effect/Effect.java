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
package de.lennox.rainbowify.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

/**
 * The base effect class
 *
 * @author Lennox
 * @since 1.0.0
 */
public abstract class Effect {
  protected static final MinecraftClient MC = MinecraftClient.getInstance();
  protected float fade;

  /**
   * Initializes the effect and all it's dependencies
   *
   * @since 2.0.0
   */
  public abstract void init();

  /**
   * Draws the effect in the background of the gui
   *
   * @param stack The rendering stack
   * @since 2.0.0
   */
  public abstract void draw(MatrixStack stack);

  public void fade(float fade) {
    this.fade = fade;
  }
}
