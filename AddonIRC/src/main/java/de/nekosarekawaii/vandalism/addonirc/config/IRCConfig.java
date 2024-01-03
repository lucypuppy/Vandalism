/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.addonirc.config;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.addonirc.clientmenu.IrcClientMenuWindow;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;
import imgui.type.ImString;

import java.util.HashMap;

public class IRCConfig extends AbstractConfig<JsonObject> {

    private final HashMap<String, ImString> values = new HashMap<>();

    public IRCConfig() {
        super(JsonObject.class, "irc");

        this.values.put("address", IrcClientMenuWindow.ADDRESS);
        this.values.put("username", IrcClientMenuWindow.USERNAME);
        this.values.put("password", IrcClientMenuWindow.PASSWORD);
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        this.values.forEach((key, value) -> {
            mainNode.addProperty(key, value.get());
        });

        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        this.values.forEach((key, value) -> {
            if (mainNode.has(key)) {
                value.set(mainNode.get(key).getAsString());
            }
        });
    }

}
