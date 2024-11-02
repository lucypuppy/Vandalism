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

package de.nekosarekawaii.vandalism.base.value.impl.minecraft;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiRegistryValue<K> extends MultiModeValue {

    private final Registry<K> registry;

    public MultiRegistryValue(ValueParent parent, String name, String description, final Registry<K> registry) {
        this(parent, name, description, registry, Collections.emptyList());
    }

    @SafeVarargs
    public MultiRegistryValue(ValueParent parent, String name, String description, final Registry<K> registry, final K... options) {
        this(parent, name, description, registry, Collections.emptyList(), options);
    }

    public MultiRegistryValue(ValueParent parent, String name, String description, final Registry<K> registry, final List<K> defaultValue) {
        this(parent, name, description, registry, defaultValue, registry.stream().toList().toArray((K[]) new Object[registry.size()]));
    }

    @SafeVarargs
    public MultiRegistryValue(ValueParent parent, String name, String description, final Registry<K> registry, final List<K> defaultValue, final K... options) {
        super(
                parent,
                name,
                description,
                defaultValue.stream().map(k -> {
                    final Identifier id = registry.getId(k);
                    return id == null ? null : id.toString();
                }).toList(),
                Arrays.stream(options).map(k -> {
                    final Identifier id = registry.getId(k);
                    return id == null ? null : id.toString();
                }).toArray(String[]::new)
        );
        this.registry = registry;
    }

    public boolean isSelected(final Identifier value) {
        return this.getValue().contains(value.toString());
    }

    public boolean isSelected(final K value) {
        final Identifier id = this.registry.getId(value);
        return id != null && this.getValue().contains(id.toString());
    }

}
