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

package de.nekosarekawaii.vandalism.event.network;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

/**
 * Event that is called once the playing client disconnects from the server.
 */
public interface DisconnectListener {

    void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason);

    class DisconnectEvent extends AbstractEvent<DisconnectListener> {

        public static final int ID = 14;

        private final ClientConnection clientConnection;
        private final Text disconnectReason;

        public DisconnectEvent(final ClientConnection clientConnection, final Text disconnectReason) {
            this.clientConnection = clientConnection;
            this.disconnectReason = disconnectReason;
        }

        @Override
        public void call(DisconnectListener listener) {
            listener.onDisconnect(this.clientConnection, this.disconnectReason);
        }

    }

}
