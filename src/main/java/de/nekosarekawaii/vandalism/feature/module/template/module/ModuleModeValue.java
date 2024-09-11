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

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.value.template.ValueModeGeneric;
import de.nekosarekawaii.vandalism.feature.module.Module;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

public class ModuleModeValue<T extends Module> extends ValueModeGeneric<ModuleMulti<T>> {

    @SafeVarargs
    public ModuleModeValue(final Module parent, final String name, final String description, final ModuleMulti<T>... options) {
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
    public void save(final JsonObject mainNode) {
        super.save(mainNode);
        for (final ModuleMulti<T> option : getOptions()) {
            if (option.getValues().isEmpty()) {
                continue;
            }
            final JsonObject optionNode = new JsonObject();
            ConfigWithValues.saveValues(optionNode, option.getValues());
            mainNode.add(option.getName(), optionNode);
        }
    }

    @Override
    public void load(final JsonObject mainNode) {
        super.load(mainNode);
        for (final ModuleMulti<T> option : getOptions()) {
            final JsonObject optionNode = mainNode.getAsJsonObject(option.getName());
            if (optionNode != null) {
                ConfigWithValues.loadValues(optionNode, option.getValues());
            }
        }
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
