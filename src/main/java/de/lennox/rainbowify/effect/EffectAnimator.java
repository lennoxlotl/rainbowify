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
package de.lennox.rainbowify.effect;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.animation.Animation;
import de.lennox.rainbowify.bus.Subscription;
import de.lennox.rainbowify.bus.events.InGameHudDrawEvent;
import de.lennox.rainbowify.bus.events.ScreenInitEvent;
import de.lennox.rainbowify.config.Config;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;

public class EffectAnimator {
  private static final MinecraftClient MC = MinecraftClient.getInstance();
  private final Animation fadeAnimation = new Animation(250);
  private final List<Effect> effects = new ArrayList<>();

  private final Subscription<InGameHudDrawEvent> inGameHudDrawSubscription =
      event -> {
        Config.RainbowOpacity rainbowOpacity =
            (Config.RainbowOpacity)
                RainbowifyMod.instance().optionRepository().optionBy("rainbow_opacity").value;
        var pausedScreen = false;
        var currentScreen = MC.currentScreen;
        // If the current screen is pausing the game we need to skip the animation
        if (currentScreen != null) pausedScreen = validatePause(currentScreen);
        fadeAnimation.animate(0, rainbowOpacity.opacity(), currentScreen != null);
        // Set the animation status for all effects
        for (Effect effect : effects) {
          effect.setFade(pausedScreen ? rainbowOpacity.opacity() : fadeAnimation.animation());
        }
      };

  private final Subscription<ScreenInitEvent> screenInitSubscriber =
      event -> {
        // Check if the previous screen was whether null or a screen which pauses the game or is not
        // affected by the mod
        if (event.previous() == null
            || event.previous() instanceof ProgressScreen
            || validatePause(event.previous())) {
          fadeAnimation.reset(0);
        }
      };

  /**
   * Initialize the effects to make them ready for animation
   *
   * @param effects
   */
  public void init(List<Effect> effects) {
    this.effects.addAll(effects);
    RainbowifyMod.instance().eventBus().createSubscription(this);
  }

  /**
   * Validates if a screen is a pause screen or not
   *
   * @param screen The current screen
   * @return Pause state of screen
   */
  private boolean validatePause(Screen screen) {
    // If the previous screen was null we don't need to do this check
    if (screen == null) return true;
    // Checks if the screen is pausing the game and if the player is in singleplayer as pausing only
    // works in singleplayer
    return screen.shouldPause() && MC.isInSingleplayer();
  }
}
