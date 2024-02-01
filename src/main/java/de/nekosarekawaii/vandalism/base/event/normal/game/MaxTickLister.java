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

package de.nekosarekawaii.vandalism.base.event.normal.game;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MaxTickLister {

    void onMaxTicks(final MaxTickEvent event);

    class MaxTickEvent extends AbstractEvent<MaxTickLister> {

        public static final int ID = 35;

        public int maxTicks;
        public int ticks;
        public int minTicks;

        public MaxTickEvent(final int maxTicks, final int ticks) {
            this.maxTicks = maxTicks;
            this.ticks = ticks;
            this.minTicks = Math.min(maxTicks, ticks);
        }

        @Override
        public void call(final MaxTickLister tickTimeListener) {
            tickTimeListener.onMaxTicks(this);
        }
    }

}
