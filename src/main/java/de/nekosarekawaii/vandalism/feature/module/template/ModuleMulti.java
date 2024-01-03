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

package de.nekosarekawaii.vandalism.feature.module.template;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;

public abstract class ModuleMulti<T extends AbstractModule> implements MinecraftWrapper {

    private final String name;
    private final T parent;

    public ModuleMulti(final String name, final T parent) {
        this.name = name;
        this.parent = parent;
    }

    public void onActivate() {
    }

    public void onDeactivate() {
    }

    public String getName() {
        return this.name;
    }

    public T getParent() {
        return this.parent;
    }

}