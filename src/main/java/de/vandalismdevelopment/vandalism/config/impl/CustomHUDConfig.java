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
            elementObject.addProperty("absoluteX", element.absoluteX);
            elementObject.addProperty("absoluteY", element.absoluteY);
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
                if (elementObject.has("absoluteX") && elementObject.has("absoluteY")) {
                    element.absoluteX = elementObject.get("absoluteX").getAsInt();
                    element.absoluteY = elementObject.get("absoluteY").getAsInt();
                    element.calculateAlignment();
                    element.calculatePosition();
                } else {
                    Vandalism.getInstance().getLogger().error("Element " + element.getName() + " has no absolute position in the config!");
                }
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
