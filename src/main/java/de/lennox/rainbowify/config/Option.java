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
package de.lennox.rainbowify.config;

import com.google.gson.JsonObject;
import de.lennox.rainbowify.config.file.ParsedOption;
import net.minecraft.client.option.SimpleOption;

/**
 * The base option type
 *
 * @param <T>
 * @author Lennox
 * @since 1.0.0
 */
public abstract class Option<T> {
  public final String name, translationKey;
  public T value;

  public Option(String name, String translationKey, T defaultValue) {
    this.name = name;
    this.translationKey = translationKey;
    this.value = defaultValue;
  }

  /**
   * Parses the option as JsonObject
   *
   * @return The parsed option
   * @see JsonObject
   * @see OptionRepository
   * @since 2.0.0
   */
  public abstract ParsedOption parseConfig();

  /**
   * Sets the options value by the given parsed config option
   *
   * @param option The config option
   * @since 2.0.0
   */
  public abstract void fromConfig(ParsedOption option);

  /**
   * Parses the option as Minecraft Option
   *
   * @return The parsed option
   * @see SimpleOption
   * @see OptionRepository
   * @since 1.0.0
   */
  @SuppressWarnings("rawtypes")
  public abstract SimpleOption parseAsOption();
}
