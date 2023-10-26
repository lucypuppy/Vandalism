package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.feature.impl.script.Script;

import java.io.File;
import java.io.IOException;

public class ScriptConfig extends ValueableConfig {

    public ScriptConfig(final File dir) {
        super(dir, "scripts");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject scriptsObject = new JsonObject();
        for (final Script script : Vandalism.getInstance().getScriptRegistry().getScripts()) {
            final JsonObject scriptObject = new JsonObject();
            if (!script.getValues().isEmpty()) {
                final JsonObject valuesObject = new JsonObject();
                this.saveValues(valuesObject, script.getValues());
                scriptObject.add("values", valuesObject);
            }
            scriptsObject.add(script.getName(), scriptObject);
        }
        return scriptsObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        for (final Script script : Vandalism.getInstance().getScriptRegistry().getScripts()) {
            final JsonObject scriptObject = jsonObject.getAsJsonObject(script.getName());
            if (scriptObject != null) {
                final JsonElement valuesElement = scriptObject.get("values");
                if (valuesElement != null) {
                    this.loadValues(valuesElement.getAsJsonObject(), script.getValues());
                }
            } else {
                Vandalism.getInstance().getLogger().error("Script " + script.getName() + " not found in config!");
            }
        }
    }

}
