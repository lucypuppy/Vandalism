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
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class CancelModuleMode extends ModuleMulti<VelocityModule> implements IncomingPacketListener {

    private final BooleanValue customizeCancel = new BooleanValue(
            this, "Customize Cancel",
            "Customizes the cancel velocity.",
            false
    );

    private final BooleanValue cancelHorizontal = new BooleanValue(
            this,
            "Cancel Horizontal",
            "Cancels the X and Z velocity.",
            true
    ).visibleCondition(this.customizeCancel::getValue);

    private final BooleanValue cancelVertical = new BooleanValue(
            this,
            "Cancel Vertical",
            "Cancels the Y velocity.",
            true
    ).visibleCondition(this.customizeCancel::getValue);

    public CancelModuleMode() {
        super("Cancel");
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
        if (
                event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket &&
                        mc.player != null &&
                        velocityPacket.getEntityId() == mc.player.getId()
        ) {
            if (this.customizeCancel.getValue()) {
                final Vec3d velocity = mc.player.getVelocity();
                if (this.cancelHorizontal.getValue()) {
                    velocityPacket.velocityX = (int) (velocity.x * 8000D);
                    velocityPacket.velocityZ = (int) (velocity.z * 8000D);
                }
                if (this.cancelVertical.getValue()) {
                    velocityPacket.velocityY = (int) (velocity.y * 8000D);
                }
            } else {
                event.cancel();
            }
        }
    }

}
