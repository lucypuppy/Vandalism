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

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.Packet;

public interface OutgoingPacketListener {

    void onOutgoingPacket(final OutgoingPacketEvent event);

    class OutgoingPacketEvent extends CancellableEvent<OutgoingPacketListener> {

        public static final int ID = 1;

        public Packet<?> packet;
        public final NetworkPhase networkPhase;
        public final ClientConnection connection;

        public OutgoingPacketEvent(final Packet<?> packet, final NetworkPhase networkPhase, final ClientConnection connection) {
            this.packet = packet;
            this.networkPhase = networkPhase;
            this.connection = connection;
        }

        @Override
        public void call(final OutgoingPacketListener listener) {
            listener.onOutgoingPacket(this);
        }

    }

}
