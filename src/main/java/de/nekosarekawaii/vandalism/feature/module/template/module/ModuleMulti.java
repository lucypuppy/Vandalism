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

package de.nekosarekawaii.vandalism.feature.module.template.module;

import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModuleMulti<T extends Module> implements ValueParent, MinecraftWrapper {

    private final List<Value<?>> values = new ArrayList<>();

    @Getter
    private final String name;
    public final @Nullable T parent;

    public ModuleMulti(final String name) {
        this.name = name;
        this.parent = null;
    }

    public ModuleMulti(final String name, final @Nullable T parent) {
        this.name = name;
        this.parent = parent;
    }

    public void onActivate() {
    }

    public void onDeactivate() {
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

}