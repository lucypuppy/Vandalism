package me.nekosarekawaii.foxglove.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.MainConfig;
import me.nekosarekawaii.foxglove.config.impl.ModulesConfig;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The ConfigManager class is responsible for managing the configuration files in the Foxglove mod.
 * It provides methods for saving and loading the configurations to/from JSON files.
 */
public final class ConfigManager {

    private final Gson gson;
    private final ObjectArrayList<Config> configs;

    private final MainConfig mainConfig;

    public MainConfig getMainConfig() {
        return this.mainConfig;
    }

    private final ModulesConfig modulesConfig;

    public ModulesConfig getModulesConfig() {
        return this.modulesConfig;
    }

    /**
     * Constructs a new instance of the ConfigManager class.
     * Initializes the Gson object and the list of configurations.
     * Adds the main configuration to the list.
     */
    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.configs = new ObjectArrayList<>();
        this.addConfigs(
                this.mainConfig = new MainConfig(),
                this.modulesConfig = new ModulesConfig()
        );
    }

    /**
     * Adds configurations to the list of managed configurations.
     *
     * @param configs the configurations to add
     */
    private void addConfigs(final Config... configs) {
        for (final Config config : configs) {
            if (!this.configs.contains(config)) {
                this.configs.add(config);
            }
        }
    }

    /**
     * Saves all the configurations to their respective JSON files.
     */
    public void save() {
        for (final Config config : this.configs) {
            try {
                final FileWriter fileWriter = new FileWriter(config.file);
                final PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(this.gson.toJson(config.save()));
                printWriter.close();
                fileWriter.close();
            } catch (final IOException e) {
                Foxglove.getInstance().getLogger().error("Failed to save Config: " + config.file.getName(), e);
            }
        }
    }

    /**
     * Loads all the configurations from their respective JSON files.
     */
    public void load() {
        for (final Config config : this.configs) {
            try {
                final FileReader fileReader = new FileReader(config.file);
                final JsonReader jsonReader = new JsonReader(fileReader);
                final JsonElement jsonElement = JsonParser.parseReader(jsonReader);
                if (!jsonElement.isJsonNull()) {
                    if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                        fileReader.close();
                        jsonReader.close();
                        throw new JsonSyntaxException("Did not consume the entire document.");
                    } else {
                        config.load((JsonObject) jsonElement);
                    }
                }
                fileReader.close();
                jsonReader.close();
            } catch (final IOException e) {
                Foxglove.getInstance().getLogger().error("Failed to load Config: " + config.file.getName(), e);
            }
        }
    }

}
