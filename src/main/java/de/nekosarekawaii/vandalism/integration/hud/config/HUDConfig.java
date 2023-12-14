package de.nekosarekawaii.vandalism.integration.hud.config;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.integration.hud.HUDManager;

public class HUDConfig extends AbstractConfig<JsonObject> {

    private final HUDManager hudManager;

    public HUDConfig(final HUDManager hudManager) {
        super(JsonObject.class, "hud");

        this.hudManager = hudManager;
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        for (final HUDElement hudElement : hudManager.getList()) {
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
    public void load0(JsonObject mainNode) {
        for (final HUDElement hudElement : hudManager.getList()) {
            if (!mainNode.has(hudElement.getName())) {
                continue;
            }
            final var hudElementNode = mainNode.getAsJsonObject(hudElement.getName());
            hudElement.x = hudElementNode.get("x").getAsInt();
            hudElement.y = hudElementNode.get("y").getAsInt();

            if (hudElementNode.has("values")) {
                final JsonObject valuesNode = hudElementNode.get("values").getAsJsonObject();

                ConfigWithValues.loadValues(valuesNode, hudElement.getValues());
            }
        }
    }

}
