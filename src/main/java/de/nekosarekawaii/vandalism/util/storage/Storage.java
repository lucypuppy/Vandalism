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

package de.nekosarekawaii.vandalism.util.storage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementation of a storage. A storage is a list of objects. It can be used to store objects of a specific type.
 * The storage can be initialized, and it can be added and removed objects. It also supports consumers for adding and
 * removing objects. The storage is thread-safe. The storage is abstract and must be implemented. The storage can be
 * initialized by calling the {@link #init()} method. The storage can be added and removed objects by calling the
 * {@link #add(Object...)} and {@link #remove(Object...)} methods.
 *
 * @param <T> The type of the storage.
 */
public abstract class Storage<T> {

    /**
     * The list of the storage.
     */
    private final List<T> list;

    /**
     * The add and remove consumer.
     */
    private Consumer<T> addConsumer, removeConsumer;

    /**
     * Creates a new storage with a {@link CopyOnWriteArrayList}.
     */
    public Storage() {
        this(CopyOnWriteArrayList::new);
    }

    /**
     * Creates a new storage with the given list.
     *
     * @param list The list.
     */
    public Storage(final Supplier<List<T>> list) {
        this.list = list.get();
    }

    /**
     * Initializes the storage.
     */
    public abstract void init();

    /**
     * Adds objects to the storage.
     *
     * @param t The objects.
     */
    @SafeVarargs
    public final void add(final T... t) {
        for (T t1 : t) addBy(t1, list.size());
    }

    /**
     * Removes objects from the storage.
     *
     * @param t The objects.
     */
    @SafeVarargs
    public final void remove(T... t) {
        for (T t1 : t) {
            this.list.remove(t1);
            if (this.removeConsumer != null) this.removeConsumer.accept(t1);
        }
    }

    /**
     * Adds an object to the storage by the given index.
     *
     * @param t     The object.
     * @param index The index.
     */
    public void addBy(final T t, final int index) {
        if (list.contains(t)) return;
        this.list.add(index, t);
        if (this.addConsumer != null) this.addConsumer.accept(t);
    }

    /**
     * Inserts an object to the storage by the given index.
     *
     * @param t     The object.
     * @param index The index.
     */
    public void insert(final T t, int index) {
        this.removeConsumer.accept(this.list.get(index));
        this.list.set(index, t);
        this.addConsumer.accept(this.list.get(index));
    }

    /**
     * Removes an object from the storage by the given index.
     *
     * @param index The index.
     */
    public void removeBy(final int index) {
        this.list.remove(index);
    }

    /**
     * Gets an object from the storage by the given class type.
     *
     * @param clazz The class.
     * @param <V>   The type of the object.
     * @return The object.
     */
    @SuppressWarnings("unchecked")
    public <V extends T> V getByClass(final Class<V> clazz) {
        return (V) this.list.stream().filter(t -> t.getClass() == clazz).findFirst().orElse(null);
    }

    public List<T> getList() {
        return this.list;
    }

    public void setAddConsumer(final Consumer<T> addConsumer) {
        this.addConsumer = addConsumer;
    }

    public void setRemoveConsumer(final Consumer<T> removeConsumer) {
        this.removeConsumer = removeConsumer;
    }

}