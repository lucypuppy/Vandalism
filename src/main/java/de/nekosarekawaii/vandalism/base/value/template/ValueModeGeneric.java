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

package de.nekosarekawaii.vandalism.base.value.template;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import imgui.ImGui;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ValueModeGeneric<T> extends Value<T> {

    @Getter
    private final List<T> options;
    private final Function<T, String> toString;
    private final Function<String, T> fromString;

    @SafeVarargs
    public ValueModeGeneric(ValueParent parent, String name, String description, Function<T, String> toString, Function<String, T> fromString, final T... options) {
        super(parent, name, description, options[0]);
        this.toString = toString;
        this.fromString = fromString;
        this.options = Arrays.stream(options).toList();
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        final T value = this.fromString.apply(mainNode.get(this.getName()).getAsString());
        if (!this.options.contains(value)) return;
        this.setValue(value);
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(this.getName(), this.toString.apply(this.getValue()));
    }

    @Override
    public void render() {
        final String selectedString = this.toString.apply(this.getValue());
        ImGui.setNextItemWidth(ImGui.getColumnWidth() - 2);
        if (ImGui.beginCombo("##" + this.getName() + this.getParent().getName(), selectedString)) {
            for (final T mode : this.options) {
                final String modeString = this.toString.apply(mode);
                if (ImGui.selectable(modeString, modeString.equals(selectedString))) {
                    this.setValue(mode);
                }
            }
            ImGui.endCombo();
        }
    }

    public int getSelectedIndex() {
        return this.options.indexOf(this.getValue());
    }

}
