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

package de.nekosarekawaii.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.AbstractEvent;
import de.florianmichael.dietrichevents2.StateTypes;

public interface MotionListener {

    default void onPreMotion(final MotionEvent event) {
    }

    default void onPostMotion(final MotionEvent event) {
    }

    class MotionEvent extends AbstractEvent<MotionListener> {

        public static final int ID = 2;

        private final StateTypes state;

        public MotionEvent(final StateTypes state) {
            this.state = state;
        }

        @Override
        public void call(final MotionListener listener) {
            if (this.state == StateTypes.PRE) {
                listener.onPreMotion(this);
            } else {
                listener.onPostMotion(this);
            }
        }
    }

}
