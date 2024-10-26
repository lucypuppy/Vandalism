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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class SpartanRoflModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener, OutgoingPacketListener {

    public SpartanRoflModuleMode() {
        super("Spartan Rofl");
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
        final double speed = 0.33;
        double motionX = 0, motionY = 0, motionZ = 0;

        if (mc.player.input.jumping) {
            motionY = 0.42;
        } else if (mc.player.input.sneaking) {
            motionY = -0.42;
        }

        if (MovementUtil.isMoving()) {
            final Vec3d speedVelocity = MovementUtil.setSpeed(speed);
            motionX = speedVelocity.x;
            motionZ = speedVelocity.z;
        }

        mc.player.setVelocity(motionX, motionY, motionZ);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet) {
            final double roflY = Math.sin(System.currentTimeMillis()) * 0.05;
            final double roflXZ = roflY / 4;
            packet.x += roflXZ;
            packet.y += roflY;
            packet.z += roflXZ;
        }
    }

}