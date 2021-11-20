/*
 * Copyright (c) 2021 Lennox
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
package de.lennox.rainbowify;

import de.lennox.rainbowify.bus.Event;
import de.lennox.rainbowify.bus.EventBus;
import de.lennox.rainbowify.config.OptionRepository;
import de.lennox.rainbowify.effect.EffectRepository;
import net.fabricmc.api.ModInitializer;

public class RainbowifyMod implements ModInitializer {
  private static RainbowifyMod rainbowifyMod;
  private final EventBus<Event> eventBus = new EventBus<>();
  private final EffectRepository effectRepository = new EffectRepository();
  private final OptionRepository optionRepository = new OptionRepository();

  public static RainbowifyMod instance() {
    return rainbowifyMod;
  }

  @Override
  public void onInitialize() {
    rainbowifyMod = this;
  }

  public void init() {
    // Initialize the mod
    System.out.println("Loading Rainbowify.");
    eventBus.createSubscription(this);
    optionRepository.init();
    System.out.println("Loaded Rainbowify successfully.");
  }

  public void preShaderLoad() {
    effectRepository.init();
  }

  public OptionRepository optionRepository() {
    return optionRepository;
  }

  public EventBus<Event> eventBus() {
    return eventBus;
  }
}
