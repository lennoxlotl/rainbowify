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
import net.minecraft.client.option.Option;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.Optional;

import static net.minecraft.client.option.CyclingOption.*;

public class EnumOption<E extends Enum<E>> extends CustomOption<Enum<E>> {
    private final Class<E> optionEnum;

    public EnumOption(String key, E defaultValue) {
        super(key, "rainbowify.setting." + key, defaultValue);
        this.optionEnum = defaultValue.getDeclaringClass();
    }

    private static <E extends Enum<E>> Text valueText(EnumOption<E> option, E value) {
        return new TranslatableText(option.translationKey + "." + value.name().toLowerCase());
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
        if(object.has("value")) {
            var enumName = object.get("value").getAsString();
            var possibleConstant = Arrays.stream(optionEnum.getEnumConstants()).filter(constant -> constant.name().equals(enumName)).findFirst();
            possibleConstant.ifPresent(constant -> value = constant);
        }
    }

    @Override
    public Option parseAsOption() {
        OptionRepository optionRepository = RainbowifyMod.instance().optionRepository();
        return create(
            translationKey,
            optionEnum.getEnumConstants(),
            value -> valueText(this, value),
            ignored -> optionRepository.enumOption(name),
            (ignored, option, value) -> optionRepository.setEnumOption(name, value)
        );
    }
}
