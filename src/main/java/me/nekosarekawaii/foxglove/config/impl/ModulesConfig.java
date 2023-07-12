package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.ValueableConfig;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.ValueCategory;

import java.io.IOException;

public class ModulesConfig extends ValueableConfig {

    public ModulesConfig() {
        super(Foxglove.getInstance().getDir(), "modules");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject modulesObject = new JsonObject();

        for (final Module module : Foxglove.getInstance().getModuleRegistry().getModules()) {
            final JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("enabled", module.isEnabled());

            if (!module.getValues().isEmpty()) {
                final JsonObject valuesObject = new JsonObject();
                saveValues(valuesObject, module.getValues());
                moduleObject.add("values", valuesObject);
            }

            modulesObject.add(module.getName(), moduleObject);
        }

        return modulesObject;
    }

    private void saveValues(final JsonObject valuesArray, final ObjectArrayList<Value<?>> values) {
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

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        for (final Module module : Foxglove.getInstance().getModuleRegistry().getModules()) {
            final JsonObject moduleObject = jsonObject.getAsJsonObject(module.getName());

            if (moduleObject != null) {
                module.setState(moduleObject.get("enabled").getAsBoolean());

                final JsonElement valuesElement = moduleObject.get("values");
                if (valuesElement != null) {
                    loadValues(valuesElement.getAsJsonObject(), module.getValues());
                }
            }
        }
    }

    private void loadValues(final JsonObject valuesArray, final ObjectArrayList<Value<?>> values) {
        for (final Value<?> value : values) {
            final JsonElement valueElement = valuesArray.get(value.getHashIdent());

            if (valueElement == null) {
                System.out.println("Value " + value.getName() + " not found in config!");
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
