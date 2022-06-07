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
import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.CustomOption;
import de.lennox.rainbowify.config.OptionRepository;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class BooleanOption extends CustomOption<Boolean> {
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
  public JsonObject parseJson() {
    var json = new JsonObject();
    json.addProperty("name", name);
    json.addProperty("value", value);
    return json;
  }

  @Override
  public void fromJson(JsonObject object) {
    if (object.has("value")) value = object.get("value").getAsBoolean();
  }

  @Override
  public SimpleOption<Boolean> parseAsOption() {
    // Create the base option
    OptionRepository optionRepository = RainbowifyMod.instance().optionRepository();
    return new SimpleOption<>(
        translationKey,
        tooltip == null ? SimpleOption.emptyTooltip() : SimpleOption.constantTooltip(tooltip),
        (optionText, value1) -> {
          if (value1) {
            return enabledText;
          } else {
            return disabledText;
          }
        },
        SimpleOption.BOOLEAN,
        value,
        aBoolean -> optionRepository.optionBy(name).value = aBoolean);
  }
}
