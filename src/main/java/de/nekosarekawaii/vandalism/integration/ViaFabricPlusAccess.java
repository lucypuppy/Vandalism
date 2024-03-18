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

package de.nekosarekawaii.vandalism.integration;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.protocoltranslator.translator.ItemTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ViaFabricPlusAccess {

    private static Position toPosition(final BlockPos pos) {
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void send1_8SignUpdatePacket(final BlockPos pos, final String line1, final String line2, final String line3, final String line4) {
        final PacketWrapper packet = PacketWrapper.create(ServerboundPackets1_8.UPDATE_SIGN, ProtocolTranslator.getPlayNetworkUserConnection());
        packet.write(Type.POSITION1_8, toPosition(pos));
        packet.write(Type.STRING, line1);
        packet.write(Type.STRING, line2);
        packet.write(Type.STRING, line3);
        packet.write(Type.STRING, line4);
        try {
            packet.sendToServerRaw();
        }
        catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while sending a 1.8 sign update packet.", e);
        }
    }

    public static void send1_8BlockPlacePacket(final BlockPos pos, final int face, final ItemStack item, final float cX, final float cY, final float cZ) {
        final PacketWrapper packet = PacketWrapper.create(ServerboundPackets1_8.PLAYER_BLOCK_PLACEMENT, ProtocolTranslator.getPlayNetworkUserConnection());
        packet.write(Type.POSITION1_8, toPosition(pos));
        packet.write(Type.UNSIGNED_BYTE, (short) face);
        packet.write(Type.ITEM, ItemTranslator.mcToVia(item, ProtocolVersion.v1_8));
        packet.write(Type.UNSIGNED_BYTE, (short) cX);
        packet.write(Type.UNSIGNED_BYTE, (short) cY);
        packet.write(Type.UNSIGNED_BYTE, (short) cZ);
        try {
            packet.sendToServerRaw();
        }
        catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while sending a 1.8 block place packet.", e);
        }
    }

}
