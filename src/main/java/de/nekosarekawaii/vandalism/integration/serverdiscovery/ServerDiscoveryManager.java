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

package de.nekosarekawaii.vandalism.integration.serverdiscovery;

import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.Request;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.gui.player.PlayerDiscoveryClientWindow;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.gui.server.ServerDiscoveryClientWindow;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.gui.server.ServerInfoClientWindow;

public class ServerDiscoveryManager {

    private static final String API_KEY = "LVQrwaRXJWlpIwnfPK3V89fAqIXQBiLe";

    public ServerDiscoveryManager(final ClientWindowManager clientWindowManager) {
        clientWindowManager.add(
                new ServerInfoClientWindow(),
                new ServerDiscoveryClientWindow(),
                new PlayerDiscoveryClientWindow()
        );
    }

    public Response request(final Request<?> request) {
        return request.send(API_KEY);
    }

}
