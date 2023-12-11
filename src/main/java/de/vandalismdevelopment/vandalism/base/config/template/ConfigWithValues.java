package de.vandalismdevelopment.vandalism.base.config.template;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.config.AbstractConfig;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;

import java.util.List;

public class ConfigWithValues extends AbstractConfig<JsonObject> {

    private final List<? extends ValueParent> keys;

    public ConfigWithValues(String name, final List<? extends ValueParent> keys) {
        super(JsonObject.class, name);

        this.keys = keys;
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        for (ValueParent key : this.keys) {
            final var keyNode = new JsonObject();
            saveValues(keyNode, key.getValues());

            mainNode.add(key.getName(), keyNode);
        }
        return mainNode;
    }

    @Override
    public void load0(JsonObject node) {
        for (ValueParent key : this.keys) {
            final var keyNode = node.getAsJsonObject(key.getName());
            if (keyNode != null) {
                loadValues(keyNode, key.getValues());
            }
        }
    }

    public static void saveValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            final var valueNode = new JsonObject();

            if (value instanceof final ValueGroup valueGroup) {
                // Save the values of the category recursively
                saveValues(valueNode, valueGroup.getValues());
            } else {
                // Save the value
                value.save(valueNode);
            }

            // Add the value to the target node
            targetNode.add(value.getName(), valueNode);
        }
    }

    public static void loadValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            if (!targetNode.has(value.getName())) {
                continue;
            }
            final JsonObject valueNode = targetNode.get(value.getName()).getAsJsonObject();

            if (value instanceof final ValueGroup valueGroup) {
                loadValues(valueNode, valueGroup.getValues());
            } else {
                value.load(valueNode);
            }
        }
    }

}
