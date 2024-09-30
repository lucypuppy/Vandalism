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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class VehicleControlModule extends Module implements PlayerUpdateListener {

    public final BooleanValue alwaysSaddle = new BooleanValue(
            this,
            "Always Saddle",
            "Allows you to ride entities that dont have a saddle.",
            false
    );

    private final BooleanValue vehicleFlight = new BooleanValue(
            this,
            "Vehicle Flight",
            "Allows you to fly with a vehicle.",
            true
    );

    private final ValueGroup vehicleFlightGroup = new ValueGroup(
            this,
            "Vehicle Flight Settings",
            "The group for the vehicle flight settings."
    ).visibleCondition(this.vehicleFlight::getValue);

    private final DoubleValue speed = new DoubleValue(
            this.vehicleFlightGroup,
            "Speed",
            "The speed of the vehicle flight.",
            1.2,
            1.0,
            5.0
    ).visibleCondition(this.vehicleFlight::getValue);

    private final DoubleValue motionYOffset = new DoubleValue(
            this.vehicleFlightGroup,
            "Motion Y Offset",
            "The motion y offset of the vehicle flight.",
            0.5,
            0.1,
            2.0
    ).visibleCondition(this.vehicleFlight::getValue);

    private final KeyBindValue upwardsKey = new KeyBindValue(
            this.vehicleFlightGroup,
            "Upwards Key",
            "The key to fly upwards.",
            GLFW.GLFW_KEY_SPACE
    ).visibleCondition(this.vehicleFlight::getValue);

    private final KeyBindValue downwardsKey = new KeyBindValue(
            this.vehicleFlightGroup,
            "Downwards Key",
            "The key to fly downwards.",
            GLFW.GLFW_KEY_LEFT_SHIFT
    ).visibleCondition(this.vehicleFlight::getValue);

    public VehicleControlModule() {
        super("Vehicle Control", "Allows you to customize the vehicle movement.", Category.MOVEMENT);
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
        final Entity vehicle = this.mc.player.getVehicle();
        if (vehicle == null) {
            return;
        }
        if (!this.vehicleFlight.getValue()) {
            return;
        }
        final double direction = MovementUtil.getDirection();
        final double speed = this.speed.getValue();
        final boolean isMoving = MovementUtil.isMoving();
        vehicle.setVelocity(
                isMoving ? -Math.sin(direction) * speed : 0,
                this.upwardsKey.isPressed() ? this.motionYOffset.getValue() : this.downwardsKey.isPressed() ? -this.motionYOffset.getValue() : 0,
                isMoving ? Math.cos(direction) * speed : 0
        );
    }

}
