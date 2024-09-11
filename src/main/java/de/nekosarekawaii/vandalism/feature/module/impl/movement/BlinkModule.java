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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.PacketHelper;
import net.minecraft.network.packet.Packet;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BlinkModule extends AbstractModule implements OutgoingPacketListener, IncomingPacketListener, AttackListener {

    private final BooleanValue delayIncoming = new BooleanValue(this, "Delay Incoming", "Delays incoming packets too.", false);
    private final BooleanValue reSyncOnAttack = new BooleanValue(this, "Resync On Attack", "Resyncs you when attacking.", false);

    private final ConcurrentLinkedQueue<BlinkPacket> packets = new ConcurrentLinkedQueue<>();

    public BlinkModule() {
        super("Blink", "Makes you look like teleporting to other players.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID, IncomingPacketEvent.ID, AttackSendEvent.ID);
    }

    @Override
    public void onDeactivate() {
        sendPackets();
        Vandalism.getInstance().getEventSystem().unsubscribe(this, OutgoingPacketEvent.ID, IncomingPacketEvent.ID, AttackSendEvent.ID);
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (!delayIncoming.getValue()) {
            return;
        }
        event.setCancelled(true);
        packets.add(new BlinkPacket(event.packet, BlinkPacket.Direction.INCOMING));
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        event.setCancelled(true);
        packets.add(new BlinkPacket(event.packet, BlinkPacket.Direction.OUTGOING));
    }

    @Override
    public void onAttackSend(AttackSendEvent event) {
        if (reSyncOnAttack.getValue()) {
            this.deactivate();
        }
    }

    private void sendPackets() {
        while (!packets.isEmpty()) {
            BlinkPacket packet = packets.poll();
            if (packet.direction == BlinkPacket.Direction.INCOMING && delayIncoming.getValue()) {
                PacketHelper.receivePacket(packet.packet);
            } else if (packet.direction == BlinkPacket.Direction.OUTGOING) {
                PacketHelper.sendImmediately(packet.packet, null, true);
            }
        }
    }

    private record BlinkPacket(Packet<?> packet, BlinkModule.BlinkPacket.Direction direction) {
        private enum Direction {
            INCOMING,
            OUTGOING
        }
    }
}
