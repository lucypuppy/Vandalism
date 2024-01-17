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
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

public class ReverseModuleMode extends ModuleMulti<VelocityModule> implements IncomingPacketListener {

    private final Value<Float> multiplier = new FloatValue(getParent(), "Multiplier", "The multiplier for the velocity.",
            0.3F, 0.0F, 1.0F).visibleCondition(() -> this.getParent().mode.getValue().equals(this));

    public ReverseModuleMode(final VelocityModule parent) {
        super("Reverse", parent);
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
            final float yaw = (float) Math.toRadians(this.mc.player.getYaw());
            velocityPacket.velocityX = (int) ((-MathHelper.sin(yaw) * this.multiplier.getValue()) * 8000.0D);
            velocityPacket.velocityZ = (int) ((MathHelper.cos(yaw) * this.multiplier.getValue()) * 8000.0D);
        }
    }

}
