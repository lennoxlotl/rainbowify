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
package de.lennox.rainbowify.config;

import de.lennox.rainbowify.RainbowifyMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigScreen extends GameOptionsScreen {
  private final Screen previous;
  private ButtonListWidget list;

  public ConfigScreen(Screen previous) {
    super(
        previous,
        MinecraftClient.getInstance().options,
        Text.translatable("rainbowify.setting.title"));
    this.previous = previous;
  }

  /** Initializes the configuration screen */
  protected void init() {
    this.list =
        new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
    this.list.addAll(RainbowifyMod.instance().optionRepository().parsedOptions());
    this.addSelectableChild(this.list);
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
    this.list.render(matrices, mouseX, mouseY, delta);
    drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xffffff);
    super.render(matrices, mouseX, mouseY, delta);
    List<OrderedText> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
    if (list != null) {
      this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
    }
  }

  /** Called when the gui is closed */
  public void removed() {
    RainbowifyMod.instance().optionRepository().save();
  }
}
