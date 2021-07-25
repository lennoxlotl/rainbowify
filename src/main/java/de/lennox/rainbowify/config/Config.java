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
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;

public class Config {

    public static final BooleanOption BLUR = new BooleanOption("blur", new TranslatableText("rainbowify.setting.blur.tooltip"), false);
    public static final EnumOption<BlurAmount> BLUR_AMOUNT = new EnumOption<>("blur_amount", BlurAmount.MEDIUM);
    public static final EnumOption<RainbowOpacity> RAINBOW_OPACITY = new EnumOption<>("rainbow_opacity", RainbowOpacity.HIGH);
    public static final EnumOption<RainbowSpeed> RAINBOW_SPEED = new EnumOption<>("rainbow_speed", RainbowSpeed.MEDIUM);

    public static Option[] parseOptions() {
        ArrayList<Option> parsedOptions = new ArrayList<>();
        RainbowifyMod.instance().optionRepository().options().forEach(customOption -> parsedOptions.add(customOption.parseAsOption()));
        return parsedOptions.toArray(Option[]::new);
    }

    public enum RainbowOpacity {
        LOW(0.1f),
        MEDIUM(0.25f),
        HIGH(0.5f),
        VERY_HIGH(0.75f),
        FULL(1.0f);

        private final float opacity;

        RainbowOpacity(float opacity) {
            this.opacity = opacity;
        }

        public float opacity() {
            return opacity;
        }
    }

    public enum RainbowSpeed {
        SLOW(5000),
        MEDIUM(3000),
        FAST(1000),
        VERY_FAST(500);

        private final long time;

        RainbowSpeed(long time) {
            this.time = time;
        }

        public long time() {
            return time;
        }
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
