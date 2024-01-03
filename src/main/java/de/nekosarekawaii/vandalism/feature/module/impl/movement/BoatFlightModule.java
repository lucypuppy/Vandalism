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
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;

public class BoatFlightModule extends AbstractModule implements TickGameListener {

    public BoatFlightModule() {
        super("Boat Flight", "Allows you to fly with a boat.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || !mc.player.hasVehicle()) return;

        float motionY = 0;
        if (mc.options.jumpKey.isPressed())
            motionY += 0.5f;
        else if (mc.options.sprintKey.isPressed())
            motionY -= 0.5f;

        mc.player.getVehicle().setVelocity(mc.player.getVehicle().getVelocity().getX(), motionY, mc.player.getVehicle().getVelocity().getY());

        if (mc.options.forwardKey.isPressed())
            MovementUtil.setSpeed(mc.player.getVehicle(), 1f);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }
}
