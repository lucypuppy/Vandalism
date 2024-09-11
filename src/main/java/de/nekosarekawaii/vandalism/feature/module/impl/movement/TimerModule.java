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
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.TickTimeListener;
import de.nekosarekawaii.vandalism.feature.module.Module;

public class TimerModule extends Module implements TickTimeListener {

    private final FloatValue ticksPerSecond = new FloatValue(
            this,
            "Ticks per seconds",
            "The amount of speed the timer should have.",
            20.0f,
            1.0f,
            100.0f
    );

    private final BooleanValue guis = new BooleanValue(
            this,
            "GUIs",
            "Also applies the timer to GUIs.",
            false
    );

    public TimerModule() {
        super(
                "Timer",
                "Allows you to customize the speed of the game timer.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickTimeEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickTimeEvent.ID, this);
    }

    @Override
    public void onTickTimings(final TickTimeEvent event) {
        if (this.guis.getValue() || this.mc.currentScreen == null) {
            event.tickTime = 1000f / this.ticksPerSecond.getValue();
        }
    }

}
