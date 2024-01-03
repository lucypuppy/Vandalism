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
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiBlockValue extends MultiModeValue {

    public MultiBlockValue(ValueParent parent, String name, String description, final Block... options) {
        this(parent, name, description, Collections.emptyList(), options);
    }

    public MultiBlockValue(ValueParent parent, String name, String description, final List<Block> defaultValue, final Block... options) {
        super(parent, name, description, defaultValue.stream().map(block -> Registries.BLOCK.getId(block).toString()).toList(), Arrays.stream(options).map(block -> Registries.BLOCK.getId(block).toString()).toArray(String[]::new));
    }

    public boolean isSelected(final Identifier value) {
        return this.getValue().contains(value.toString());
    }

    public boolean isSelected(final Block value) {
        return this.getValue().contains(Registries.BLOCK.getId(value).toString());
    }

}
