package de.foxglovedevelopment.foxglove.config;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;


public abstract class Config {

    public final File file;

    public Config(final File configDir, final String name) {
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

    public abstract JsonObject save() throws IOException;

    public abstract void load(final JsonObject jsonObject) throws IOException;

}
