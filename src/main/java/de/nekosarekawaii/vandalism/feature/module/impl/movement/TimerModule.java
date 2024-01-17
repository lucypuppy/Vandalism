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
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class TimerModule extends AbstractModule implements PlayerUpdateListener {

    private final FloatValue timerSpeed = new FloatValue(
            this,
            "Timer Speed",
            "The amount of speed the timer should have.",
            2.0f,
            0.1f,
            20.0f
    );

    private final BooleanValue guis = new BooleanValue(
            this,
            "GUI's",
            "Also applies the timer to GUI's.",
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
        TimerHack.reset();
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        TimerHack.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.guis.getValue() && this.mc.currentScreen != null) {
            TimerHack.reset();
        } else {
            TimerHack.setSpeed(this.timerSpeed.getValue());
        }
    }

}
