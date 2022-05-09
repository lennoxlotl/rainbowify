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

import static net.minecraft.client.gui.DrawableHelper.drawCenteredText;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.bus.events.ScreenBackgroundDrawEvent;
import de.lennox.rainbowify.config.Config;
import de.lennox.rainbowify.mixin.modifications.accessor.ScreenAccessor;
import de.lennox.rainbowify.mixin.modifications.invoker.DrawableHelperInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DeathScreen.class)
public abstract class MixinDeathScreen extends MixinScreen {
  @Shadow
  @Nullable
  protected abstract Style getTextComponentUnderMouse(int mouseX);

  @Shadow private Text scoreText;

  @Shadow @Final private Text message;

  /**
   * @author Lennox
   * @reason Remove the gradient in the background of the death screen (bad solution to fix this,
   *     need to find something better)
   */
  @Overwrite
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    if (Config.ENABLED.value) {
      if (MinecraftClient.getInstance().world != null) {
        RainbowifyMod.instance().eventBus().publish(new ScreenBackgroundDrawEvent(matrices));
      }
    } else {
      ((DrawableHelperInvoker) this)
          .invokeFillGradient(matrices, 0, 0, this.width, this.height, 1615855616, -1602211792);
    }
    matrices.push();
    matrices.scale(2.0F, 2.0F, 2.0F);
    drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2 / 2, 30, 16777215);
    matrices.pop();
    if (this.message != null) {
      drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, 16777215);
      if (mouseY > 85 && mouseY < 94) {
        var style = this.getTextComponentUnderMouse(mouseX);
        this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
      }
    }
    drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, 16777215);
    for (Drawable drawable : ((ScreenAccessor) this).getDrawables()) {
      drawable.render(matrices, mouseX, mouseY, delta);
    }
  }
}
