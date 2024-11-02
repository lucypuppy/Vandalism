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

package de.nekosarekawaii.vandalism.event.game;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface MouseInputListener {

    void onMouse(final MouseEvent event);

    class MouseEvent extends CancellableEvent<MouseInputListener> {

        public static final int ID = 8;

        public final Type type;
        public int button, action, mods;
        public double horizontal, vertical;
        public double x, y;

        public MouseEvent(final int button, final int action, final int mods) {
            this.type = Type.BUTTON;

            this.button = button;
            this.action = action;
            this.mods = mods;
        }

        public MouseEvent(final boolean scroll, final double x, final double y) {
            this.type = scroll ? Type.SCROLL : Type.POS;

            if (scroll) {
                this.horizontal = x;
                this.vertical = y;
            } else {
                this.x = x;
                this.y = y;
            }
        }

        @Override
        public void call(final MouseInputListener listener) {
            listener.onMouse(this);
        }

    }

    enum Type {
        BUTTON, SCROLL, POS
    }

}
