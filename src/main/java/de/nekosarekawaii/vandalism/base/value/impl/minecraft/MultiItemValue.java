/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiItemValue extends MultiModeValue {

    public MultiItemValue(ValueParent parent, String name, String description, final Item... options) {
        this(parent, name, description, Collections.emptyList(), options);
    }

    public MultiItemValue(ValueParent parent, String name, String description, final List<Item> defaultValue, final Item... options) {
        super(parent, name, description, defaultValue.stream().map(item -> Registries.ITEM.getId(item).toString()).toList(), Arrays.stream(options).map(item -> Registries.ITEM.getId(item).toString()).toArray(String[]::new));
    }

    public boolean isSelected(final Identifier value) {
        return this.getValue().contains(value.toString());
    }

    public boolean isSelected(final Item value) {
        return this.getValue().contains(Registries.ITEM.getId(value).toString());
    }

}
