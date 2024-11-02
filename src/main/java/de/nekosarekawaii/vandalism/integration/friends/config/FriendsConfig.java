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

package de.nekosarekawaii.vandalism.integration.friends.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.Config;
import de.nekosarekawaii.vandalism.integration.friends.Friend;
import de.nekosarekawaii.vandalism.integration.friends.FriendsManager;

public class FriendsConfig extends Config<JsonObject> {

    private final FriendsManager friendsManager;

    public FriendsConfig(final FriendsManager friendsManager) {
        super(JsonObject.class, "friends");
        this.friendsManager = friendsManager;
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        final JsonArray friendsNode = new JsonArray();
        for (final Friend friend : this.friendsManager.getList()) {
            final JsonObject friendNode = new JsonObject();
            friendNode.addProperty("name", friend.getName());
            friendNode.addProperty("alias", friend.getAlias());
            friendsNode.add(friendNode);
        }
        mainNode.add("friends", friendsNode);
        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        if (!mainNode.has("friends")) return;
        final JsonArray friendsNode = mainNode.getAsJsonArray("friends");
        for (int i = 0; i < friendsNode.size(); i++) {
            try {
                final JsonObject friendNode = friendsNode.get(i).getAsJsonObject();
                final String name = friendNode.get("name").getAsString();
                final String alias = friendNode.get("alias").getAsString();
                this.friendsManager.add(new Friend(name, alias));
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to load friend from config.", e);
            }
        }
    }

}
