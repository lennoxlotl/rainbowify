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
package de.lennox.rainbowify.config.option;

import com.mojang.serialization.Codec;
import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.Option;
import de.lennox.rainbowify.config.OptionRepository;
import de.lennox.rainbowify.config.file.ParsedOption;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * The enum option of a type T, used to create cycling options
 *
 * @param <E> The type E
 * @since 2.0.0
 */
public class EnumOption<E extends Enum<E>> extends Option<Enum<E>> {
  private final Class<E> optionEnum;

  public EnumOption(String key, E defaultValue) {
    super(key, "rainbowify.setting." + key, defaultValue);
    this.optionEnum = defaultValue.getDeclaringClass();
  }

  @Override
  public ParsedOption parseConfig() {
    return new ParsedOption(name, value);
  }

  @Override
  public void fromConfig(ParsedOption option) {
    this.value = E.valueOf(optionEnum, (String) option.value());
  }

  /**
   * Retrieves a translatable text for a given enum value
   *
   * @param option The option
   * @param value The value
   * @return The translatable text
   * @param <E> The enum setting type T
   * @since 2.0.0
   */
  private static <E extends Enum<E>> Text enumValueTextOf(EnumOption<E> option, E value) {
    return Text.translatable(option.translationKey + "." + value.name().toLowerCase());
  }

  /**
   * Converts all enum constants as array to a list of strings (their names)
   *
   * @param constants The constants
   * @return The names of the constants
   * @since 2.0.0
   */
  private List<String> enumNamesOf(E[] constants) {
    // Map the constants to names
    //noinspection Convert2MethodRef
    return Arrays.stream(constants).map(e -> e.name()).toList();
  }

  /**
   * Parses a given String value to the E of the setting, if there is none with that name it will
   * use the first one in the constant array
   *
   * @param value The string value
   * @return The value as E
   * @since 2.0.0
   */
  private E enumValueOf(String value) {
    E[] options = optionEnum.getEnumConstants();
    // Resolve the requested value
    return Arrays.stream(options)
        .filter(e -> e.name().equalsIgnoreCase(value))
        .findFirst()
        .orElse(options[0]);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public SimpleOption parseAsOption() {
    // Create the option
    OptionRepository optionRepository = RainbowifyMod.instance().optionRepository();
    E[] options = optionEnum.getEnumConstants();
    //noinspection unchecked
    return new SimpleOption(
        translationKey,
        // Enum values can't have tooltips yet...
        SimpleOption.emptyTooltip(),
        (optionText, value) ->
            enumValueTextOf(
                this,
                // Retrieve the value E from the selected value as String
                enumValueOf((String) value)),
        // Create a custom cycling callback
        new SimpleOption.AlternateValuesSupportingCyclingCallbacks<>(
            enumNamesOf(options), List.of(), () -> false, SimpleOption::setValue, Codec.STRING),
        value.name(),
        (Consumer<Object>)
            newValue -> {
              // Update the options value in the option repository
              optionRepository.optionOf(name).value = enumValueOf((String) newValue);
            });
  }

  /**
   * Creates an enum option
   *
   * @param key The key name
   * @param defaultValue The default value
   * @return The enum option
   * @param <T> The enum of selectables
   * @since 2.0.0
   */
  public static <T extends Enum<T>> EnumOption<T> of(String key, T defaultValue) {
    return new EnumOption<>(key, defaultValue);
  }
}
