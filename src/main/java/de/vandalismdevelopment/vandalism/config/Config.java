package de.vandalismdevelopment.vandalism.config;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;


public abstract class Config {

    private final File file;
    private final String name;

    public Config(final File configDir, final String name) {
        this.name = name;
        this.file = new File(configDir, name + ".json");
        if (!configDir.exists()) configDir.mkdirs();
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public File getFile() {
        return this.file;
    }

    public String getName() {
        return this.name;
    }

    public abstract JsonObject save() throws IOException;

    public abstract void load(final JsonObject jsonObject) throws IOException;

}
