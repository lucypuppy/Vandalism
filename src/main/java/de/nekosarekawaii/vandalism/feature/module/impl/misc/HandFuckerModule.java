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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.Arm;

import java.util.ArrayList;
import java.util.List;

public class HandFuckerModule extends AbstractModule implements PlayerUpdateListener, IncomingPacketListener {

    private final IntegerValue delay = new IntegerValue(this, "Delay", "Delay of the hand switch", 1_000, 1, 2_000);
    private final BooleanValue annoyMe = new BooleanValue(this, "Annoy me", "Annoy me please", false);
    private long lastUpdate;
    private Arm arm;

    public HandFuckerModule() {
        super("Hand Fucker", "Switches hands server-side.", Category.MISC); // TODO Version range
        this.lastUpdate = 0L;
        this.deactivateOnQuitDefault();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
        this.arm = mc.options.getMainArm().getValue();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if ((System.currentTimeMillis() - this.lastUpdate) > this.delay.getValue()) {
            this.lastUpdate = System.currentTimeMillis();
            final SyncedClientOptions options = mc.options.getSyncedOptions();
            this.arm = this.arm.getOpposite();
            mc.player.networkHandler.getConnection().channel.eventLoop().execute(() -> {
                mc.player.networkHandler.getConnection().channel.pipeline().writeAndFlush(new ClientOptionsC2SPacket(new SyncedClientOptions(options.language(), options.viewDistance(), options.chatVisibility(), options.chatColorsEnabled(), options.playerModelParts(), this.arm, options.filtersText(), options.allowsServerListing())));
            });
        }
    }

    private int entityId = 0;

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (this.annoyMe.getValue())
            return;

        if (event.packet instanceof GameJoinS2CPacket joinPacket) {
            this.entityId = joinPacket.playerEntityId();
        }

        if (event.packet instanceof final EntityTrackerUpdateS2CPacket packet) {
            if (packet.id() == (mc.player == null ? this.entityId : mc.player.getId())) {
                final List<DataTracker.SerializedEntry<?>> entries = new ArrayList<>();
                for (final DataTracker.SerializedEntry<?> trackedValue : packet.trackedValues()) {
                    if (trackedValue.id() != 18) {
                        entries.add(trackedValue);
                    }
                }
                event.packet = new EntityTrackerUpdateS2CPacket(packet.id(), entries);
            }
        }
    }
}
