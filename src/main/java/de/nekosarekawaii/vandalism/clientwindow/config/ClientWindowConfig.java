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

package de.nekosarekawaii.vandalism.clientwindow.config;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.Config;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;

public class ClientWindowConfig extends Config<JsonObject> {

    private final ClientWindowManager clientWindowManager;

    public ClientWindowConfig(final ClientWindowManager clientWindowManager) {
        super(JsonObject.class, "windows");
        this.clientWindowManager = clientWindowManager;
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            mainNode.addProperty(window.getName(), window.isActive());
        }
        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            final String windowName = window.getName();
            if (mainNode.has(windowName)) {
                window.setActive(mainNode.get(windowName).getAsBoolean());
            }
        }
    }

}
