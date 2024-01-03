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
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.TimerHack;

public class TimerModule extends AbstractModule implements TickGameListener {

    private final Value<Float> timerModifier = new FloatValue(
            this,
            "Timer Modifier",
            "Allows you to customize the timer speed.",
            2.f,
            .1f,
            20.f
    );

    private final BooleanValue screen = new BooleanValue(
            this,
            "Screen",
            "Allows you to use the timer in screen(s).",
            false
    );


    public TimerModule() {
        super("Timer", "Modifies the timer speed.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (!this.screen.getValue() && mc.currentScreen != null) {
            TimerHack.reset();
        } else {
            TimerHack.setSpeed(this.timerModifier.getValue());
        }
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        TimerHack.reset();
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }

}
