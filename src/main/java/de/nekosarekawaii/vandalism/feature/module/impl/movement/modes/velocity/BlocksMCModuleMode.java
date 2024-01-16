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
import net.minecraft.util.math.MathHelper;

public class BlocksMCModuleMode extends ModuleMulti<VelocityModule> implements IncomingPacketListener {

    public BlocksMCModuleMode(final VelocityModule parent) {
        super("BlocksMC", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, IncomingPacketEvent.ID);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket && this.mc.player != null && velocityPacket.getId() == this.mc.player.getId()) {
            final float yaw = this.mc.player.getYaw() * 0.017453292F;
            velocityPacket.velocityX = (int) ((-MathHelper.sin(yaw) * 0.8F) * 8000.0D);
            velocityPacket.velocityZ = (int) ((MathHelper.cos(yaw) * 0.8F) * 8000.0D);
        }
    }

}
