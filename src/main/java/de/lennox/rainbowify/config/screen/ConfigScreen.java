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
package de.lennox.rainbowify.config.screen;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.option.CategoryOption;
import de.lennox.rainbowify.config.screen.widget.CategoryListEntry;
import de.lennox.rainbowify.config.screen.widget.CategoryListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigScreen extends GameOptionsScreen {
  private final Screen previous;
  private CategoryListWidget categoryListWidget;

  public ConfigScreen(Screen previous) {
    super(
        previous,
        MinecraftClient.getInstance().options,
        Text.translatable("rainbowify.setting.title"));
    this.previous = previous;
  }

  /** Initializes the configuration screen */
  protected void init() {
    // Collect all option categories
    List<RenderedCategory> categories = new ArrayList<>();
    AtomicInteger offset = new AtomicInteger(52);
    RainbowifyMod.instance().optionRepository().options().stream()
        .filter(option -> option instanceof CategoryOption)
        .map(option -> (CategoryOption) option)
        .forEach(
            categoryOption -> categories.add(categoryOption.createRenderCategory(this, offset)));
    // Create the category list renderer widget
    this.categoryListWidget =
        new CategoryListWidget(this.client, this.width, this.height, 32, height - 32, 25);
    this.categoryListWidget.addAll(categories);
    // Add a "Done" button
    this.addDrawableChild(
        new ButtonWidget(
            this.width / 2 - 100,
            this.height - 27,
            200,
            20,
            ScreenTexts.DONE,
            (button) -> {
              RainbowifyMod.instance().optionRepository().save();
              if (this.client != null) {
                this.client.setScreen(this.previous);
              }
            }));
  }

  /**
   * Renders the configuration screen
   *
   * @param matrices The current draw matrix
   * @param mouseX The mouse x position
   * @param mouseY The mouse y position
   * @param delta The render-tick delta
   */
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    this.renderBackground(matrices);
    this.categoryListWidget.render(matrices, mouseX, mouseY, delta);
    drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xffffff);
    super.render(matrices, mouseX, mouseY, delta);
    // Iterate through all categories to find a tool-tip if there is one to be rendered
    List<OrderedText> list = null;
    for (CategoryListEntry child : categoryListWidget.children()) {
      ButtonListWidget widget = child.renderedCategory().listWidget();
      // Get the tool-tip for the individual category
      list = getHoveredButtonTooltip(widget, mouseX, mouseY);
      // If a tool-tip was found another one won't be rendered
      if (list != null) {
        break;
      }
    }
    // Render the tool-tip
    if (list != null) {
      this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    this.categoryListWidget.mouseClicked(mouseX, mouseY, button);
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    this.categoryListWidget.mouseScrolled(mouseX, mouseY, amount);
    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    this.categoryListWidget.mouseReleased(mouseX, mouseY, button);
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    this.categoryListWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    this.categoryListWidget.mouseMoved(mouseX, mouseY);
    super.mouseMoved(mouseX, mouseY);
  }

  /** Called when the gui is closed */
  public void removed() {
    RainbowifyMod.instance().optionRepository().save();
  }
}
