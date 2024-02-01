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

import de.nekosarekawaii.vandalism.base.value.template.ValueModeGeneric;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

public class ModuleModeValue<T extends AbstractModule> extends ValueModeGeneric<ModuleMulti<T>> {

    @SafeVarargs
    public ModuleModeValue(final AbstractModule parent, final String name, final String description, final ModuleMulti<T>... options) {
        super(parent, name, description, ModuleMulti::getName, mn -> {
            for (final ModuleMulti<T> module : options) {
                if (module.getName().equals(mn)) {
                    return module;
                }
            }
            return null;
        }, options);

        this.onValueChange((oldValue, newValue) -> {
            if (parent.isActive()) {
                oldValue.onDeactivate();
                newValue.onActivate();
            }
        });

    }

    @Override
    public void render() {
        super.render();
        if (ImGui.isItemClicked(ImGuiMouseButton.Middle)) {
            this.resetValue();
        }
        this.getValue().renderValues();
    }

}
