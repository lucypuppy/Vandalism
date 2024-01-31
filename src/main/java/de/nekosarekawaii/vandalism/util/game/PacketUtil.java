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

package de.nekosarekawaii.vandalism.util.game;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

import java.util.concurrent.RejectedExecutionException;

public class PacketUtil {

    public static void recievePacket(final ClientPlayNetworkHandler handler, final Packet<?> packet) {
        if (handler.isConnectionOpen()) {
            final ClientConnection connection = handler.getConnection();
            final PacketListener packetListener = connection.getPacketListener();

            if (packetListener == null) {
                throw new IllegalStateException("Received a packet before the packet listener was initialized");
            }

            if (packetListener.accepts(packet)) {
                try {
                    ClientConnection.handlePacket(packet, packetListener);
                } catch (OffThreadException e) {
                } catch (RejectedExecutionException e) {
                    connection.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
                } catch (ClassCastException e) {
                    connection.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
                }

                connection.packetsReceivedCounter++;
            }
        }
    }

}
