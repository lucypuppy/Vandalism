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
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class OldAACModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener {

    private double startY;

    public OldAACModuleMode() {
        super("Old AAC");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);

        this.startY = mc.player.getY();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (mc.player.fallDistance > 3.0f) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));

            if (mc.player.input.jumping) {
                this.startY += 1;
            } else if (mc.player.input.sneaking) {
                this.startY -= 1;
            }

            mc.player.fallDistance = 0.0f;
        }

        if (mc.player.hurtTime > 0 && mc.player.getPos().getY() < this.startY - 2.0) {
            final Vec3d velocity = MovementUtil.isMoving() ? MovementUtil.setSpeed(1.5f) : mc.player.getVelocity();
            mc.player.setVelocity(velocity.x, 0.6, velocity.z);
        }
    }

}
