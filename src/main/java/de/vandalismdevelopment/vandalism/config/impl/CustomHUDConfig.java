package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.gui.ingame.CustomHUDRenderer;
import de.vandalismdevelopment.vandalism.gui.ingame.HUDElement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CustomHUDConfig extends ValueableConfig {

    public CustomHUDConfig(final File dir) {
        super(dir, "customhud");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject elementsObject = new JsonObject();
        final CustomHUDRenderer customHUDRenderer = Vandalism.getInstance().getCustomHUDRenderer();
        if (customHUDRenderer == null) return elementsObject;
        final List<HUDElement> hudElements = customHUDRenderer.getHudElements();
        if (hudElements == null || hudElements.isEmpty()) return elementsObject;
        for (final HUDElement hudElement : hudElements) {
            final JsonObject elementObject = new JsonObject();
            elementObject.addProperty("absoluteX", hudElement.absoluteX);
            elementObject.addProperty("absoluteY", hudElement.absoluteY);
            if (!hudElement.getValues().isEmpty()) {
                final JsonObject valuesObject = new JsonObject();
                this.saveValues(valuesObject, hudElement.getValues());
                elementObject.add("values", valuesObject);
            }
            elementsObject.add(hudElement.getName(), elementObject);
            Vandalism.getInstance().getLogger().info("HUD Element " + hudElement.getName() + " has been saved.");
        }
        return elementsObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final CustomHUDRenderer customHUDRenderer = Vandalism.getInstance().getCustomHUDRenderer();
        if (customHUDRenderer == null) return;
        final List<HUDElement> hudElements = customHUDRenderer.getHudElements();
        if (hudElements == null || hudElements.isEmpty()) return;
        for (final HUDElement hudElement : hudElements) {
            final JsonObject elementObject = jsonObject.getAsJsonObject(hudElement.getName());
            if (elementObject != null) {
                if (elementObject.has("absoluteX") && elementObject.has("absoluteY")) {
                    hudElement.updateScreenPosition(
                            elementObject.get("absoluteX").getAsDouble(),
                            elementObject.get("absoluteY").getAsDouble()
                    );
                } else {
                    Vandalism.getInstance().getLogger().error("HUD Element " + hudElement.getName() + " has no absolute position in the config!");
                }
                final JsonElement valuesElement = elementObject.get("values");
                if (valuesElement != null) {
                    this.loadValues(valuesElement.getAsJsonObject(), hudElement.getValues());
                }
            } else {
                Vandalism.getInstance().getLogger().error("HUD Element " + hudElement.getName() + " not found in config!");
            }
        }
    }

}
