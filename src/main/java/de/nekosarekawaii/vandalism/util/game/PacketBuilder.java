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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.Deflater;

public class PacketBuilder {

    private final ByteArrayOutputStream baos;
    private final DataOutputStream output;

    private PacketBuilder(int id) {
        this.output = new DataOutputStream(baos = new ByteArrayOutputStream());
        this.putVarInt(id);
    }

    public static PacketBuilder byId(int id) {
        return new PacketBuilder(id);
    }

    public byte[] build() {
        return baos.toByteArray();
    }

    public static byte[] readByteArray(final ByteBuf in) {
        final int len = readVarInt(in);
        final byte[] data = new byte[len];
        in.readBytes(data);
        return data;
    }

    public static int readVarInt(final ByteBuf in) {
        int i = 0;
        int j = 0;
        int k;
        do {
            k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((k & 0x80) == 0x80);
        return i;
    }

    public static String readString(final ByteBuf in) {
        final int len = readVarInt(in);
        final byte[] data = new byte[len];
        in.readBytes(data);
        return new String(data, 0, len, StandardCharsets.UTF_8);
    }

    public PacketBuilder putBoolean(boolean value) {
        try {
            output.writeBoolean(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putByte(int value) {
        try {
            output.writeByte(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putShort(int value) {
        try {
            output.writeShort(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putChar(char value) {
        try {
            output.writeChar(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putInt(int value) {
        try {
            output.writeInt(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putLong(long value) {
        try {
            output.writeLong(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putFloat(float value) {
        try {
            output.writeFloat(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putDouble(double value) {
        try {
            output.writeDouble(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    private void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        out.writeByte(value);
    }

    public static void writeVarInt(ByteBuf out, int value) {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        out.writeByte(value);
    }

    public static void writeString(final ByteBuf out, final String value) {
        final byte[] data = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, data.length);
        out.writeBytes(data, 0, data.length);
    }

    public PacketBuilder putVarInt(int value) {
        try {
            this.writeVarInt(output, value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putVarLong(long value) {
        try {
            while ((value & -128L) != 0L) {
                this.output.writeByte((int) (value & 127L) | 128);
                value >>>= 7;
            }

            this.output.writeByte((int) value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder put(byte[] value) {
        try {
            this.output.write(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putByteArray(byte[] value) {
        try {
            this.writeVarInt(output, value.length);
            this.output.write(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putShortArray(short... values) {
        try {
            this.putVarInt(values.length);
            for (short value : values) {
                this.output.writeShort(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putIntArray(int... values) {
        try {
            this.putVarInt(values.length);
            for (int value : values) {
                this.output.writeInt(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putLongArray(long... values) {
        try {
            this.putVarInt(values.length);
            for (long value : values) {
                this.output.writeLong(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putFloatArray(float... values) {
        try {
            this.putVarInt(values.length);
            for (float value : values) {
                this.output.writeFloat(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putDoubleArray(double... values) {
        try {
            this.putVarInt(values.length);
            for (double value : values) {
                this.output.writeDouble(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putVarIntArray(int... values) {
        try {
            this.putVarInt(values.length);
            for (int value : values) {
                this.putVarInt(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putVarLongArray(long... values) {
        try {
            this.putVarInt(values.length);
            for (long value : values) {
                this.putVarLong(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putStringArray(String... values) {
        try {
            this.putVarInt(values.length);
            for (String value : values) {
                this.putString(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public <T> PacketBuilder putArray(T[] values, Serializer<T> serializer) {
        try {
            this.putVarInt(values.length);
            for (T value : values) {
                serializer.serialize(this, value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putUTF(String value) {
        try {
            this.output.writeUTF(value);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putString(String value) {
        this.putByteArray(value.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public PacketBuilder putUuid(UUID value) {
        try {
            this.output.writeLong(value.getMostSignificantBits());
            this.output.writeLong(value.getLeastSignificantBits());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public PacketBuilder putVarIntEnum(Enum<?> value) {
        this.putVarInt(value.ordinal());
        return this;
    }

    public static PacketBuilder buildHandshakePacket(int protocol, String host, int port, int state) {
        return PacketBuilder.byId(0x00)
                .putVarInt(protocol)
                .putString(host)
                .putShort(port)
                .putVarInt(state);
    }

    public static PacketBuilder buildLoginStartPacket(String username) {
        return PacketBuilder.byId(0x00).putString(username);
    }

    public byte[] buildEncapsulated(boolean compressed) {
        try {
            final byte[] content = this.build();
            int len = content.length;
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream output = new DataOutputStream(baos);

            if (compressed) {
                if (content.length > 0) {
                    final Deflater deflater = new Deflater();
                    deflater.setInput(content);
                    len = deflater.deflate(content);
                    deflater.end();
                    this.writeVarInt(output, len);
                } else {
                    this.writeVarInt(output, 0);
                }
            }

            this.writeVarInt(output, content.length);
            output.write(content, 0, len);
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] buildEncapsulated(int threshold) {
        try {
            final byte[] content = this.build();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream output = new DataOutputStream(baos);

            int i = content.length;

            if (i < threshold)
            {
                writeVarInt(output, 0);
                output.write(content);
            }
            else
            {
                writeVarInt(output, content.length);
                Deflater deflater = new Deflater();
                deflater.setInput(content, 0, i);
                deflater.finish();

                byte[] buffer = new byte[1024];

                while (!deflater.finished())
                {
                    int j = deflater.deflate(buffer);
                    output.write(buffer, 0, j);
                }

                deflater.reset();
            }
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public interface Serializer<T> {

        void serialize(PacketBuilder builder, T value) throws Exception;
    }
}