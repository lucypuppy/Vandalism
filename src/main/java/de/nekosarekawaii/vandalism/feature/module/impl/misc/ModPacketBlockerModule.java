/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModPacketBlockerModule extends Module implements IncomingPacketListener, OutgoingPacketListener {

    public final BooleanValue unloadFabricAPICallbacks = new BooleanValue(
            this,
            "Unload Fabric API Callbacks",
            "Unloads Fabric API callbacks.",
            true
    );

    private final Map<String, BooleanValue> platformSettings = new HashMap<>();

    public ModPacketBlockerModule() {
        super("Mod Packet Blocker", "Blocks various packets from mods which could be detected by a server.", Category.MISC);
        this.activateDefault();
        for (final String modId : Arrays.asList("journeymap", "roughlyenoughitems", "architectury")) {
            this.platformSettings.put(
                    modId,
                    new BooleanValue(
                            this,
                            "Block " + modId + " Packets",
                            "Blocks packets from " + modId + ".",
                            true
                    ).visibleCondition(() -> FabricLoader.getInstance().isModLoaded(modId))
            );
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

    private boolean cancel(final String channel) {
        for (final Map.Entry<String, BooleanValue> entry : this.platformSettings.entrySet()) {
            if (entry.getValue().getValue() && channel.startsWith(entry.getKey())) return true;
        }
        return false;
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final CustomPayloadS2CPacket customPayloadPacket) {
            final String channel = customPayloadPacket.payload().getId().id().getNamespace();
            if (this.cancel(channel)) event.cancel();
        }
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final CustomPayloadC2SPacket customPayloadPacket) {
            final String channel = customPayloadPacket.payload().getId().id().getNamespace();
            if (this.cancel(channel)) event.cancel();
        }
    }

}
