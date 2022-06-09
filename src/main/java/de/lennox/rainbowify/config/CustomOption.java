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

import com.google.gson.JsonObject;
import net.minecraft.client.option.SimpleOption;

public abstract class CustomOption<T> {
  public final String name, translationKey;
  public T value;

  public CustomOption(String name, String translationKey, T defaultValue) {
    this.name = name;
    this.translationKey = translationKey;
    this.value = defaultValue;
  }

  /**
   * Parses the option as JsonObject
   *
   * @see JsonObject
   * @see OptionRepository
   * @return The parsed option
   */
  public abstract JsonObject parseJson();

  /**
   * Loads the config value from a JsonObject
   *
   * @see JsonObject
   * @see OptionRepository
   * @param object The json object
   */
  public abstract void fromJson(JsonObject object);

  /**
   * Parses the option as Minecraft Option
   *
   * @see SimpleOption
   * @see OptionRepository
   * @return The parsed option
   */
  @SuppressWarnings("rawtypes")
  public abstract SimpleOption parseAsOption();
}
