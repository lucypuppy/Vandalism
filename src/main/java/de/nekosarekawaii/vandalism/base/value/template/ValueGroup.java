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

package de.nekosarekawaii.vandalism.base.value.template;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class ValueGroup extends Value<List<Value<?>>> implements ValueParent, MinecraftWrapper {

    private boolean wasOpen = false;

    public ValueGroup(ValueParent parent, String name, String description) {
        super(parent, name, description, new ArrayList<>());
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        final JsonObject valueNode = mainNode.getAsJsonObject(this.getName());
        for (final Value<?> value : this.getValues()) {
            value.load(valueNode);
        }
        final String wasOpenName = this.getName() + " was open";
        if (valueNode.has(wasOpenName)) {
            this.wasOpen = valueNode.get(wasOpenName).getAsBoolean();
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        final JsonObject valueNode = new JsonObject();
        for (final Value<?> value : this.getValues()) {
            value.save(valueNode);
        }
        valueNode.addProperty(this.getName() + " was open", this.wasOpen);
        mainNode.add(this.getName(), valueNode);
    }

    @Override
    public void render() {
        if (ImGui.treeNodeEx(this.getName() + "##" + this.getName() + this.getParent().getName(), this.wasOpen ? ImGuiTreeNodeFlags.DefaultOpen : 0)) {
            this.wasOpen = true;
            this.renderValues();
            ImGui.treePop();
        } else {
            this.wasOpen = false;
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

    public boolean wasOpen() {
        return this.wasOpen;
    }

}
