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
package de.lennox.rainbowify.config;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.option.BooleanOption;
import de.lennox.rainbowify.config.option.EnumOption;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;

import java.io.File;
import java.util.ArrayList;

public class Config {

    public static final BooleanOption BLUR = new BooleanOption("blur", new TranslatableText("rainbowify.setting.blur.tooltip"), true);
    public static final EnumOption<BlurAmount> BLUR_AMOUNT = new EnumOption<>("blur_amount", BlurAmount.MEDIUM);

    public static Option[] parseOptions() {
        ArrayList<Option> parsedOptions = new ArrayList<>();
        RainbowifyMod.instance().optionRepository().options().forEach(customOption -> parsedOptions.add(customOption.parseAsOption()));
        return parsedOptions.toArray(Option[]::new);
    }

    public enum BlurAmount {
        LOW(5),
        MEDIUM(15),
        HIGH(25),
        VERY_HIGH(50),
        EXTREME(100);

        private final int radius;

        BlurAmount(int radius) {
            this.radius = radius;
        }

        public int radius() {
            return radius;
        }
    }

}
