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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

import java.util.Random;

public class VulcanModuleMode extends ModuleMulti<NoFallModule> implements PlayerUpdateListener, OutgoingPacketListener {

    private int ticks;

    public VulcanModuleMode() {
        super("Vulcan");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (mc.player.fallDistance > 2.25F) {
            this.ticks++;

            if (this.ticks == 1) {
                mc.player.setVelocity(0, new Random().nextBoolean() ? -0.2 : 0.2, 0);
            }

            if (this.ticks > 2) {
                mc.player.fallDistance = 0;
                this.ticks = 0;
            }
        }
    }


    @Override
    public void onOutgoingPacket(final OutgoingPacketListener.OutgoingPacketEvent event) {
        if (this.ticks > 1) {
            if (event.packet instanceof PlayerMoveC2SPacket packet) {
                packet.onGround = true;
            }

            if (
                    event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket &&
                            this.mc.player != null &&
                            velocityPacket.getId() == this.mc.player.getId()
            ) {
                event.cancel();
            }
        }
    }

}
