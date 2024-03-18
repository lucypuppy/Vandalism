/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.clientmenu.config;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;

public class ClientMenuConfig extends AbstractConfig<JsonObject> {

    private final ClientMenuManager clientMenuManager;

    public ClientMenuConfig(final ClientMenuManager clientMenuManager) {
        super(JsonObject.class, "client-menu");
        this.clientMenuManager = clientMenuManager;
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
            mainNode.addProperty(window.getName(), window.isActive());
        }
        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
            final String windowName = window.getName();
            if (mainNode.has(windowName)) {
                window.setActive(mainNode.get(windowName).getAsBoolean());
            }
        }
    }

}
