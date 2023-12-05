package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.gui.ingame.Element;

import java.io.File;
import java.io.IOException;

public class CustomHUDConfig extends ValueableConfig {

    public CustomHUDConfig(final File dir) {
        super(dir, "customhud");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject elementsObject = new JsonObject();
        for (final Element element : Vandalism.getInstance().getCustomHUDRenderer().getElements()) {
            final JsonObject elementObject = new JsonObject();
            elementObject.addProperty("x", element.x);
            elementObject.addProperty("y", element.y);
            if (!element.getValues().isEmpty()) {
                final JsonObject valuesObject = new JsonObject();
                this.saveValues(valuesObject, element.getValues());
                elementObject.add("values", valuesObject);
            }
            elementsObject.add(element.getName(), elementObject);
        }
        return elementsObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        for (final Element element : Vandalism.getInstance().getCustomHUDRenderer().getElements()) {
            final JsonObject elementObject = jsonObject.getAsJsonObject(element.getName());
            if (elementObject != null) {
                element.x = elementObject.get("x").getAsInt();
                element.y = elementObject.get("y").getAsInt();
                element.calculateAlignment();
                final JsonElement valuesElement = elementObject.get("values");
                if (valuesElement != null) {
                    this.loadValues(valuesElement.getAsJsonObject(), element.getValues());
                }
            } else {
                Vandalism.getInstance().getLogger().error("Element " + element.getName() + " not found in config!");
            }
        }
    }

}
