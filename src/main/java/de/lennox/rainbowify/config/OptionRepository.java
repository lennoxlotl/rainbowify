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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import de.lennox.rainbowify.config.file.ConfigFile;
import de.lennox.rainbowify.config.file.ParsedOption;
import de.lennox.rainbowify.config.option.BooleanOption;
import de.lennox.rainbowify.config.option.CategoryOption;
import de.lennox.rainbowify.config.option.EnumOption;
import de.lennox.rainbowify.config.option.SliderOption;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OptionRepository {
  private static final String CONFIG_VERSION = "1";

  @SuppressWarnings("rawtypes")
  private final Map<String, Option> configOptions = new HashMap<>();

  private final File configLocation =
      new File(FabricLoader.getInstance().getConfigDir().toFile(), "rainbowify.json");
  private final Gson gson =
      new GsonBuilder()
          .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
          .setLenient()
          .setPrettyPrinting()
          .create();

  /** Initializes all configuration options */
  public void init() throws IOException {
    add(CategoryOption.of("cgeneral", BooleanOption.of("enabled", true)));
    // Add blur options
    add(
        CategoryOption.of(
            "cblur",
            BooleanOption.of("blur", Text.translatable("rainbowify.setting.blur.tooltip"), false),
            SliderOption.of("blur_amount", 3, 1, 8)));
    // Add rainbow options
    add(
        CategoryOption.of(
            "crainbow",
            BooleanOption.of(
                "rainbow", Text.translatable("rainbowify.setting.rainbow.tooltip"), true),
            EnumOption.of("rainbow_opacity", CyclingOptions.RainbowOpacity.HIGH),
            EnumOption.of("rainbow_speed", CyclingOptions.RainbowSpeed.MEDIUM)));
    // Add glint options
    add(
        CategoryOption.of(
            "cglint",
            BooleanOption.of("glint", Text.translatable("rainbowify.setting.glint.tooltip"), false),
            BooleanOption.of("insane_armor", false)));
    load();
  }

  /** Loads all currently saved config options from the json file */
  public void load() throws IOException {
    if (configLocation.exists()) {
      // Check if the config being loaded is an old configuration file
      boolean oldConfig = !JsonParser.parseReader(new FileReader(configLocation)).isJsonObject();
      if (oldConfig) {
        warnOldConfig();
        return;
      }
      // Load the config
      String configContent =
          new String(IOUtils.toByteArray(new FileReader(configLocation), StandardCharsets.UTF_8));
      ConfigFile file = gson.fromJson(configContent, ConfigFile.class);
      if (!file.version().equals(CONFIG_VERSION)) {
        warnOldConfig();
        return;
      }
      // Load all options values
      for (ParsedOption parsedOption : file.options()) {
        //noinspection rawtypes
        Option option = optionOf(parsedOption.name());
        // Check if the option exists
        if (option == null) {
          continue;
        }
        // Set the value of the option
        try {
          option.fromConfig(parsedOption);
        } catch (Exception ex) {
          System.err.println(
              "The config option "
                  + option.name
                  + " could not be loaded! This might be due to a config option change.");
        }
      }
    }
  }

  /**
   * Warns the user in the console that his config is outdated and has to be re-configured
   *
   * @since 2.0.0
   */
  private void warnOldConfig() {
    System.out.println(
        "WARNING: Your old config has been deleted due to it being deprecated, you need to re-configure the mod!");
    //noinspection ResultOfMethodCallIgnored
    configLocation.delete();
  }

  /** Saves all selected configuration options to a json file */
  public void save() throws IOException {
    // Parse all options
    List<ParsedOption> parsedOptions = new ArrayList<>();
    configOptions.values().stream()
        .filter(option -> !(option instanceof CategoryOption))
        .forEach(option -> parsedOptions.add(option.parseConfig()));
    // Write the parsed options into the file
    FileOutputStream fos = new FileOutputStream(configLocation);
    fos.write(gson.toJson(new ConfigFile(CONFIG_VERSION, parsedOptions)).getBytes());
    fos.flush();
    fos.close();
  }

  /**
   * Adds a new option
   *
   * @param option The option which is going to be added
   */
  @SuppressWarnings("rawtypes")
  public void add(Option option) {
    // Add the option
    configOptions.put(option.name, option);
  }

  /**
   * Returns an option by its name
   *
   * @param name The option name
   * @return The option
   */
  @SuppressWarnings("rawtypes")
  public Option optionOf(String name) {
    return configOptions.values().stream()
        .filter(option -> !(option instanceof CategoryOption))
        .filter(option -> option.name.equals(name))
        .findFirst()
        .orElse(null);
  }

  public Collection<Option> options() {
    return configOptions.values();
  }
}
