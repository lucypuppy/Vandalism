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
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.base.value.impl.awt.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import org.lwjgl.glfw.GLFW;

public class VehicleFlightModule extends AbstractModule implements PlayerUpdateListener {

    private final DoubleValue speed = new DoubleValue(
            this,
            "Speed",
            "The speed of the vehicle flight.",
            1.2,
            1.0,
            5.0
    );

    private final DoubleValue motionYOffset = new DoubleValue(
            this,
            "Motion Y Offset",
            "The motion y offset of the vehicle flight.",
            0.5,
            0.1,
            2.0
    );

    private final KeyBindValue upwardsKey = new KeyBindValue(
            this,
            "Upwards Key",
            "The key to fly upwards.",
            GLFW.GLFW_KEY_SPACE
    );

    private final KeyBindValue downwardsKey = new KeyBindValue(
            this,
            "Downwards Key",
            "The key to fly downwards.",
            GLFW.GLFW_KEY_LEFT_SHIFT
    );

    public VehicleFlightModule() {
        super("Vehicle Flight", "Allows you to fly with a vehicle.", Category.MOVEMENT);
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
        if (!this.mc.player.hasVehicle()) {
            return;
        }
        final double direction = MovementUtil.getDirection();
        final double speed = this.speed.getValue();
        final boolean isMoving = MovementUtil.isMoving();
        this.mc.player.getVehicle().setVelocity(
                isMoving ? Math.cos(direction) * speed : 0,
                this.upwardsKey.isPressed() ? this.motionYOffset.getValue() : this.downwardsKey.isPressed() ? -this.motionYOffset.getValue() : 0,
                isMoving ? Math.sin(direction) * speed : 0
        );
    }

}
