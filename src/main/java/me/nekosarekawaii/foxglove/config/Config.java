package me.nekosarekawaii.foxglove.config;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

/**
 * The Config class is an abstract base class for configurations in the Foxglove mod.
 * It provides common functionality for saving and loading configurations to/from JSON files.
 */
public abstract class Config {

    /**
     * The file representing the configuration JSON file.
     */
    public final File file;

    /**
     * Initializes a new instance of the Config class.
     *
     * @param configDir the directory where the configuration file is located
     * @param name      the name of the configuration file (without the ".json" extension)
     */
    public Config(final File configDir, final String name) {
        this.file = new File(configDir, name + ".json");

        // Create the directory if it doesn't exist
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        // Create the configuration file if it doesn't exist
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Saves the configuration to a JSON object.
     *
     * @return the JSON object representing the configuration
     * @throws IOException if an I/O error occurs during the save process
     */
    public abstract JsonObject save() throws IOException;

    /**
     * Loads the configuration from a JSON object.
     *
     * @param jsonObject the JSON object representing the configuration
     * @throws IOException if an I/O error occurs during the load process
     */
    public abstract void load(final JsonObject jsonObject) throws IOException;

}
