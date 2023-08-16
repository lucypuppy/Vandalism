package de.nekosarekawaii.foxglove.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.value.IValue;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.ValueCategory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ValueableConfig extends Config implements IValue {

    private final List<Value<?>> values;

    public ValueableConfig(final File configDir, final String name) {
        super(configDir, name);
        this.values = new ArrayList<>();
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public Config getConfig() {
        return this;
    }

    protected void saveValues(final JsonObject valuesArray, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            final JsonObject valueObject = new JsonObject();

            if (value instanceof ValueCategory) {
                this.saveValues(valueObject, ((ValueCategory) value).getValues());
            } else {
                value.onConfigSave(valueObject);
            }

            valuesArray.add(value.getHashIdent(), valueObject);
        }
    }

    protected void loadValues(final JsonObject valuesArray, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            final JsonElement valueElement = valuesArray.get(value.getHashIdent());

            if (valueElement == null) {
                Foxglove.getInstance().getLogger().error("Value " + value.getName() + " not found in config!");
                continue;
            }

            if (value instanceof ValueCategory) {
                this.loadValues(valueElement.getAsJsonObject(), ((ValueCategory) value).getValues());
            } else {
                value.onConfigLoad(valueElement.getAsJsonObject());
            }
        }
    }

}
