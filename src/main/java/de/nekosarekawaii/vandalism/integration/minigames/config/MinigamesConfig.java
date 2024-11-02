/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.minigames.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.Config;
import de.nekosarekawaii.vandalism.integration.minigames.Minigame;
import de.nekosarekawaii.vandalism.integration.minigames.MinigamesManager;

public class MinigamesConfig extends Config<JsonObject> {

    private final MinigamesManager minigamesManager;

    public MinigamesConfig(final MinigamesManager minigamesManager) {
        super(JsonObject.class, "minigames");
        this.minigamesManager = minigamesManager;
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        final JsonArray minigamesNode = new JsonArray();
        for (final Minigame minigame : this.minigamesManager.getList()) {
            final JsonObject minigameNode = new JsonObject();
            minigameNode.addProperty("name", minigame.getName());
            final JsonObject configNode = new JsonObject();
            minigame.save(configNode);
            minigameNode.add("config", configNode);
            minigamesNode.add(minigameNode);
        }
        mainNode.add("minigames", minigamesNode);
        final Minigame minigame = this.minigamesManager.getCurrentMinigame();
        if (minigame != null) {
            mainNode.addProperty("lastMinigame", minigame.getName());
        }
        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        if (mainNode.has("minigames")) {
            final JsonArray minigamesNode = mainNode.get("minigames").getAsJsonArray();
            minigamesNode.asList().stream().map(JsonElement::getAsJsonObject).forEach(accountNode -> {
                final String name = accountNode.get("name").getAsString();
                final Minigame minigame = this.minigamesManager.getByName(name);
                if (minigame != null) {
                    minigame.load(accountNode.get("config").getAsJsonObject());
                }
            });
        }
        if (mainNode.has("lastMinigame")) {

        }
    }

}
