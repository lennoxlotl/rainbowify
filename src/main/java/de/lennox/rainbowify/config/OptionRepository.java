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

import com.google.gson.*;
import de.lennox.rainbowify.config.option.BooleanOption;
import de.lennox.rainbowify.config.option.EnumOption;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionRepository {
  @SuppressWarnings("rawtypes")
  private final Map<String, Option> configOptions = new HashMap<>();

  private final File configLocation =
      new File(FabricLoader.getInstance().getConfigDir().toFile(), "rainbowify.json");
  private final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

  /** Initializes all configuration options */
  public void init() {
    add(BooleanOption.of("enabled", true));
    add(BooleanOption.of("rainbow", Text.translatable("rainbowify.setting.rainbow.tooltip"), true));
    add(BooleanOption.of("blur", Text.translatable("rainbowify.setting.blur.tooltip"), false));
    add(BooleanOption.of("glint", Text.translatable("rainbowify.setting.glint.tooltip"), false));
    add(BooleanOption.of("insane_armor", false));
    add(EnumOption.of("blur_amount", Config.BlurAmount.MEDIUM));
    add(EnumOption.of("rainbow_opacity", Config.RainbowOpacity.HIGH));
    add(EnumOption.of("rainbow_speed", Config.RainbowSpeed.MEDIUM));
    load();
  }

  /**
   * Parses all options as Minecraft Options
   *
   * @return The parsed options
   * @see SimpleOption
   * @see OptionRepository
   */
  @SuppressWarnings("rawtypes")
  public SimpleOption[] parsedOptions() {
    // Collect all options
    List<SimpleOption> parsedOptions = new ArrayList<>();
    configOptions.values().forEach(option -> parsedOptions.add(option.parseAsOption()));
    return parsedOptions.toArray(SimpleOption[]::new);
  }

  /** Loads all currently saved config options from the json file */
  public void load() {
    // Check if the config file exists
    if (configLocation.exists()) {
      try {
        // Parse the file content
        var parsed = JsonParser.parseReader(new FileReader(configLocation));
        if (parsed.isJsonArray()) {
          var settingsArray = parsed.getAsJsonArray();
          // Loop through all settings elements
          for (JsonElement jsonElement : settingsArray) {
            if (jsonElement.isJsonObject()) {
              // Fetch a setting by the name parameter and apply the value parameter on it
              JsonObject settingsObject = jsonElement.getAsJsonObject();
              if (settingsObject.has("name")) {
                var settingsName = settingsObject.get("name").getAsString();
                optionOf(settingsName).fromJson(settingsObject);
              }
            }
          }
        }
      } catch (Exception e) {
        System.err.println("Error while loading rainbowify settings.");
        e.printStackTrace();
      }
    }
  }

  /** Saves all selected configuration options to a json file */
  public void save() {
    // If the config location does not exist create it
    if (!configLocation.exists()) {
      try {
        //noinspection ResultOfMethodCallIgnored
        configLocation.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    // Parse all options
    var settingsArray = new JsonArray();
    configOptions.values().forEach(option -> settingsArray.add(option.parseJson()));
    // Write the parsed options into the file
    try (var fileWriter = new FileWriter(configLocation)) {
      fileWriter.write(gson.toJson(settingsArray));
    } catch (IOException e) {
      System.err.println("Error while saving rainbowify settings.");
      e.printStackTrace();
    }
  }

  /**
   * Adds a new option
   *
   * @param option The option which is going to be added
   */
  @SuppressWarnings("rawtypes")
  private void add(Option option) {
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
    return configOptions.get(name);
  }
}
