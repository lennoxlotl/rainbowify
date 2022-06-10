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

/**
 * Option for integer options in the form of sliders
 *
 * @since 2.0.0
 * @author Lennox
 */
public class SliderOption extends Option<Integer> {
  private final Text tooltip;
  private final int min, max;

  public SliderOption(String key, int defaultValue, int min, int max) {
    super(key, "rainbowify.setting." + key, defaultValue);
    tooltip = null;
    this.min = min;
    this.max = max;
  }

  public SliderOption(String key, Text tooltip, int defaultValue, int min, int max) {
    super(key, "rainbowify.setting." + key, defaultValue);
    this.tooltip = tooltip;
    this.min = min;
    this.max = max;
  }

  @Override
  public ParsedOption parseConfig() {
    return new ParsedOption(name, value);
  }

  @Override
  public void fromConfig(ParsedOption option) {
    this.value = (int) option.value();
  }

  @Override
  public SimpleOption<Integer> parseAsOption() {
    // Create the base option
    OptionRepository optionRepository = RainbowifyMod.instance().optionRepository();
    return new SimpleOption<>(
        translationKey,
        tooltip == null ? SimpleOption.emptyTooltip() : SimpleOption.constantTooltip(tooltip),
        (optionText, val) -> Text.of(Text.translatable(translationKey).getString() + ": " + val),
        new SimpleOption.ValidatingIntSliderCallbacks(min, max),
        Codec.INT,
        value,
        aInteger -> optionRepository.optionOf(name).value = aInteger);
  }

  /**
   * Creates a new boolean option without a tooltip
   *
   * @param key The key name
   * @param defaultValue The default value#
   * @param min The minimum value
   * @param max The maximum value
   * @return The created boolean option
   * @since 2.0.0
   */
  public static SliderOption of(String key, int defaultValue, int min, int max) {
    return new SliderOption(key, defaultValue, min, max);
  }

  /**
   * Creates a new boolean option with a tooltip
   *
   * @param key The key name
   * @param tooltip The tool tip
   * @param defaultValue The default value
   * @param min The minimum value
   * @param max The maximum value
   * @return The created boolean option
   * @since 2.0.0
   */
  public static SliderOption of(String key, Text tooltip, int defaultValue, int min, int max) {
    return new SliderOption(key, tooltip, defaultValue, min, max);
  }
}
