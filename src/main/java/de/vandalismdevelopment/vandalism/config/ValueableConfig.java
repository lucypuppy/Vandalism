package de.vandalismdevelopment.vandalism.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;

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
            if (value == null) continue;

            final JsonObject valueObject = new JsonObject();

            if (value instanceof ValueCategory) {
                this.saveValues(valueObject, ((ValueCategory) value).getValues());
            } else {
                value.onConfigSave(valueObject);
            }

            valuesArray.add(value.getSaveIdentifier(), valueObject);
        }
    }

    protected void loadValues(final JsonObject valuesArray, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            final JsonElement valueElement = valuesArray.get(value.getSaveIdentifier());

            if (valueElement == null) {
                Vandalism.getInstance().getLogger().error("Value " + value.getSaveIdentifier() + " not found in config!");
                continue;
            }

            if (value instanceof ValueCategory) {
                this.loadValues(valueElement.getAsJsonObject(), ((ValueCategory) value).getValues());
            } else {
                value.onConfigLoad(valueElement.getAsJsonObject());
            }
        }
    }

    @Override
    public String getValueName() {
        return this.getName();
    }

}
