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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.TickTimeListener;
import de.nekosarekawaii.vandalism.feature.module.Module;

public class TimerRangeModule extends Module implements TickTimeListener {

    public TimerRangeModule() {
        super("Timer Range", "Automatically speeds up the speed of the game.", Category.COMBAT);
    }

    private boolean hurt;
    private int ticks;

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TickTimeEvent.ID);
    }

    @Override
    public void onDeactivate() {
        hurt = false;
        ticks = 0;
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TickTimeEvent.ID);
    }

    @Override
    public void onTickTimings(TickTimeEvent event) {
        if (mc.player == null) {
            return;
        }

        if (mc.player.hurtTime > 8) {
            event.fromPercentage(0.5f);
            hurt = true;
        } else if (mc.player.hurtTime <= 4) {
            if (hurt) {
                event.fromPercentage(2f);
                if (ticks >= 6) {
                    ticks = 0;
                    hurt = false;
                } else {
                    ticks++;
                }
            } else {
                event.fromPercentage(1.0f);
            }
        } else {
            event.fromPercentage(0.75f);
        }
    }
}
