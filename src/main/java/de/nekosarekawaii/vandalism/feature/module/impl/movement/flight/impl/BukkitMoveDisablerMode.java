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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class BukkitMoveDisablerMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener, IncomingPacketListener {

    public BukkitMoveDisablerMode() {
        super("BukkitMoveDisablerMode");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        final double hspeed = 0.0625D;
        final double vspeed = 0.0625D;

        final Vec3d forward = new Vec3d(0, 0, hspeed)
                .rotateY(-(float) Math.toRadians(Math.round(mc.player.getYaw() / 90) * 90));
        Vec3d moveVec = Vec3d.ZERO;

        if (mc.options.forwardKey.isPressed()) {
            moveVec = moveVec.add(forward);
            mc.player.setVelocity(0, 0, 0);
        } else if (mc.options.backKey.isPressed()) {
            moveVec = moveVec.add(forward.negate());
            mc.player.setVelocity(0, 0, 0);
        } else if (mc.options.leftKey.isPressed()) {
            moveVec = moveVec.add(forward.rotateY((float) Math.toRadians(90)));
            mc.player.setVelocity(0, 0, 0);
        } else if (mc.options.rightKey.isPressed()) {
            moveVec = moveVec.add(forward.rotateY((float) -Math.toRadians(90)));
            mc.player.setVelocity(0, 0, 0);
        } else if (mc.options.jumpKey.isPressed()) {
            moveVec = moveVec.add(0, vspeed, 0);
            mc.player.setVelocity(0, 0, 0);
        } else if (mc.options.sneakKey.isPressed()) {
            moveVec = moveVec.add(0, -vspeed, 0);
            mc.player.setVelocity(0, 0, 0);
        }

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player.getX() + moveVec.x, mc.player.getY() + moveVec.y, mc.player.getZ() + moveVec.z, false));

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player.getX() + moveVec.x, mc.player.getY() - 100, mc.player.getZ() + moveVec.z, true));
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (event.packet instanceof final PlayerPositionLookS2CPacket packet) {
            if (mc.player == null) return;

            packet.pitch = mc.player.getPitch();
            packet.yaw = mc.player.getYaw();
        }
    }
}
