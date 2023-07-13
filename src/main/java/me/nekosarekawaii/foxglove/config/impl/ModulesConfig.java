package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.ValueableConfig;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;

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
            } else {
                Foxglove.getInstance().getLogger().error("Module " + module.getName() + " not found in config!");
            }
        }
    }

}
