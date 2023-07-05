package me.nekosarekawaii.foxglove.config;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;

import java.io.File;

public abstract class ValueableConfig extends Config implements IValue {

    private final ObjectArrayList<Value<?>> values;

    public ValueableConfig(final File configDir, final String name) {
        super(configDir, name);
        this.values = new ObjectArrayList<>();
    }

    @Override
    public ObjectArrayList<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public Config getConfig() {
        return this;
    }

}
