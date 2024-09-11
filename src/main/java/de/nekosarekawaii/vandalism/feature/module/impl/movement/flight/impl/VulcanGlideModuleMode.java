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
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import de.nekosarekawaii.vandalism.util.PlayerDamageUtil;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class VulcanGlideModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener, OutgoingPacketListener, IncomingPacketListener {

    private int ticks, ticks2;

    private final BooleanValue damage = new BooleanValue(this, "Damage", "Damages the player on takeoff and allows you to fly further distances.", false);

    public VulcanGlideModuleMode(final FlightModule parent) {
        super("Vulcan Glide", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID, IncomingPacketEvent.ID);

        if (damage.getValue()) {
            if (mc.player.isOnGround()) {
                PlayerDamageUtil.damagePlayerVulcan();
            } else {
                ChatUtil.errorChatMessage("You must be on the ground to activate Vulcan Glide.");
                this.parent.deactivate();
            }
        }
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID, IncomingPacketEvent.ID);
        mc.player.setVelocity(mc.player.getVelocity().getX(), 0.06, mc.player.getVelocity().getZ());
        this.ticks = 0;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.damage.getValue() && mc.player.fallDistance < 0.1F) {
            if (mc.player.hurtTime > 8) {
                mc.player.setVelocity(0, 9.0, 0);
            }

            if (mc.player.hurtTime == 5) {
                mc.player.setVelocity(0, 0, 0);
            }
            return;
        }


        if (mc.player.fallDistance > 0.0025) {
            final Vec3d velocity = MovementUtil.setSpeed(0.215);
            this.ticks++;

            if (this.ticks % 2 == 0) {
                this.ticks2++;

                if (this.ticks2 % 3 == 0) {
                    mc.player.setVelocity(velocity.x, -0.1, velocity.z);
                } else {
                    mc.player.setVelocity(velocity.x, -0.155, velocity.z);
                }
            } else {
                mc.player.setVelocity(velocity.x, -0.1, velocity.z);
            }
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof PlayerMoveC2SPacket packet) {
            if (!WorldUtil.isOnGround(2)) {
                if (mc.player.age % 10 == 0) {
                    packet.onGround = true;
                }
            }
        }
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (
                event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket &&
                        this.mc.player != null &&
                        velocityPacket.getEntityId() == this.mc.player.getId()
        ) {
            event.cancel();
        }
    }

}
