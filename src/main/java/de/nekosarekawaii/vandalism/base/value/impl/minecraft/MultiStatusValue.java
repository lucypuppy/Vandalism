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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiStatusValue extends MultiModeValue {

    public MultiStatusValue(ValueParent parent, String name, String description, final StatusEffect... options) {
        this(parent, name, description, Collections.emptyList(), options);
    }

    public MultiStatusValue(ValueParent parent, String name, String description, final List<StatusEffect> defaultValue, final StatusEffect... options) {
        super(parent, name, description,
                defaultValue.stream().map(effect -> Registries.STATUS_EFFECT.getId(effect).toShortTranslationKey()).toList(),
                Arrays.stream(options).map(effect -> Registries.STATUS_EFFECT.getId(effect).toShortTranslationKey()).toArray(String[]::new)
        );
    }

    public boolean isSelected(final Identifier value) {
        return this.getValue().contains(value.toShortTranslationKey());
    }

    public boolean isSelected(final StatusEffect value) {
        return this.getValue().contains(Registries.STATUS_EFFECT.getId(value).toShortTranslationKey());
    }

}
