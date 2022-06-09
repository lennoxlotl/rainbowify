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

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.CustomOption;
import de.lennox.rainbowify.config.OptionRepository;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class EnumOption<E extends Enum<E>> extends CustomOption<Enum<E>> {
  private final Class<E> optionEnum;

  public EnumOption(String key, E defaultValue) {
    super(key, "rainbowify.setting." + key, defaultValue);
    this.optionEnum = defaultValue.getDeclaringClass();
  }

  private static <E extends Enum<E>> Text enumTooltipTextOf(EnumOption<E> option, E value) {
    return Text.translatable(option.translationKey + "." + value.name().toLowerCase());
  }

  @Override
  public JsonObject parseJson() {
    var json = new JsonObject();
    json.addProperty("name", name);
    json.addProperty("value", value.name());
    return json;
  }

  @Override
  public void fromJson(JsonObject object) {
    if (object.has("value")) {
      var enumName = object.get("value").getAsString();
      var possibleConstant =
          Arrays.stream(optionEnum.getEnumConstants())
              .filter(constant -> constant.name().equals(enumName))
              .findFirst();
      possibleConstant.ifPresent(constant -> value = constant);
    }
  }

  /**
   * Converts all enum constants as array to a list of strings (their names)
   *
   * @param constants The constants
   * @return The names of the constants
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
            enumTooltipTextOf(
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
              optionRepository.optionBy(name).value = enumValueOf((String) newValue);
            });
  }
}
