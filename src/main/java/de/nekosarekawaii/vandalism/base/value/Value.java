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

package de.nekosarekawaii.vandalism.base.value;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

public abstract class Value<V> implements IName {

    @Getter
    private final ValueParent parent;
    private final String name;
    @Getter
    private final @Nullable String description;

    @Getter
    private final V defaultValue;
    @Getter
    private V value;
    private BiConsumer<V, V> valueChangeConsumer;
    private BooleanSupplier visibleCondition;

    public Value(ValueParent parent, String name, @Nullable String description, V defaultValue) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        parent.getValues().add(this);
        if (this.description != null && this.description.trim().isEmpty()) {
            throw new IllegalStateException("Description cannot be empty, use null instead.");
        }
    }

    public void setValue(V value) {
        final V oldValue = this.value;
        this.value = value;
        if (this.valueChangeConsumer != null) {
            // Allows the event to change the value
            this.valueChangeConsumer.accept(oldValue, value);
        }
    }

    public <S extends Value<V>> S onValueChange(final BiConsumer<V, V> valueChangeConsumer) {
        this.valueChangeConsumer = valueChangeConsumer;
        return (S) this;
    }

    public <S extends Value<V>> S visibleCondition(final BooleanSupplier visible) {
        this.visibleCondition = visible;
        return (S) this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void resetValue() {
        this.setValue(this.defaultValue);
    }

    public BooleanSupplier isVisible() {
        return this.visibleCondition;
    }

    public abstract void load(final JsonObject mainNode);

    public abstract void save(final JsonObject mainNode);

    public abstract void render();

}
