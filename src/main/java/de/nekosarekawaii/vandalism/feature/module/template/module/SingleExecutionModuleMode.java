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

import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.module.Module;

public abstract class SingleExecutionModuleMode<M extends Module> extends ModuleMulti<M> {

    protected final IntegerValue times = new IntegerValue(
            this,
            "Times",
            "The amount of times the function should be executed.",
            1,
            1,
            100
    );

    public SingleExecutionModuleMode(final String name, final M parent) {
        super(name, parent);
    }

    protected abstract void onExecute();

    @Override
    public void onActivate() {
        for (int i = 0; i < this.times.getValue(); i++) {
            if (mc.player == null) {
                continue;
            }
            this.onExecute();
        }
        this.parent.deactivate();
    }

}
