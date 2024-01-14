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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.velocity;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class CancelModuleMode extends ModuleMulti<VelocityModule> implements IncomingPacketListener {

    public CancelModuleMode(final VelocityModule parent) {
        super("Cancel", parent);
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
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket &&
                velocityPacket.getId() == this.mc.player.getId()) {
            event.cancel();
        }
    }

}
