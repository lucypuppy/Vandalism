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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.PacketUtil;
import net.minecraft.network.packet.Packet;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BlinkModule extends AbstractModule implements OutgoingPacketListener, IncomingPacketListener {

    private final BooleanValue delayIncoming = new BooleanValue(this, "Delay Incoming", "Delays incoming packets too.", false);

    private final ConcurrentLinkedQueue<Packet<?>> inPackets = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Packet<?>> outPackets = new ConcurrentLinkedQueue<>();

    public BlinkModule() {
        super("Blink", "Makes you look like teleporting to other players.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        sendPackets();
        Vandalism.getInstance().getEventSystem().unsubscribe(this, OutgoingPacketEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (!delayIncoming.getValue()) {
            return;
        }
        event.setCancelled(true);
        inPackets.add(event.packet);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        event.setCancelled(true);
        outPackets.add(event.packet);
    }

    private void sendPackets() {
        while (!inPackets.isEmpty() && delayIncoming.getValue()) {
            PacketUtil.receivePacket(inPackets.poll());
        }

        while (!outPackets.isEmpty()) {
            PacketUtil.sendImmediately(outPackets.poll(), null, true);
        }

        inPackets.clear();
        outPackets.clear();
    }
}
