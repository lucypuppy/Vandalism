/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class MotionModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener {

    private final DoubleValue motionYOffset = new DoubleValue(
            this,
            "Motion Y Offset",
            "The motion y offset of the motion flight.",
            1.0,
            0.1,
            2.0
    );

    private final DoubleValue speed = new DoubleValue(
            this,
            "Speed",
            "The speed amount of the motion flight.",
            2.0,
            1.0,
            5.0
    );

    public MotionModuleMode() {
        super("Motion");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        double motionX = 0;
        double motionZ = 0;
        final double motionY = this.mc.options.jumpKey.isPressed() ? this.motionYOffset.getValue() : this.mc.options.sneakKey.isPressed() ? -this.motionYOffset.getValue() : 0;
        if (MovementUtil.isMoving()) {
            final Vec3d speedVelocity = MovementUtil.setSpeed(this.speed.getValue());
            motionX = speedVelocity.x;
            motionZ = speedVelocity.z;
        }
        this.mc.player.setVelocity(motionX, motionY, motionZ);
    }

}
