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

package de.nekosarekawaii.vandalism.base.value.impl.misc;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.util.interfaces.Easing;
import imgui.ImGui;
import imgui.type.ImInt;

public class EasingTypeValue extends Value<Easing> {

    public EasingTypeValue(ValueParent parent, String name, String description, Easing defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void load(JsonObject mainNode) {
        if (!mainNode.has(this.getName())) return;
        this.setValue(Easing.getDefaultEasingByName(mainNode.get(this.getName()).getAsString()));
    }

    @Override
    public void save(JsonObject mainNode) {
        mainNode.addProperty(this.getName(), Easing.getDefaultEasingName(this.getValue()));
    }

    @Override
    public void render() {
        final ImInt selectedIndex = new ImInt(Easing.getDefaultEasingFunctions().indexOf(Easing.getDefaultEasingName(this.getValue())));
        ImGui.setNextItemWidth(ImGui.getColumnWidth() - 2);
        if (ImGui.combo("##" + this.getName() + this.getParent().getName(), selectedIndex, Easing.getDefaultEasingFunctions().toArray(String[]::new))) {
            this.setValue(Easing.getDefaultEasingByName(Easing.getDefaultEasingFunctions().get(selectedIndex.get())));
        }
    }
}
