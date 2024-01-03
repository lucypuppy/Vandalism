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

package de.nekosarekawaii.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

public interface OutgoingPacketListener {

    void onOutgoingPacket(final OutgoingPacketEvent event);

    class OutgoingPacketEvent extends CancellableEvent<OutgoingPacketListener> {

        public static final int ID = 12;

        public Packet<?> packet;
        public final NetworkState networkState;

        public OutgoingPacketEvent(final Packet<?> packet, final NetworkState networkState) {
            this.packet = packet;
            this.networkState = networkState;
        }

        @Override
        public void call(final OutgoingPacketListener listener) {
            listener.onOutgoingPacket(this);
        }

    }

}
