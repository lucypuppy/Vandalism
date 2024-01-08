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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.Priorities;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

public class PacketManagerModule extends AbstractModule implements IncomingPacketListener, OutgoingPacketListener {

    private static final String LOGGED_CLIENT_PACKETS = "Logged Client Packets";
    private static final String LOGGED_SERVER_PACKETS = "Logged Server Packets";

    private static final String CANCELLED_CLIENT_PACKETS = "Cancelled Client Packets";
    private static final String CANCELLED_SERVER_PACKETS = "Cancelled Server Packets";

    private final BooleanValue log = new BooleanValue(this, "Log", "Log packets.", true);
    private final BooleanValue cancel = new BooleanValue(this, "Cancel", "Cancel packets.", false);

    public PacketManagerModule() {
        super("Packet Manager", "Allows to log and cancel packets.", Category.MISC);
        for (final NetworkState networkState : NetworkState.values()) {
            final String networkStateName = StringUtils.normalizeEnumName(networkState.name());
            final ValueGroup valueGroup = new ValueGroup(this, networkStateName, networkStateName + " Packets");
            final String[] serverPackets = networkState.getPacketIdToPacketMap(NetworkSide.CLIENTBOUND).values().stream().map(Class::getSimpleName).toArray(String[]::new);
            if (serverPackets.length > 0) {
                new MultiModeValue(
                        valueGroup,
                        LOGGED_SERVER_PACKETS,
                        "Logs incoming packets.",
                        serverPackets
                );
                new MultiModeValue(
                        valueGroup,
                        CANCELLED_SERVER_PACKETS,
                        "Cancel incoming packets.",
                        serverPackets
                );
            }
            final String[] clientPackets = networkState.getPacketIdToPacketMap(NetworkSide.SERVERBOUND).values().stream().map(Class::getSimpleName).toArray(String[]::new);
            if (clientPackets.length > 0) {
                new MultiModeValue(
                        valueGroup,
                        LOGGED_CLIENT_PACKETS,
                        "Logs outgoing packets.",
                        clientPackets
                );
                new MultiModeValue(
                        valueGroup,
                        CANCELLED_CLIENT_PACKETS,
                        "Cancel outgoing packets." + (networkState == NetworkState.PLAY ? " (for example CloseHandledScreenC2SPacket which allows you to put items into your crafting slots)" : ""),
                        clientPackets
                );
            }
        }
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this, Priorities.HIGH);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    private boolean loggedContainsPacket(final boolean outgoing, final Packet<?> packet, final NetworkState networkState) {
        if (!this.log.getValue()) {
            return false;
        }
        final String packetName = packet.getClass().getSimpleName();
        for (final Value<?> value : this.getValues()) {
            if (value instanceof final ValueGroup valueGroup && valueGroup.getName().equalsIgnoreCase(networkState.name())) {
                for (final Value<?> valueGroupValue : valueGroup.getValues()) {
                    if (valueGroupValue instanceof final MultiModeValue multiModeValue) {
                        if (multiModeValue.getValue().contains(packetName)) {
                            if (outgoing) {
                                return multiModeValue.getName().equalsIgnoreCase(LOGGED_CLIENT_PACKETS);
                            } else {
                                return multiModeValue.getName().equalsIgnoreCase(LOGGED_SERVER_PACKETS);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean cancelContainsPacket(final boolean outgoing, final Packet<?> packet, final NetworkState networkState) {
        if (!this.cancel.getValue()) {
            return false;
        }
        final String packetName = packet.getClass().getSimpleName();
        for (final Value<?> value : this.getValues()) {
            if (value instanceof final ValueGroup valueGroup && valueGroup.getName().equalsIgnoreCase(networkState.name())) {
                for (final Value<?> valueGroupValue : valueGroup.getValues()) {
                    if (valueGroupValue instanceof final MultiModeValue multiModeValue) {
                        if (multiModeValue.getValue().contains(packetName)) {
                            if (outgoing) {
                                return multiModeValue.getName().equalsIgnoreCase(CANCELLED_CLIENT_PACKETS);
                            } else {
                                return multiModeValue.getName().equalsIgnoreCase(CANCELLED_SERVER_PACKETS);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean handlePacket(final boolean outgoing, final Packet<?> packet, final NetworkState networkState) {
        final String packetName = packet.getClass().getSimpleName();
        if (this.loggedContainsPacket(outgoing, packet, networkState)) {
            final StringBuilder text = new StringBuilder();
            if (outgoing) {
                text.append("Outgoing packet: ");
            } else {
                text.append("Incoming packet: ");
            }
            text.append(packetName);
            if (this.mc.inGameHud != null) {
                ChatUtil.infoChatMessage(text.toString());
            } else {
                Vandalism.getInstance().getLogger().info(text.toString());
            }
        }
        return this.cancelContainsPacket(outgoing, packet, networkState);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (this.handlePacket(false, event.packet, event.networkState)) {
            event.cancel();
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (this.handlePacket(true, event.packet, event.networkState)) {
            event.cancel();
        }
    }

}
