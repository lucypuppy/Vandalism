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
            moduleObject.addProperty("enabled", module.isEnabled());
            moduleObject.addProperty("showInModuleList", module.isShowInModuleList());
            if (!module.getValues().isEmpty()) {
                final JsonObject valuesObject = new JsonObject();
                this.saveValues(valuesObject, module.getValues());
                moduleObject.add("values", valuesObject);
            }
            modulesObject.add(module.getName(), moduleObject);
        }
        return modulesObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
            final JsonObject moduleObject = jsonObject.getAsJsonObject(module.getName());
            if (moduleObject != null) {
                if (moduleObject.has("enabled")) {
                    module.setState(moduleObject.get("enabled").getAsBoolean());
                }
                if (moduleObject.has("showInModuleList")) {
                    module.setShowInModuleList(moduleObject.get("showInModuleList").getAsBoolean());
                }
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
