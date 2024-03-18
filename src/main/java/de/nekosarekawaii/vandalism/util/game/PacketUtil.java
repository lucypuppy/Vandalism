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

package de.nekosarekawaii.vandalism.util.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.RejectedExecutionException;

public class PacketUtil {

    public static void receivePacket(final Packet<?> packet) {
        final ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

        if (handler != null && handler.isConnectionOpen()) {
            final ClientConnection connection = handler.getConnection();
            final PacketListener packetListener = connection.getPacketListener();
            if (packetListener == null) {
                throw new IllegalStateException("Received a packet before the packet listener was initialized");
            }
            if (packetListener.accepts(packet)) {
                try {
                    ClientConnection.handlePacket(packet, packetListener);
                } catch (OffThreadException ignored) {
                } catch (RejectedExecutionException e) {
                    connection.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
                } catch (ClassCastException e) {
                    connection.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
                }
                connection.packetsReceivedCounter++;
            }
        }
    }

    public static void sendImmediately(final Packet<?> packet, final @Nullable PacketCallbacks callbacks, final boolean flush) {
        final ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

        if (handler != null && handler.isConnectionOpen()) {
            final ClientConnection connection = handler.getConnection();

            connection.packetsSentCounter++;
            if (connection.channel.eventLoop().inEventLoop()) {
                connection.sendInternal(packet, callbacks, flush);
            } else {
                connection.channel.eventLoop().execute(() -> {
                    connection.sendInternal(packet, callbacks, flush);
                });
            }
        }
    }

    public static byte[] readBuffer(final ByteBuf buf) {
        final byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

}
