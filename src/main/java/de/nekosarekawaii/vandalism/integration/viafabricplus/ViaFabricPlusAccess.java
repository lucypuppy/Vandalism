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

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.protocoltranslator.translator.ItemTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import net.lenni0451.reflect.stream.RStream;
import net.lenni0451.reflect.stream.field.FieldWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ViaFabricPlusAccess {

    private static final FieldWrapper previousVersion;

    static {
        final String protocolTranslatorClass = "de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator";

        previousVersion = RStream.of(protocolTranslatorClass).fields().by("previousVersion");
    }

    private static BlockPosition toPosition(final BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void send1_8SignUpdatePacket(final BlockPos pos, final String line1, final String line2, final String line3, final String line4) {
        final PacketWrapper packet = PacketWrapper.create(ServerboundPackets1_8.SIGN_UPDATE, getUserConnection());
        packet.write(Types.BLOCK_POSITION1_8, toPosition(pos));
        packet.write(Types.STRING, line1);
        packet.write(Types.STRING, line2);
        packet.write(Types.STRING, line3);
        packet.write(Types.STRING, line4);

        packet.sendToServerRaw();
    }

    public static void send1_8BlockPlacePacket(final BlockPos pos, final int face, final ItemStack item, final float cX, final float cY, final float cZ) {
        final PacketWrapper packet = PacketWrapper.create(ServerboundPackets1_8.USE_ITEM_ON, getUserConnection());
        packet.write(Types.BLOCK_POSITION1_8, toPosition(pos));
        packet.write(Types.UNSIGNED_BYTE, (short) face);
        packet.write(Types.ITEM1_8, translateItem(item, ProtocolVersion.v1_8));
        packet.write(Types.UNSIGNED_BYTE, (short) cX);
        packet.write(Types.UNSIGNED_BYTE, (short) cY);
        packet.write(Types.UNSIGNED_BYTE, (short) cZ);

        packet.sendToServerRaw();
    }

    /**
     * Attempts to get the protocol version.
     *
     * @return the user connection
     */
    public static ProtocolVersion getTargetVersion() {
        return ProtocolTranslator.getTargetVersion();
    }

    /**
     * Attempts to get the user connection.
     *
     * @return the user connection
     */
    public static UserConnection getUserConnection() {
        return ProtocolTranslator.getPlayNetworkUserConnection();
    }

    /**
     * Attempts to translate the item using ViaFabricPlus' Item Translator.
     *
     * @param mcStack       the minecraft item stack
     * @param targetVersion the version to translate to
     * @return the viaversion item
     */
    public static Item translateItem(final ItemStack mcStack, final ProtocolVersion targetVersion) {
        return ItemTranslator.mcToVia(mcStack, targetVersion);
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

    /**
     * Attempts to set the target version.
     *
     * @param version the target version
     */
    public static void setTargetVersion(final ProtocolVersion version, final boolean revertOnDisconnect) {
        ProtocolTranslator.setTargetVersion(version, revertOnDisconnect);
    }

}
