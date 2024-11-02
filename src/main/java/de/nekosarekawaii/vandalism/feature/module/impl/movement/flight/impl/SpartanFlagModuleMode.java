/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.util.math.Vec3d;

public class SpartanFlagModuleMode extends ModuleMulti<FlightModule> implements PlayerUpdateListener {

    private double lastRandom;

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
            1.2,
            1.0,
            5.0
    );

    public SpartanFlagModuleMode() {
        super("Spartan Flag");
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
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (mc.player.isInFluid()) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
            return;
        }

        double random = Math.random() * 2.0;
        if (Math.abs(this.lastRandom - random) < 0.5) {
            random *= Math.random();
        }
        this.lastRandom = random;

        final double speed = this.speed.getValue() + random;
        double motionX = 0, motionY = 0, motionZ = 0;

        if (mc.player.age % 11 == 0) {
            if (mc.player.input.jumping) {
                motionY = this.motionYOffset.getValue() + random;
            } else if (mc.player.input.sneaking) {
                motionY = -(this.motionYOffset.getValue() + random);
            }
        }

        if (MovementUtil.isMoving()) {
            final Vec3d speedVelocity = MovementUtil.setSpeed(speed);
            motionX = speedVelocity.x;
            motionZ = speedVelocity.z;
        }

        mc.player.setVelocity(motionX, motionY, motionZ);
    }

}
