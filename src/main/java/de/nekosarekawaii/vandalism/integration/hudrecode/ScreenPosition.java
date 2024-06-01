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

package de.nekosarekawaii.vandalism.integration.hudrecode;

import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector2i;

@Getter
public class ScreenPosition implements MinecraftWrapper {
    private final Vector2i absolute;
    private final Vector2f relative;

    private ScreenPosition(final Vector2i absolute, final Vector2f relative) {
        this.absolute = absolute;
        this.relative = relative;
    }

    public static ScreenPosition Absolute(final int x, final int y) {
        return new ScreenPosition(
                new Vector2i(x, y),
                new Vector2f((float) x / mc.getWindow().getScaledWidth(), (float) y / mc.getWindow().getScaledHeight())
        );
    }

    public static ScreenPosition Relative(final float x, final float y) {
        return new ScreenPosition(
                new Vector2i((int) x * mc.getWindow().getScaledWidth(), (int) y * mc.getWindow().getScaledHeight()),
                new Vector2f(x, y)
        );
    }

    public ScreenPosition rescale(final ScreenPosition position) {
        this.absolute.set(position.absolute);
        this.relative.set(position.relative);
        return this;
    }

    public ScreenPosition copy() {
        return new ScreenPosition(
                new Vector2i(this.absolute),
                new Vector2f(this.relative)
        );
    }
}