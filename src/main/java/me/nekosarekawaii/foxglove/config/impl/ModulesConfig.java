package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.Config;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

import java.io.IOException;

/**
 * The ModulesConfig class represents the configuration for the modules in the Foxglove mod.
 * It extends the Config class and provides methods to save and load the module configurations.
 */
public class ModulesConfig extends Config {

    public ModulesConfig() {
        super(Foxglove.getInstance().getDir(), "modules");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject();
        final JsonArray moduleArray = new JsonArray();

        for (final Module module : Foxglove.getInstance().getFeatures().getModules()) {
            final JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("name", module.getName());
            moduleObject.addProperty("enabled", module.isEnabled());

            final JsonArray valuesArray = new JsonArray();

            for (final Value<?> value : module.getValues()) {
                final JsonObject valueObject = new JsonObject();
                valueObject.addProperty("name", value.getHashIdent());

                value.onConfigSave(valueObject);

                valuesArray.add(valueObject);
            }

            moduleObject.add("values", valuesArray);
            moduleArray.add(moduleObject);
        }

        configObject.add("modules", moduleArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final JsonArray moduleArray = jsonObject.getAsJsonArray("modules");

        for (final JsonElement moduleElement : moduleArray) {
            final JsonObject moduleObject = moduleElement.getAsJsonObject();
            final String moduleName = moduleObject.get("name").getAsString();
            final Module module = Foxglove.getInstance().getFeatures().getModules().get(moduleName);

            if (module != null) {
                final JsonArray valuesArray = moduleObject.getAsJsonArray("values");

                for (final JsonElement valueElement : valuesArray) {
                    final JsonObject valueObject = valueElement.getAsJsonObject();
                    final String valueName = valueObject.get("name").getAsString();
                    final Value<?> value = module.getValue(valueName);

                    if (value == null) {
                        Foxglove.getInstance().getLogger().error("Couldnt find value: " + valueName);
                        continue;
                    }

                    module.getValue(valueName).onConfigLoad(valueObject);
                }

                module.setState(moduleObject.get("enabled").getAsBoolean());
            } else
                Foxglove.getInstance().getLogger().error("Unimplemented module inside the Modules Config: " + moduleName);
        }
    }

}
