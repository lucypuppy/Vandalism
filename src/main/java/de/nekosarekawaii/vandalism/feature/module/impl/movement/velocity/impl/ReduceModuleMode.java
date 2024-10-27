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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.hit.HitResult;

public class ReduceModuleMode extends ModuleMulti<VelocityModule> implements IncomingPacketListener {

    private final FloatValue velocityStrength = new FloatValue(
            this,
            "Velocity Strength",
            "The strength of the velocity.",
            0.7F,
            0.0F,
            1.0F
    );

    private final BooleanValue checkAim = new BooleanValue(
            this,
            "Check Aim",
            "Only reduce the velocity if the player is aiming at an entity.",
            true
    );

    public ReduceModuleMode() {
        super("Reduce");
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
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket &&
                mc.player != null && velocityPacket.getEntityId() == mc.player.getId()) {
            if (this.checkAim.getValue() && (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY))
                return;

            double velocityX = velocityPacket.getVelocityX() / 8000.0d;
            double velocityZ = velocityPacket.getVelocityZ() / 8000.0d;
            velocityX *= this.velocityStrength.getValue();
            velocityZ *= this.velocityStrength.getValue();
            velocityPacket.velocityX = (int) (velocityX * 8000.0d);
            velocityPacket.velocityZ = (int) (velocityZ * 8000.0d);
        }
    }

}
