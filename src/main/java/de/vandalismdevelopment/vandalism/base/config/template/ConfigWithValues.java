package de.vandalismdevelopment.vandalism.base.config.template;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.config.AbstractConfig;
import de.vandalismdevelopment.vandalism.base.value.IValue;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueCategory;

import java.util.List;

public class ConfigWithValues extends AbstractConfig<JsonObject> {

    private final List<? extends IValue> keys;

    public ConfigWithValues(String name, final List<? extends IValue> keys) {
        super(JsonObject.class, name);

        this.keys = keys;
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        for (IValue key : this.keys) {
            final var keyNode = new JsonObject();
            saveValues(keyNode, key.getValues());

            mainNode.add(key.getValueName(), keyNode);
        }
        return mainNode;
    }

    @Override
    public void load0(JsonObject node) {
        for (IValue key : this.keys) {
            final var keyNode = node.getAsJsonObject(key.getValueName());
            if (keyNode != null) {
                loadValues(keyNode, key.getValues());
            }
        }
    }

    public static void saveValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            final var valueNode = new JsonObject();

            if (value instanceof final ValueCategory valueCategory) {
                // Save the values of the category recursively
                saveValues(valueNode, valueCategory.getValues());
            } else {
                // Save the value
                value.onConfigSave(valueNode);
            }

            // Add the value to the target node
            targetNode.add(value.getSaveIdentifier(), valueNode);
        }
    }

    public static void loadValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            if (!targetNode.has(value.getSaveIdentifier())) {
                continue;
            }
            final JsonObject valueNode = targetNode.get(value.getSaveIdentifier()).getAsJsonObject();

            if (value instanceof final ValueCategory valueCategory) {
                loadValues(valueNode, valueCategory.getValues());
            } else {
                value.onConfigLoad(valueNode);
            }
        }
    }

}
