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


public class ConfigManager {

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

    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.configs = new ObjectArrayList<>();

        this.addConfigs(
                this.mainConfig = new MainConfig(),
                this.modulesConfig = new ModulesConfig()
        );
    }

    private void addConfigs(final Config... configs) {
        for (final Config config : configs) {
            if (!this.configs.contains(config)) {
                this.configs.add(config);
            }
        }
    }

    public void save(final Config config) {
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

    public void save() {
        for (final Config config : this.configs) {
            save(config);
        }
    }

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
