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

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OptionRepository {

    private final List<CustomOption> configOptions = new ArrayList<>();
    private final File configLocation = new File(FabricLoader.getInstance().getConfigDir().toFile(), "rainbowify.json");
    private final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public void init() {
        configOptions.addAll(List.of(
            Config.BLUR,
            Config.BLUR_AMOUNT
        ));
        load();
    }

    public void load() {
        if(configLocation.exists()) {
            try {
                var parsed = new JsonParser().parse(new FileReader(configLocation));
                if(parsed.isJsonArray()) {
                    var settingsArray = parsed.getAsJsonArray();
                    for (JsonElement jsonElement : settingsArray) {
                        if(jsonElement.isJsonObject()) {
                            JsonObject settingsObject = jsonElement.getAsJsonObject();
                            if(settingsObject.has("name")) {
                                var settingsName = settingsObject.get("name").getAsString();
                                optionBy(settingsName).fromJson(settingsObject);
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error while loading rainbowify settings.");
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if(!configLocation.exists()) configLocation.mkdirs();

        var settingsArray = new JsonArray();
        configOptions.forEach(customOption -> settingsArray.add(customOption.parseJson()));

        try(var fileWriter = new FileWriter(configLocation)) {
            fileWriter.write(gson.toJson(settingsArray));
        } catch (IOException e) {
            System.err.println("Error while saving rainbowify settings.");
            e.printStackTrace();
        }
    }

    private CustomOption optionBy(String name) {
        var option = configOptions.stream().filter(customOption -> customOption.name.equals(name)).findFirst();
        return option.orElse(null);
    }

    public void setBooleanOption(String key, boolean value) {
        optionBy(key).value = value;
    }

    public boolean booleanOption(String key) {
        return (boolean) optionBy(key).value;
    }

    public <E extends Enum<E>> E enumOption(String key) {
        return (E) optionBy(key).value;
    }

    public <E extends Enum<E>> void setEnumOption(String key, E value) {
        optionBy(key).value = value;
    }

    public List<CustomOption> options() {
        return configOptions;
    }
}
