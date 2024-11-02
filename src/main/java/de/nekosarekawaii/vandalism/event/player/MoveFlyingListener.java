/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MoveFlyingListener {

    void onMoveFlying(final MoveFlyingEvent event);

    class MoveFlyingEvent extends AbstractEvent<MoveFlyingListener> {

        public static final int ID = 20;

        public double sidewaysSpeed, upwardSpeed, forwardSpeed;

        public MoveFlyingEvent(final double sidewaysSpeed, final double upwardSpeed, final double forwardSpeed) {
            this.sidewaysSpeed = sidewaysSpeed;
            this.upwardSpeed = upwardSpeed;
            this.forwardSpeed = forwardSpeed;
        }

        @Override
        public void call(final MoveFlyingListener listener) {
            listener.onMoveFlying(this);
        }

    }

}
