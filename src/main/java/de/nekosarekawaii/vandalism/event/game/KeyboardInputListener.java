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

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface KeyboardInputListener {

    default void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {}

    default void onCharInput(final long window, final int codePoint, final int modifiers) {}

    class KeyboardInputEvent extends AbstractEvent<KeyboardInputListener> {

        public static final int ID = 6;

        private final Type type;

        public final long window;
        public final int key, codePoint, scanCode, action, modifiers;

        public KeyboardInputEvent(final Type type, final long window, final int key, final int codePoint, final int scanCode, final int action, final int modifiers) {
            this.type = type;
            this.window = window;
            this.key = key;
            this.codePoint = codePoint;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        @Override
        public void call(final KeyboardInputListener listener) {
            if (this.type == Type.KEY) {
                listener.onKeyInput(this.window, this.key, this.scanCode, this.action, this.modifiers);
            } else {
                listener.onCharInput(this.window, this.codePoint, this.modifiers);
            }
        }
    }

    enum Type {
        KEY, CHAR
    }

}
