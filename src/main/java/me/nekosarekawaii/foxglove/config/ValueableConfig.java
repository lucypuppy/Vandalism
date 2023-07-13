package me.nekosarekawaii.foxglove.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.ValueCategory;

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

    protected void saveValues(final JsonObject valuesArray, final ObjectArrayList<Value<?>> values) {
        for (final Value<?> value : values) {
            final JsonObject valueObject = new JsonObject();

            if (value instanceof ValueCategory) {
                saveValues(valueObject, ((ValueCategory) value).getValues());
            } else {
                value.onConfigSave(valueObject);
            }

            valuesArray.add(value.getHashIdent(), valueObject);
        }
    }

    protected void loadValues(final JsonObject valuesArray, final ObjectArrayList<Value<?>> values) {
        for (final Value<?> value : values) {
            final JsonElement valueElement = valuesArray.get(value.getHashIdent());

            if (valueElement == null) {
                Foxglove.getInstance().getLogger().error("Value " + value.getName() + " not found in config!");
                continue;
            }

            if (value instanceof ValueCategory) {
                loadValues(valueElement.getAsJsonObject(), ((ValueCategory) value).getValues());
            } else {
                value.onConfigLoad(valueElement.getAsJsonObject());
            }
        }
    }

}
