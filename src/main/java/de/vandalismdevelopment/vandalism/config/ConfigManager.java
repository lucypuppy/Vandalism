package de.vandalismdevelopment.vandalism.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.*;
import de.vandalismdevelopment.vandalism.config.impl.account.AccountsConfig;
import de.vandalismdevelopment.vandalism.config.impl.main.MainConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final Gson gson;
    private final List<Config> configs;

    private final MainConfig mainConfig;
    public MainConfig getMainConfig() {
        return this.mainConfig;
    }

    private final ModulesConfig modulesConfig;
    public ModulesConfig getModulesConfig() {
        return this.modulesConfig;
    }

    private final AccountsConfig accountsConfig;
    public AccountsConfig getAccountConfig() {
        return this.accountsConfig;
    }

    private final ScriptsConfig scriptsConfig;
    public ScriptsConfig getScriptsConfig() {
        return this.scriptsConfig;
    }

    private final CustomHUDConfig customHUDConfig;
    public CustomHUDConfig getCustomHUDConfig() {
        return this.customHUDConfig;
    }

    private final MenusConfig menusConfig;
    public MenusConfig getMenusConfig() {
        return this.menusConfig;
    }

    private final IRCConfig ircConfig;

    public IRCConfig getIrcConfig() {
        return this.ircConfig;
    }

    public ConfigManager(final File dir) {
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.configs = new ArrayList<>();
        this.addConfigs(
                this.mainConfig = new MainConfig(dir),
                this.modulesConfig = new ModulesConfig(dir),
                this.accountsConfig = new AccountsConfig(dir),
                this.scriptsConfig = new ScriptsConfig(dir),
                this.customHUDConfig = new CustomHUDConfig(dir),
                this.menusConfig = new MenusConfig(dir),
                this.ircConfig = new IRCConfig(dir)
        );
    }

    private void addConfigs(final Config... configs) {
        for (final Config config : configs) {
            if (!this.configs.contains(config)) {
                this.configs.add(config);
            }
        }
    }

    public void load() {
        Vandalism.getInstance().getLogger().info("Loading configs...");
        for (final Config config : this.configs) {
            try {
                final FileReader fileReader = new FileReader(config.getFile());
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
                Vandalism.getInstance().getLogger().info("Config " + config.getFile().getName() + " loaded.");
            } catch (final IOException | JsonSyntaxException e) {
                Vandalism.getInstance().getLogger().error("Failed to load config: " + config.getFile().getName(), e);
            }
        }
    }

    public void save(final Config config) {
        try {
            final FileWriter fileWriter = new FileWriter(config.getFile());
            final PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(this.gson.toJson(config.save()));
            printWriter.close();
            fileWriter.close();
            Vandalism.getInstance().getLogger().info("Config " + config.getFile().getName() + " saved.");
        } catch (final IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to save config: " + config.getFile().getName(), e);
        }
    }

    public void save() {
        for (final Config config : this.configs) {
            this.save(config);
        }
    }

}
