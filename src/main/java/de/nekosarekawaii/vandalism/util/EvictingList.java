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

package de.nekosarekawaii.vandalism.util;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of an evicting list. This list will remove the first entry if the list is full.
 *
 * @param <V> The type of the list.
 */
public class EvictingList<V> {

    private final List<V> list;
    private final int maxSize;

    /**
     * Dummy constructor.
     */
    public EvictingList() {
        this(Collections.emptyList(), 0);
    }

    /**
     * Creates a new evicting list.
     *
     * @param list    The list.
     * @param maxSize The maximum size of the list.
     */
    public EvictingList(final List<V> list, final int maxSize) {
        this.list = list;
        this.maxSize = maxSize;
    }

    /**
     * Adds a value to the list. If the list is full, the first entry will be removed. Returns true if the list was full.
     *
     * @param value The value to add.
     * @return True if the list was full.
     */
    public boolean add(final V value) {
        final boolean full = this.list.size() >= this.maxSize;
        if (full) {
            this.list.remove(this.list.get(0));
        }
        this.list.add(value);
        return full;
    }

    /**
     * @return True if the list is full.
     */
    public boolean isFull() {
        return this.list.size() >= this.maxSize;
    }

    public List<V> getNormalList() {
        return this.list;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

}