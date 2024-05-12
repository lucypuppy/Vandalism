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

package de.nekosarekawaii.vandalism.integration.hudrecode;

import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.function.Supplier;

@Getter
public class Position {

    private static final Supplier<ScreenPosition> DEFAULT_SCREEN_POSITION = () -> ScreenPosition.Relative(.4F, .4F);

    private final ScreenPosition currentPosition;
    private final ScreenPosition defaultPosition;

    public Position() {
        this(DEFAULT_SCREEN_POSITION.get(), DEFAULT_SCREEN_POSITION.get());
    }

    public Position(final ScreenPosition currentPosition, final ScreenPosition defaultPosition) {
        this.currentPosition = currentPosition;
        this.defaultPosition = defaultPosition;
    }

    public void reset() {
        currentPosition.rescale(defaultPosition.copy());
    }

    public Vector2f relative() {
        return currentPosition.getRelative();
    }

    public Vector2i absolute() {
        return currentPosition.getAbsolute();
    }
}
