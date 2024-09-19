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

package de.nekosarekawaii.vandalism.event.render;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.text.Style;

public interface TextDrawListener {

    void onTextDraw(final TextDrawEvent event);

    class TextDrawEvent extends AbstractEvent<TextDrawListener> {

        public static final int ID = 31;

        public String text;
        public Style startingStyle;

        public TextDrawEvent(final String text, final Style startingStyle) {
            this.text = text;
            this.startingStyle = startingStyle;
        }

        @Override
        public void call(final TextDrawListener listener) {
            listener.onTextDraw(this);
        }

    }

}
