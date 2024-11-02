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

package de.nekosarekawaii.vandalism.util;

public class Counter {

    private int value;
    private final int max;

    public Counter(final int value, final int max) {
        this.value = value;
        this.max = max;
    }

    public final int value() {
        return this.value;
    }

    public final void count() throws CounterMaxReachedException {
        this.value++;
        if (this.value > this.max) {
            throw new CounterMaxReachedException();
        }
    }

    public static class CounterMaxReachedException extends RuntimeException {

    }
}
