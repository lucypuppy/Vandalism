package de.vandalismdevelopment.vandalism.base.config.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.config.AbstractConfig;
import de.vandalismdevelopment.vandalism.base.config.template.ConfigWithValues;
import de.vandalismdevelopment.vandalism.integration.hud.HUDManager;
import de.vandalismdevelopment.vandalism.integration.hud.HUDElement;

public class HUDConfig extends AbstractConfig<JsonObject> {

    private final HUDManager hudManager;

    public HUDConfig(final HUDManager hudManager) {
        super(JsonObject.class, "hud");

        this.hudManager = hudManager;
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        for (final HUDElement hudElement : hudManager.getHudElements()) {
            final JsonObject hudElementNode = new JsonObject();

            hudElementNode.addProperty("x", hudElement.x);
            hudElementNode.addProperty("y", hudElement.y);

            if (!hudElement.getValues().isEmpty()) {
                final JsonObject valuesNode = new JsonObject();

                ConfigWithValues.saveValues(valuesNode, hudElement.getValues());
                hudElementNode.add("values", valuesNode);
            }

            mainNode.add(hudElement.getName(), hudElementNode);
        }

        return mainNode;
    }

    @Override
    public void load0(JsonObject node) {
        for (final HUDElement hudElement : hudManager.getHudElements()) {
            if (!node.has(hudElement.getName())) {
                continue;
            }
            final var hudElementNode = node.getAsJsonObject(hudElement.getName());
            hudElement.x = hudElementNode.get("x").getAsInt();
            hudElement.y = hudElementNode.get("y").getAsInt();

            if (hudElementNode.has("values")) {
                final JsonObject valuesNode = hudElementNode.get("values").getAsJsonObject();

                ConfigWithValues.loadValues(valuesNode, hudElement.getValues());
            }
        }
    }

}
