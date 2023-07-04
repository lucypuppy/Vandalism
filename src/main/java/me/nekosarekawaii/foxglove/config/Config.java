package me.nekosarekawaii.foxglove.config;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;

import java.io.File;
import java.io.IOException;


public abstract class Config implements IValue {

    private final ObjectArrayList<Value<?>> values;

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
        this.values = new ObjectArrayList<>();
    }

    public abstract JsonObject save() throws IOException;

    public abstract void load(final JsonObject jsonObject) throws IOException;

    @Override
    public ObjectArrayList<Value<?>> getValues() {
        return this.values;
    }

}
