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

package de.nekosarekawaii.vandalism.base.value.impl.minecraft;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import net.minecraft.registry.Registry;

import java.util.Collections;
import java.util.List;

public class MultiRegistryBlacklistValue<K> extends MultiRegistryValue<K> {

    public MultiRegistryBlacklistValue(ValueParent parent, String name, String description, final Registry<K> registry, final List<K> blacklist) {
        this(parent, name, description, registry, blacklist, true);
    }

    public MultiRegistryBlacklistValue(ValueParent parent, String name, String description, final Registry<K> registry, final List<K> blacklist, final boolean defaultEmpty) {
        super(
                parent,
                name,
                description,
                registry,
                defaultEmpty ? Collections.emptyList() : registry.stream().filter(k -> !blacklist.contains(k)).toList(),
                registry.stream().filter(k -> !blacklist.contains(k)).toList().toArray((K[]) new Object[registry.size()])
        );
    }

}
