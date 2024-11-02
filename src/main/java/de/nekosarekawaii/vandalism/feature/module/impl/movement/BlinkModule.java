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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.GameTickListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.PacketHelper;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BlinkModule extends Module implements OutgoingPacketListener, IncomingPacketListener, AttackListener, GameTickListener {

    private final BooleanValue delayIncoming = new BooleanValue(this, "Delay Incoming", "Delays incoming packets too.", false);
    private final BooleanValue reSyncOnAttack = new BooleanValue(this, "Resync On Attack", "Resyncs you when attacking.", false);
    private final BooleanValue pulse = new BooleanValue(this, "Pulse", "Blinks in intervals.", false);
    private final IntegerValue pulseDelay = new IntegerValue(this, "Pulse Delay", "The delay between each pulse.", 500, 0, 5000);
    private final BooleanValue showPlacedBlocks = new BooleanValue(this, "Show Placed Blocks", "Shows placed blocks while blinking.", false);

    private final ConcurrentLinkedQueue<BlinkPacket> packets = new ConcurrentLinkedQueue<>();

    public BlinkModule() {
        super("Blink", "Makes you look like teleporting to other players.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID, IncomingPacketEvent.ID, AttackSendEvent.ID, GameTickEvent.ID);
        this.lastSend = System.currentTimeMillis();
    }

    @Override
    public void onDeactivate() {
        this.sendPackets();
        Vandalism.getInstance().getEventSystem().unsubscribe(this, OutgoingPacketEvent.ID, IncomingPacketEvent.ID, AttackSendEvent.ID, GameTickEvent.ID);
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (event.networkPhase != NetworkPhase.PLAY || mc.world == null || mc.player == null) {
            return;
        }
        if (!this.delayIncoming.getValue() || (this.showPlacedBlocks.getValue() && event.packet instanceof final BlockUpdateS2CPacket updatePacket && !updatePacket.getState().isAir())) {
            return;
        }
        event.setCancelled(true);
        this.packets.add(new BlinkPacket(event.packet, BlinkPacket.Direction.INCOMING));
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.networkPhase != NetworkPhase.PLAY || mc.world == null || mc.player == null) {
            return;
        }
        event.setCancelled(true);
        this.packets.add(new BlinkPacket(event.packet, BlinkPacket.Direction.OUTGOING));
    }

    @Override
    public void onAttackSend(AttackSendEvent event) {
        if (this.reSyncOnAttack.getValue()) {
            if (this.pulse.getValue()) {
                this.sendPackets();
            } else {
                this.deactivate();
            }
        }
    }

    private long lastSend;

    @Override
    public void onGameTick(GameTickEvent event) {
        if (mc.player == null) return;
        if (this.pulse.getValue() && System.currentTimeMillis() - this.lastSend >= this.pulseDelay.getValue()) {
            this.sendPackets();
        }
    }

    private void sendPackets() {
        while (!this.packets.isEmpty()) {
            final BlinkPacket packet = this.packets.poll();
            if (packet.direction == BlinkPacket.Direction.INCOMING && this.delayIncoming.getValue()) {
                PacketHelper.receivePacket(packet.packet);
            } else if (packet.direction == BlinkPacket.Direction.OUTGOING) {
                PacketHelper.sendImmediately(packet.packet, null, true);
            }
        }
        this.lastSend = System.currentTimeMillis();
    }

    private record BlinkPacket(Packet<?> packet, BlinkModule.BlinkPacket.Direction direction) {
        private enum Direction {
            INCOMING,
            OUTGOING
        }
    }
}
