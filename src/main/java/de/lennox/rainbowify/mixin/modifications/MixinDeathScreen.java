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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@SuppressWarnings("unused")
@Mixin(DeathScreen.class)
public abstract class MixinDeathScreen extends MixinScreen {
  @Shadow
  @Nullable
  protected abstract Style getTextComponentUnderMouse(int mouseX);

  @Shadow
  private Text scoreText;

  @Shadow
  @Final
  private Text message;

  @Shadow private @Nullable ButtonWidget titleScreenButton;

  /**
   * @author Lennox
   * @reason Remove the gradient in the background of the death screen (bad solution to fix this,
   * need to find something better)
   */
  @Overwrite
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    boolean enabled =
        (boolean) RainbowifyMod.instance().optionRepository().optionOf("enabled").value;
    if (enabled) {
      if (MinecraftClient.getInstance().world != null) {
        // Publish the screen rendering background event if rainbowify is enabled
        RainbowifyMod.instance().eventBus().publish(new ScreenBackgroundDrawEvent(context));
      }
    } else {
      // Draw the normal background if rainbowify is disabled
      context.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
    }

    context.getMatrices().push();
    context.getMatrices().scale(2.0F, 2.0F, 2.0F);
    context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2 / 2, 30, 16777215);
    context.getMatrices().pop();
    if (this.message != null) {
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.message, this.width / 2, 85, 16777215);
    }

    context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.scoreText, this.width / 2, 100, 16777215);
    if (this.message != null && mouseY > 85) {
      Objects.requireNonNull(this.textRenderer);
      if (mouseY < 85 + 9) {
        Style style = this.getTextComponentUnderMouse(mouseX);
        context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
      }
    }

    if (this.titleScreenButton != null && this.client.getAbuseReportContext().hasDraft()) {
      context.drawTexture(ClickableWidget.WIDGETS_TEXTURE, this.titleScreenButton.getX() + this.titleScreenButton.getWidth() - 17, this.titleScreenButton.getY() + 3, 182, 24, 15, 15);
    }
  }
}
