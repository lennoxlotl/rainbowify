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

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.Option;
import de.lennox.rainbowify.config.OptionRepository;
import de.lennox.rainbowify.config.file.ParsedOption;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

/**
 * Boolean option used to create enabled / disabled options
 *
 * @since 2.0.0
 * @author Lennox
 */
public class BooleanOption extends Option<Boolean> {
  private final Text enabledText;
  private final Text disabledText;
  private final Text tooltip;

  public BooleanOption(String key, Boolean defaultValue) {
    super(key, "rainbowify.setting." + key, defaultValue);
    this.enabledText = Text.translatable(translationKey + ".true");
    this.disabledText = Text.translatable(translationKey + ".false");
    tooltip = null;
  }

  public BooleanOption(String key, Text tooltip, Boolean defaultValue) {
    super(key, "rainbowify.setting." + key, defaultValue);
    this.enabledText = Text.translatable(translationKey + ".true");
    this.disabledText = Text.translatable(translationKey + ".false");
    this.tooltip = tooltip;
  }

  @Override
  public ParsedOption parseConfig() {
    return new ParsedOption(name, value);
  }

  @Override
  public void fromConfig(ParsedOption option) {
    this.value = (Boolean) option.value();
  }

  @Override
  public SimpleOption<Boolean> parseAsOption() {
    // Create the base option
    OptionRepository optionRepository = RainbowifyMod.instance().optionRepository();
    return new SimpleOption<>(
        translationKey,
        // Display a tool-tip
        tooltip == null ? SimpleOption.emptyTooltip() : SimpleOption.constantTooltip(tooltip),
        // Display the enabled or disabled text based on the value
        (optionText, value1) -> {
          if (value1) {
            return enabledText;
          } else {
            return disabledText;
          }
        },
        // Boolean option
        SimpleOption.BOOLEAN,
        value,
        // Set the value in the option repository once changed
        aBoolean -> optionRepository.optionOf(name).value = aBoolean);
  }

  /**
   * Creates a new boolean option without a tooltip
   *
   * @param key The key name
   * @param defaultValue The default value
   * @return The created boolean option
   * @since 2.0.0
   */
  public static BooleanOption of(String key, boolean defaultValue) {
    return new BooleanOption(key, defaultValue);
  }

  /**
   * Creates a new boolean option with a tooltip
   *
   * @param key The key name
   * @param tooltip The tool tip
   * @param defaultValue The default value
   * @return The created boolean option
   * @since 2.0.0
   */
  public static BooleanOption of(String key, Text tooltip, boolean defaultValue) {
    return new BooleanOption(key, tooltip, defaultValue);
  }
}
