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

package de.nekosarekawaii.vandalism.base.value.template;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class ValueGroup extends Value<List<Value<?>>> implements ValueParent, MinecraftWrapper {

    public ValueGroup(ValueParent parent, String name, String description) {
        super(parent, name, description, new ArrayList<>());
    }

    @Override
    public void load(final JsonObject valueObject) {
        final var valueNode = valueObject.getAsJsonObject(this.getName());
        for (final Value<?> value : this.getValues()) {
            value.load(valueNode);
        }
    }

    @Override
    public void save(final JsonObject valueObject) {
        final var valueNode = new JsonObject();
        for (final Value<?> value : this.getValues()) {
            value.save(valueNode);
        }
        valueObject.add(this.getName(), valueNode);
    }

    @Override
    public void render() {
        if (ImGui.treeNodeEx(this.getName() + "##" + this.getName() + this.getParent().getName())) {
            this.renderValues();
            ImGui.treePop();
        }
    }

    @Override
    public List<Value<?>> getValues() {
        return this.getValue();
    }

    @Override
    public void resetValue() {
        for (final Value<?> value : this.getValue()) {
            value.resetValue();
        }
    }

}
