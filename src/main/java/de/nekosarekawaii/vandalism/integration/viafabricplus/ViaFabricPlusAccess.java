/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.viafabricplus;

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.protocoltranslator.translator.ItemTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import io.netty.buffer.ByteBuf;
import net.lenni0451.reflect.stream.RStream;
import net.lenni0451.reflect.stream.field.FieldWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import static de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator.getPlayNetworkUserConnection;

public class ViaFabricPlusAccess {

    private static final FieldWrapper previousVersion;

    static {
        previousVersion = RStream.of(ProtocolTranslator.class).fields().by("previousVersion");
    }

    private static BlockPosition toPosition(final BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static DataItem writtenBook(final CompoundTag tag) {
        return new DataItem(386, (byte) 1, tag); // assuming no data
    }

    public static void send1_8CustomPayload(final String channel, final ByteBuf data) {
        if (channel.contains(":")) {
            throw new IllegalStateException("Channel name has to be unmapped");
        }
        final PacketWrapper customPayload = PacketWrapper.create(ServerboundPackets1_8.CUSTOM_PAYLOAD, getPlayNetworkUserConnection());
        customPayload.write(Types.STRING, channel);
        customPayload.write(Types.REMAINING_BYTES, data.array());

        customPayload.sendToServer(Protocol1_8To1_9.class);
    }

    public static void send1_8SignUpdatePacket(final BlockPos pos, final String line1, final String line2, final String line3, final String line4) {
        final PacketWrapper signUpdate = PacketWrapper.create(ServerboundPackets1_8.SIGN_UPDATE, getPlayNetworkUserConnection());
        signUpdate.write(Types.BLOCK_POSITION1_8, toPosition(pos));
        signUpdate.write(Types.STRING, line1);
        signUpdate.write(Types.STRING, line2);
        signUpdate.write(Types.STRING, line3);
        signUpdate.write(Types.STRING, line4);

        signUpdate.sendToServer(Protocol1_8To1_9.class);
    }

    public static void send1_8BlockPlacePacket(final BlockPos pos, final int face, final ItemStack item, final float cX, final float cY, final float cZ) {
        final PacketWrapper blockPlacement = PacketWrapper.create(ServerboundPackets1_8.USE_ITEM_ON, getPlayNetworkUserConnection());
        blockPlacement.write(Types.BLOCK_POSITION1_8, toPosition(pos));
        blockPlacement.write(Types.UNSIGNED_BYTE, (short) face);
        blockPlacement.write(Types.ITEM1_8, ItemTranslator.mcToVia(item, ProtocolVersion.v1_8));
        blockPlacement.write(Types.UNSIGNED_BYTE, (short) cX);
        blockPlacement.write(Types.UNSIGNED_BYTE, (short) cY);
        blockPlacement.write(Types.UNSIGNED_BYTE, (short) cZ);

        blockPlacement.sendToServer(Protocol1_8To1_9.class);
    }

    /**
     * Attempts to set the previous version.
     *
     * @param version the previous version
     */
    public static void setPreviousVersion(final ProtocolVersion version) {
        try {
            previousVersion.set(version);
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while attempting to set the previous value.", e);
        }
    }

}
