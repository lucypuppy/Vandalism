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

package de.nekosarekawaii.vandalism.event.normal.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MoveInputListener {

    void onMoveInput(final MoveInputEvent event);

    class MoveInputEvent extends AbstractEvent<MoveInputListener> {

        public static final int ID = 21;

        public float movementForward, movementSideways;
        public final boolean slowDown;
        public final float slowDownFactor;

        public MoveInputEvent(final float movementForward, final float movementSideways, final boolean slowDown, final float slowDownFactor) {
            this.movementForward = movementForward;
            this.movementSideways = movementSideways;
            this.slowDown = slowDown;
            this.slowDownFactor = slowDownFactor;
        }

        @Override
        public void call(final MoveInputListener listener) {
            listener.onMoveInput(this);
        }

    }
}
