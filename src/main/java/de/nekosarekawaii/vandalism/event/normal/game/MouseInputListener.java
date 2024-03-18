/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.event.normal.game;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MouseInputListener {

    default void onMouseButton(final int button, final int action, final int mods) {
    }

    default void onMouseScroll(final double horizontal, final double vertical) {
    }

    default void onCursorPos(final double x, final double y) {
    }

    class MouseEvent extends AbstractEvent<MouseInputListener> {

        public static final int ID = 8;

        private final Type type;
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
            switch (this.type) {
                case BUTTON -> listener.onMouseButton(button, action, mods);
                case SCROLL -> listener.onMouseScroll(horizontal, vertical);
                case POS -> listener.onCursorPos(x, y);
            }
        }

    }

    enum Type {
        BUTTON, SCROLL, POS
    }

}
