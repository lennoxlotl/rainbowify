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
package de.lennox.rainbowify.config.option;

import com.google.gson.JsonObject;
import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.CustomOption;
import de.lennox.rainbowify.config.OptionRepository;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

import static net.minecraft.client.option.CyclingOption.create;

public class BooleanOption extends CustomOption<Boolean> {

    private final Text enabledText;
    private final Text disabledText;
    private final Text tooltip;

    public BooleanOption(String key, Boolean defaultValue) {
        super(key, "rainbowify.setting." + key, defaultValue);
        this.enabledText = new TranslatableText(translationKey + ".true");
        this.disabledText = new TranslatableText(translationKey + ".false");
        tooltip = null;
    }

    public BooleanOption(String key, Text tooltip, Boolean defaultValue) {
        super(key, "rainbowify.setting." + key, defaultValue);
        this.enabledText = new TranslatableText(translationKey + ".true");
        this.disabledText = new TranslatableText(translationKey + ".false");
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
    public CyclingOption<Boolean> parseAsOption() {
        OptionRepository optionRepository = RainbowifyMod.instance().optionRepository();

        var booleanCyclingOption = create(
            translationKey,
            enabledText,
            disabledText,
            ignored -> (Boolean) optionRepository.optionBy(name).value,
            (ignored, option, value) -> optionRepository.optionBy(name).value = value
        );

        if (tooltip != null) {
            booleanCyclingOption = booleanCyclingOption.tooltip((client) -> {
                List<OrderedText> list = client.textRenderer.wrapLines(tooltip, 200);
                return (value) -> list;
            });
        }

        return booleanCyclingOption;
    }

}
