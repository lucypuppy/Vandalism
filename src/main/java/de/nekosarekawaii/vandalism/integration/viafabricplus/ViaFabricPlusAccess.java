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

package de.nekosarekawaii.vandalism.integration.viafabricplus;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import de.nekosarekawaii.vandalism.Vandalism;
import net.lenni0451.reflect.stream.RStream;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import java.lang.reflect.Field;

public class ViaFabricPlusAccess {

    // We don't want to get the UNKNOWN version.
    private static final String PROTOCOL_TRANSLATOR_CLASS = "de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator";

    private static Position toPosition(final BlockPos pos) {
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void send1_8SignUpdatePacket(final BlockPos pos, final String line1, final String line2, final String line3, final String line4) {
        final PacketWrapper packet = PacketWrapper.create(ServerboundPackets1_8.UPDATE_SIGN, getUserConnection());
        packet.write(Type.POSITION1_8, toPosition(pos));
        packet.write(Type.STRING, line1);
        packet.write(Type.STRING, line2);
        packet.write(Type.STRING, line3);
        packet.write(Type.STRING, line4);
        try {
            packet.sendToServerRaw();
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while sending a 1.8 sign update packet.", e);
        }
    }

    public static void send1_8BlockPlacePacket(final BlockPos pos, final int face, final ItemStack item, final float cX, final float cY, final float cZ) {
        final PacketWrapper packet = PacketWrapper.create(ServerboundPackets1_8.PLAYER_BLOCK_PLACEMENT, getUserConnection());
        packet.write(Type.POSITION1_8, toPosition(pos));
        packet.write(Type.UNSIGNED_BYTE, (short) face);
        packet.write(Type.ITEM1_8, translateItem(item, ProtocolVersion.v1_8));
        packet.write(Type.UNSIGNED_BYTE, (short) cX);
        packet.write(Type.UNSIGNED_BYTE, (short) cY);
        packet.write(Type.UNSIGNED_BYTE, (short) cZ);
        try {
            packet.sendToServerRaw();
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while sending a 1.8 block place packet.", e);
        }
    }

    /**
     * Attempts to get the protocol version.
     *
     * @return the user connection
     */
    public static ProtocolVersion getTargetVersion() {
        try {
            return RStream.of(PROTOCOL_TRANSLATOR_CLASS).methods().by("getPlayNetworkUserConnection").invoke();
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while attempting to get the user connection.", e);
            return null;
        }
    }

    /**
     * Attempts to get the user connection.
     *
     * @return the user connection
     */
    public static UserConnection getUserConnection() {
        try {
            return RStream.of(PROTOCOL_TRANSLATOR_CLASS).methods().by("getPlayNetworkUserConnection").invoke();
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while attempting to get the user connection.", e);
            return null;
        }
    }

    /**
     * Attempts to translate the item using ViaFabricPlus' Item Translator.
     *
     * @param mcStack       the minecraft item stack
     * @param targetVersion the version to translate to
     * @return the viaversion item
     */
    public static Item translateItem(final ItemStack mcStack, final ProtocolVersion targetVersion) {
        try {
            final RStream classStream = RStream.of("de.florianmichael.viafabricplus.protocoltranslator.translator.ItemTranslator");
            return classStream.methods().by("mcToVia", ItemStack.class, ProtocolVersion.class).invokeArgs(mcStack, targetVersion);
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while attempting to translate the item.", e);
            return null;
        }
    }

    /**
     * Attempts to set a ViaFabricPlus setting.
     *
     * @param settingsClass the settings class (ex. GeneralSettings)
     * @param settingsName  the property field name
     * @param value         the value
     * @param <T>           type
     */
    public static <T> void setSettingValue(final String settingsClass, final String settingsName, final T value) {
        try {
            final RStream classStream = RStream.of("de.florianmichael.viafabricplus.settings.impl." + settingsClass);
            final Object globalMethod = classStream.methods().by("global").invoke();
            Field settingField = classStream.clazz().getDeclaredField(settingsName);

            settingField.getType().getMethod(
                    "setValue",
                    value.getClass()
            ).invoke(globalMethod, value);
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while attempting to set the value.", e);
        }
    }

    /**
     * Attempts to set the previous version.
     *
     * @param version the previous version
     */
    public static void setPreviousVersion(final ProtocolVersion version) {
        try {
            RStream.of(PROTOCOL_TRANSLATOR_CLASS).fields().by("previousVersion").set(version);
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
        try {
            RStream.of(PROTOCOL_TRANSLATOR_CLASS).methods().by(
                    "setTargetVersion",
                    ProtocolVersion.class,
                    boolean.class
            ).invokeArgs(version, revertOnDisconnect);
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("An error occurred while attempting to set the value.", e);
        }
    }

}
