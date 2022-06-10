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
package de.lennox.rainbowify.config;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.config.option.BooleanOption;
import de.lennox.rainbowify.config.option.EnumOption;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.ArrayList;

// TODO: Rework this in a non static form factor, for now this is fine
public class Config {
  public static final BooleanOption ENABLED = BooleanOption.of("enabled", true);
  public static final BooleanOption RAINBOW =
      BooleanOption.of("rainbow", Text.translatable("rainbowify.setting.rainbow.tooltip"), true);
  public static final BooleanOption BLUR =
      BooleanOption.of("blur", Text.translatable("rainbowify.setting.blur.tooltip"), false);

  public static final BooleanOption GLINT =
      BooleanOption.of("glint", Text.translatable("rainbowify.setting.glint.tooltip"), false);
  public static final BooleanOption INSANE_ARMOR = BooleanOption.of("insane_armor", false);
  public static final EnumOption<BlurAmount> BLUR_AMOUNT =
      EnumOption.of("blur_amount", BlurAmount.MEDIUM);
  public static final EnumOption<RainbowOpacity> RAINBOW_OPACITY =
      EnumOption.of("rainbow_opacity", RainbowOpacity.HIGH);
  public static final EnumOption<RainbowSpeed> RAINBOW_SPEED =
      EnumOption.of("rainbow_speed", RainbowSpeed.MEDIUM);

  /**
   * Parses all options as Minecraft Options
   *
   * @return The parsed options
   * @see SimpleOption
   * @see OptionRepository
   */
  public static SimpleOption[] parseOptions() {
    // Collect all options
    ArrayList<SimpleOption> parsedOptions = new ArrayList<>();
    RainbowifyMod.instance()
        .optionRepository()
        .options()
        .forEach(customOption -> parsedOptions.add(customOption.parseAsOption()));
    return parsedOptions.toArray(SimpleOption[]::new);
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
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    VERY_HIGH(5),
    EXTREME(6),
    INSANE(7),
    CRAZY(8);

    private final int radius;

    BlurAmount(int radius) {
      this.radius = radius;
    }

    public int offset() {
      return radius;
    }
  }
}
