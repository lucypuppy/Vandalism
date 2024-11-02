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

package de.nekosarekawaii.vandalism.base.value.impl.selection;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import imgui.ImGui;
import lombok.Getter;

@Getter
public class ClassModeValue<T extends IName> extends Value<T> {

    private final T[] options;

    @SafeVarargs
    public ClassModeValue(ValueParent parent, String name, String description, final T... options) {
        super(parent, name, description, options[0]);
        this.options = options;
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        final String selectedOption = mainNode.get(this.getName()).getAsString();
        for (final T value : this.options) {
            if (value.getName().equals(selectedOption)) {
                this.setValue(value);
                break;
            }
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(this.getName(), this.getValue().getName());
    }

    @Override
    public void render() {
        ImGui.setNextItemWidth(ImGui.getColumnWidth() - 2);
        if (ImGui.beginCombo("##" + this.getName() + this.getParent().getName(), this.getValue().getName())) {
            for (final T mode : this.options) {
                if (ImGui.selectable(mode.getName(), mode.getName().equals(this.getValue().getName()))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

}
