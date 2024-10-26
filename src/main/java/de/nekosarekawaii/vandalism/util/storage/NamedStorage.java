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

package de.nekosarekawaii.vandalism.util.storage;

import de.nekosarekawaii.vandalism.util.interfaces.IName;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Implementation of a named storage. This storage is used to store objects that implement the {@link IName} interface.
 *
 * @param <T> The type of the objects that are stored in this storage.
 */
public abstract class NamedStorage<T extends IName> extends Storage<T> {

    /**
     * Creates a new named storage with a {@link CopyOnWriteArrayList}.
     */
    public NamedStorage() {
        this(CopyOnWriteArrayList::new);
    }

    /**
     * Creates a new named storage with the given list.
     *
     * @param list The list.
     */
    public NamedStorage(final Supplier<List<T>> list) {
        super(list);
    }

    /**
     * Gets an object by its name. see {@link #getByName(String, boolean)} and {@link IName} for more information.
     *
     * @param name The name.
     * @param <V>  The type of the object.
     * @return The object.
     */
    public <V extends T> V getByName(final String name) {
        return getByName(name, false);
    }

    /**
     * Gets an object by its name. see {@link #getByName(String)} and {@link IName} for more information.
     *
     * @param name       The name.
     * @param ignoreCase Whether the case should be ignored.
     * @param <V>        The type of the object.
     * @return The object.
     */
    @SuppressWarnings("unchecked")
    public <V extends T> V getByName(final String name, final boolean ignoreCase) {
        return (V) this.getList().stream().filter(t -> ignoreCase ? t.getName().equalsIgnoreCase(name) : t.getName().equals(name)).findFirst().orElse(null);
    }

}