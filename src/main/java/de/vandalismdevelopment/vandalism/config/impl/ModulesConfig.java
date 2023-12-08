package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;

import java.io.File;
import java.io.IOException;

public class ModulesConfig extends ValueableConfig {

    public ModulesConfig(final File dir) {
        super(dir, "modules");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject modulesObject = new JsonObject();
        for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
            final JsonObject moduleObject = new JsonObject();
            if (!module.getValues().isEmpty()) {
                final JsonObject valuesObject = new JsonObject();
                this.saveValues(valuesObject, module.getValues());
                moduleObject.add("values", valuesObject);
            }
            modulesObject.add(module.getName(), moduleObject);
            Vandalism.getInstance().getLogger().info("Module " + module.getName() + " has been saved.");
        }
        return modulesObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
            final JsonObject moduleObject = jsonObject.getAsJsonObject(module.getName());
            if (moduleObject != null) {
                final JsonElement valuesElement = moduleObject.get("values");
                if (valuesElement != null) {
                    this.loadValues(valuesElement.getAsJsonObject(), module.getValues());
                }
            } else {
                Vandalism.getInstance().getLogger().error("Module " + module.getName() + " not found in config!");
            }
        }
    }

}
