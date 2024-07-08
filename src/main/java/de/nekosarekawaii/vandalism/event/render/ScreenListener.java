/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.event.render;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenListener {

    default void onOpenScreen(final ScreenEvent event) {
    }

    default void onResizeScreen(final ScreenEvent event) {
    }

    class ScreenEvent extends CancellableEvent<ScreenListener> {

        public static final int ID = 5;

        public final Type type;
        public Screen screen;

        public ScreenEvent(final Screen screen) {
            this.type = Type.OPEN;
            this.screen = screen;
        }

        public ScreenEvent() {
            this.type = Type.RESIZE;
        }

        @Override
        public void call(final ScreenListener listener) {
            if (this.type == Type.OPEN) {
                listener.onOpenScreen(this);
            } else {
                listener.onResizeScreen(this);
            }
        }
    }

    enum Type {
        OPEN, RESIZE
    }

}
