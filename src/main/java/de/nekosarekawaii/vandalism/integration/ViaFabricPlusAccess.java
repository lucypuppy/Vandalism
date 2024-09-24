/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration;

import com.viaversion.nbt.tag.ByteTag;
import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.nbt.tag.ListTag;
import com.viaversion.nbt.tag.StringTag;
import com.viaversion.viaversion.api.connection.UserConnection;
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
import io.netty.buffer.Unpooled;
import net.lenni0451.reflect.stream.RStream;
import net.lenni0451.reflect.stream.field.FieldWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator.getPlayNetworkUserConnection;

public class ViaFabricPlusAccess {

    private static final FieldWrapper previousVersion;

    static {
        previousVersion = RStream.of(ProtocolTranslator.class).fields().by("previousVersion");
    }

    private static BlockPosition toPosition(final BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    /***
     * Creates a 1.8 book item.
     * @param title the title of the book
     * @param author the author of the book
     * @param resolved whether the book is signed or not
     * @param written whether the book is written or not
     * @param pages the pages of the book (max pages in 1.8 are 50)
     * @return the created book item as a ByteBuf
     */
    public static ByteBuf create1_8Book(final String title, final String author, final boolean resolved, final boolean written, final List<String> pages) {
        final ListTag<StringTag> pagesTag = new ListTag<>(StringTag.class);
        for (final String page : pages) {
            pagesTag.add(new StringTag(page));
        }
        final CompoundTag tag = new CompoundTag();
        tag.put("author", new StringTag(author));
        tag.put("title", new StringTag(title));
        tag.put("resolved", new ByteTag(resolved ? (byte) 1 : (byte) 0));
        tag.put("pages", pagesTag);
        final ByteBuf buf = Unpooled.buffer();
        Types.ITEM1_8.write(buf, new DataItem(written ? 387 : 386, (byte) 1, tag));
        return buf;
    }

    public static void send1_8CustomPayload(final String channel, final ByteBuf data) {
        final UserConnection userConnection = getPlayNetworkUserConnection();
        if (userConnection == null) {
            return;
        }

        if (channel.contains(":")) {
            throw new IllegalStateException("Channel name has to be unmapped");
        }

        final PacketWrapper customPayload = PacketWrapper.create(ServerboundPackets1_8.CUSTOM_PAYLOAD, userConnection);
        customPayload.write(Types.STRING, channel);
        customPayload.write(Types.REMAINING_BYTES, data.array());

        customPayload.sendToServer(Protocol1_8To1_9.class);
    }

    public static void send1_8SignUpdatePacket(final BlockPos pos, final String line1, final String line2, final String line3, final String line4) {
        final UserConnection userConnection = getPlayNetworkUserConnection();
        if (userConnection == null) {
            return;
        }

        final PacketWrapper signUpdate = PacketWrapper.create(ServerboundPackets1_8.SIGN_UPDATE, userConnection);
        signUpdate.write(Types.BLOCK_POSITION1_8, toPosition(pos));
        signUpdate.write(Types.STRING, line1);
        signUpdate.write(Types.STRING, line2);
        signUpdate.write(Types.STRING, line3);
        signUpdate.write(Types.STRING, line4);

        signUpdate.sendToServer(Protocol1_8To1_9.class);
    }

    public static void send1_8BlockPlacePacket(final BlockPos pos, final int face, final ItemStack item, final float cX, final float cY, final float cZ) {
        final UserConnection userConnection = getPlayNetworkUserConnection();
        if (userConnection == null) {
            return;
        }

        final PacketWrapper blockPlacement = PacketWrapper.create(ServerboundPackets1_8.USE_ITEM_ON, userConnection);
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
