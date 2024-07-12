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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.TickTimeListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class SpartanOMFGLOL extends ModuleMulti<FlightModule> implements PlayerUpdateListener, IncomingPacketListener, TickTimeListener {

    public SpartanOMFGLOL() {
        super("SpartanOMFGLOL");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID, TickTimeEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID, TickTimeEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.player.fallDistance > 3.0f) {
            this.mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));

            final Vec3d velocity = MovementUtil.isMoving() ? MovementUtil.setSpeed(1.5f) : this.mc.player.getVelocity();
            this.mc.player.setVelocity(velocity.x, 0.73, velocity.z);

            this.mc.player.fallDistance = 0.0f;
        }

        if (mc.player.hurtTime > 2 && MovementUtil.isMoving()) {
            MovementUtil.setSpeed(1.5f);
        }
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (
                event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket &&
                        this.mc.player != null &&
                        velocityPacket.getId() == this.mc.player.getId()
        ) {
            event.cancel();
        }
    }

    @Override
    public void onTickTimings(TickTimeEvent event) {
        event.fromPercentage(0.75f);
    }

}