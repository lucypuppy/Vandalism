package de.vandalismdevelopment.vandalism.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.vandalismdevelopment.vandalism.Vandalism;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractConfig<T extends JsonElement> {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> nodeType;
    private final File file;

    public AbstractConfig(final Class<T> nodeType, final String name) {
        this.nodeType = nodeType;
        this.file = new File(Vandalism.getInstance().getRunDirectory(), name + ".json");
    }

    public void save() {
        try {
            file.delete();
            file.createNewFile();

            try (final FileWriter fw = new FileWriter(file)) {
                fw.write(GSON.toJson(save0()));
                fw.flush();
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to save config " + file.getName(), e);
            }
        } catch (IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to create config " + file.getName(), e);
        }
    }

    public void load() {
        if (this.file.exists()) {
            try (final FileReader fr = new FileReader(this.file)) {
                load0(GSON.fromJson(fr, this.nodeType));
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to load config " + this.file.getName(), e);
            }
        }
    }

    public abstract T save0();
    public abstract void load0(final T mainNode);

}
