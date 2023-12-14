package de.nekosarekawaii.vandalism.base.config.template;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;

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
    public void load0(JsonObject mainNode) {
        for (ValueParent key : this.keys) {
            final var keyNode = mainNode.getAsJsonObject(key.getName());
            if (keyNode != null) {
                loadValues(keyNode, key.getValues());
            }
        }
    }

    public static void saveValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            value.save(targetNode);
        }
    }

    public static void loadValues(final JsonObject targetNode, final List<Value<?>> values) {
        for (final Value<?> value : values) {
            if (!targetNode.has(value.getName())) {
                continue;
            }
            value.load(targetNode);
        }
    }

}
