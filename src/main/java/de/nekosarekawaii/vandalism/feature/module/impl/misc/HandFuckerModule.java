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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.util.Arm;
import net.raphimc.vialoader.util.VersionRange;

import java.util.ArrayList;
import java.util.List;

public class HandFuckerModule extends Module implements PlayerUpdateListener, IncomingPacketListener {

    private final IntegerValue delay = new IntegerValue(
            this, "Delay",
            "Delay of the hand switch.",
            20,
            1,
            80
    );

    private final BooleanValue annoyMe = new BooleanValue(
            this,
            "Annoy me",
            "Annoy me please.",
            false
    );

    private long partialTicks;
    private Arm arm;

    public HandFuckerModule() {
        super(
                "Hand Fucker",
                "Switches hands server-side.",
                Category.MISC,
                VersionRange.andNewer(ProtocolVersion.v1_9)
        );
        this.partialTicks = 0L;
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
        if (mc.player != null) {
            mc.player.setMainArm(mc.options.getMainArm().getValue());
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.annoyMe.getValue()) {
            mc.player.setMainArm(mc.options.getMainArm().getValue());
        }
        if (++this.partialTicks >= this.delay.getValue()) {
            this.partialTicks = 0;
            final SyncedClientOptions options = mc.options.getSyncedOptions();
            this.arm = this.arm.getOpposite();
            mc.player.networkHandler.sendPacket(new ClientOptionsC2SPacket(new SyncedClientOptions(
                    options.language(),
                    options.viewDistance(),
                    options.chatVisibility(),
                    options.chatColorsEnabled(),
                    options.playerModelParts(),
                    this.arm,
                    options.filtersText(),
                    options.allowsServerListing()
            )));
        }
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (mc.player == null || this.annoyMe.getValue())
            return;

        if (event.packet instanceof final EntityTrackerUpdateS2CPacket packet) {
            if (packet.id() == mc.player.getId()) {
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
