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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.elytraflight.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.elytraflight.ElytraFlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;

public class MotionModuleMode extends ModuleMulti<ElytraFlightModule> implements PlayerUpdateListener {

    private final DoubleValue speed = new DoubleValue(
            this,
            "Speed",
            "The speed amount of the motion elytra flight.",
            1.2,
            0.1,
            5.0
    );

    private final DoubleValue upwardsSpeed = new DoubleValue(
            this,
            "Upwards Speed",
            "The upwards speed amount of the motion elytra flight.",
            1.0,
            0.1,
            5.0
    );

    private final DoubleValue downwardsSpeed = new DoubleValue(
            this,
            "Downwards Speed",
            "The downwards speed amount of the motion elytra flight.",
            0.4,
            0.1,
            0.4
    );

    public MotionModuleMode() {
        super("Motion");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.mc.player.isFallFlying()) {
            if (this.mc.player.input.jumping) {
                this.mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
        else {
            final Vec3d velocity = MovementUtil.setSpeed(MovementUtil.isMoving() ? this.speed.getValue() : 0);
            this.mc.player.setVelocity(
                    velocity.x,
                    this.mc.player.input.jumping ? this.upwardsSpeed.getValue() : this.mc.player.input.sneaking ? -this.downwardsSpeed.getValue() : 0,
                    velocity.z
            );
        }
    }

}
