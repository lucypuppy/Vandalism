/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.hud.config;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.Config;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.feature.hud.HUDManager;

public class HUDConfig extends Config<JsonObject> {

    private final HUDManager hudManager;

    public HUDConfig(final HUDManager hudManager) {
        super(JsonObject.class, "hud");
        this.hudManager = hudManager;
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        for (final HUDElement hudElement : this.hudManager.getList()) {
            final String hudElementName = hudElement.getName();
            try {
                final JsonObject hudElementNode = new JsonObject();
                if (!hudElement.getValues().isEmpty()) {
                    final JsonObject valuesNode = new JsonObject();
                    ConfigWithValues.saveValues(valuesNode, hudElement.getValues());
                    hudElementNode.add("values", valuesNode);
                }
                mainNode.add(hudElementName, hudElementNode);
            }
            catch (Throwable t) {
                Vandalism.getInstance().getLogger().error("Failed to save the HUD element: {}", hudElementName, t);
            }
        }
        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        for (final HUDElement hudElement : this.hudManager.getList()) {
            final String hudElementName = hudElement.getName();
            try {
                if (!mainNode.has(hudElementName)) {
                    continue;
                }
                final JsonObject hudElementNode = mainNode.getAsJsonObject(hudElementName);
                if (hudElementNode.has("values")) {
                    final JsonObject valuesNode = hudElementNode.get("values").getAsJsonObject();
                    ConfigWithValues.loadValues(valuesNode, hudElement.getValues());
                }
            }
            catch (Throwable t) {
                Vandalism.getInstance().getLogger().error("Failed to load the HUD element: {}", hudElementName, t);
            }
        }
    }

}
