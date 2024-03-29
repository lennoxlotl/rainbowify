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
package de.lennox.rainbowify.config.screen;

import de.lennox.rainbowify.config.screen.widget.CategoryButtonListWidget;
import de.lennox.rainbowify.mixin.modifications.accessor.EntryListWidgetAccessor;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * Renders a generated category
 *
 * @author Lennox
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public class RenderedCategory {
  private final Text title;
  private final List<SimpleOption> options;
  private final OptionListWidget listWidget;
  private final int height;

  public RenderedCategory(
      Text title, List<SimpleOption> options, CategoryButtonListWidget listWidget, int height) {
    this.title = title;
    this.options = options;
    this.listWidget = listWidget;
    this.height = height;
  }

  /**
   * Renders the category
   *
   * @param context The rendering context
   * @param y       The y-position of the category
   * @param mouseX  The mouse x
   * @param mouseY  The mouse y
   * @param delta   The render tick delta
   * @since 2.0.0
   */
  public void render(DrawContext context, int y, int mouseX, int mouseY, float delta) {
    // Render the title of the category
    MinecraftClient client = MinecraftClient.getInstance();
    float width = client.getWindow().getScaledWidth();
    context.drawTextWithShadow(client.textRenderer, title, (int) (width / 2 - client.textRenderer.getWidth(title) / 2), y - 13, -1);

    // Render the list widget
    //noinspection rawtypes
    EntryListWidgetAccessor accessor = (EntryListWidgetAccessor) listWidget;

    // Re-position the category
    accessor.setTop(y);
    accessor.setBottom(y + height - 20);
    listWidget.render(context, mouseX, mouseY, delta);
  }

  public OptionListWidget listWidget() {
    return listWidget;
  }

  public List<SimpleOption> options() {
    return options;
  }

  public int height() {
    return height;
  }

  /**
   * Creates a new rendered category used for creating a ConfigScreen
   *
   * @param title      The title
   * @param options    The options
   * @param listWidget The option list widget
   * @param height     The height
   * @return The created render category
   * @since 2.0.0
   */
  public static RenderedCategory of(
      Text title, List<SimpleOption> options, CategoryButtonListWidget listWidget, int height) {
    return new RenderedCategory(title, options, listWidget, height);
  }
}
