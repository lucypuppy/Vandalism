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

package de.nekosarekawaii.vandalism.integration.render.shader.uniform.vector;

import net.minecraft.util.math.MathHelper;

public class Vec2i {

    public static final Vec2i ZERO = new Vec2i(0, 0);

    public final int x;
    public final int y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i multiply(int value) {
        return new Vec2i(this.x * value, this.y * value);
    }

    public int dot(Vec2i vec) {
        return this.x * vec.x + this.y * vec.y;
    }

    public Vec2i add(Vec2i vec) {
        return new Vec2i(this.x + vec.x, this.y + vec.y);
    }

    public Vec2i add(int value) {
        return new Vec2i(this.x + value, this.y + value);
    }

    public boolean equals(Vec2i other) {
        return this.x == other.x && this.y == other.y;
    }

    public Vec2i normalize() {
        int f = (int) MathHelper.sqrt(this.x * this.x + this.y * this.y);
        return f < 1.0E-4F ? ZERO : new Vec2i(this.x / f, this.y / f);
    }

    public int length() {
        return (int) MathHelper.sqrt(this.x * this.x + this.y * this.y);
    }

    public int lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public int distanceSquared(Vec2i vec) {
        int f = vec.x - this.x;
        int g = vec.y - this.y;
        return f * f + g * g;
    }

    public Vec2i negate() {
        return new Vec2i(-this.x, -this.y);
    }
}
