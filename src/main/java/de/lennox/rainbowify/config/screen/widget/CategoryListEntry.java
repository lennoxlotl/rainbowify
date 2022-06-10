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
package de.lennox.rainbowify.config.screen.widget;

import de.lennox.rainbowify.config.screen.RenderedCategory;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom ElementListWidget Entry to render our custom option categories with ease
 *
 * @since 2.0.0
 * @author Lennox
 */
public class CategoryListEntry extends ElementListWidget.Entry<CategoryListEntry> {
  private final RenderedCategory renderedCategory;

  public CategoryListEntry(RenderedCategory renderedCategory) {
    this.renderedCategory = renderedCategory;
  }

  /**
   * Collects all clickable widgets from the category renderer
   *
   * @return The clickable widgets
   * @since 2.0.0
   */
  @SuppressWarnings("rawtypes")
  private List<ClickableWidget> collectClickableWidgets() {
    List<SimpleOption> options = renderedCategory.options();
    List<ClickableWidget> selectables = new ArrayList<>();
    // Get all buttons / clickable widgets for the options
    for (SimpleOption option : options) {
      selectables.add(renderedCategory.listWidget().getButtonFor(option));
    }
    return selectables;
  }

  @Override
  public void render(
      MatrixStack matrices,
      int index,
      int y,
      int x,
      int entryWidth,
      int entryHeight,
      int mouseX,
      int mouseY,
      boolean hovered,
      float tickDelta) {
    // Render the category
    renderedCategory.render(matrices, y, mouseX, mouseY, tickDelta);
  }

  @Override
  public List<? extends Selectable> selectableChildren() {
    return collectClickableWidgets();
  }

  @Override
  public List<? extends Element> children() {
    return collectClickableWidgets();
  }

  public RenderedCategory renderedCategory() {
    return renderedCategory;
  }

  /**
   * Creates a new category list entry
   *
   * @param category The category renderer
   * @return The category list entry
   * @since 2.0.0
   */
  public static CategoryListEntry of(RenderedCategory category) {
    return new CategoryListEntry(category);
  }
}
