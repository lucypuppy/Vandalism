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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class VelocityModule extends AbstractModule implements IncomingPacketListener {

    public VelocityModule() {
        super("Velocity", "Modifies the server and the damage source velocity you take.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        //For some reason the player can be null when joining a server.
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket && this.mc.player != null && velocityPacket.getId() == this.mc.player.getId()) {
            event.cancel();
        }
    }

}
