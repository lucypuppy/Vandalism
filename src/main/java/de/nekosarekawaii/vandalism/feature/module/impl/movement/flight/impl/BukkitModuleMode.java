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
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class BukkitModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener, IncomingPacketListener {

    public BukkitModuleMode() {
        super("Bukkit");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID,
                IncomingPacketEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID,
                IncomingPacketEvent.ID
        );
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final double hSpeed = 0.0625D;
        final double vSpeed = 0.0625D;

        final Vec3d forward = new Vec3d(0, 0, hSpeed).rotateY(
                -(float) Math.toRadians(Math.round(this.mc.player.getYaw() / 90) * 90)
        );

        Vec3d moveVec = Vec3d.ZERO;

        if (this.mc.player.input.movementForward > 0) {
            moveVec = moveVec.add(forward);
            this.mc.player.setVelocity(0, 0, 0);
        } else if (this.mc.player.input.movementForward < 0) {
            moveVec = moveVec.add(forward.negate());
            this.mc.player.setVelocity(0, 0, 0);
        } else if (this.mc.player.input.movementSideways > 0) {
            moveVec = moveVec.add(forward.rotateY((float) Math.toRadians(90)));
            this.mc.player.setVelocity(0, 0, 0);
        } else if (this.mc.player.input.movementSideways < 0) {
            moveVec = moveVec.add(forward.rotateY((float) -Math.toRadians(90)));
            this.mc.player.setVelocity(0, 0, 0);
        } else if (this.mc.player.input.jumping) {
            moveVec = moveVec.add(0, vSpeed, 0);
            this.mc.player.setVelocity(0, 0, 0);
        } else if (this.mc.player.input.sneaking) {
            moveVec = moveVec.add(0, -vSpeed, 0);
            this.mc.player.setVelocity(0, 0, 0);
        }

        this.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                this.mc.player.getX() + moveVec.x,
                this.mc.player.getY() + moveVec.y,
                this.mc.player.getZ() + moveVec.z,
                false
        ));

        this.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                this.mc.player.getX() + moveVec.x,
                this.mc.player.getY() - 100,
                this.mc.player.getZ() + moveVec.z, true
        ));
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final PlayerPositionLookS2CPacket packet) {
            if (this.mc.player == null) return;
            packet.pitch = mc.player.getPitch();
            packet.yaw = mc.player.getYaw();
        }
    }

}
