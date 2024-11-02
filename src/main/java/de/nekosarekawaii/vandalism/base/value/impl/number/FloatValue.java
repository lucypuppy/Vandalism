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

package de.nekosarekawaii.vandalism.base.value.impl.number;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.template.ValueNumber;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImFloat;

public class FloatValue extends ValueNumber<Float> {

    public FloatValue(ValueParent parent, String name, String description, Float defaultValue, Float minValue, Float maxValue) {
        super(parent, name, description, defaultValue, minValue, maxValue);
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        this.setValue(mainNode.get(this.getName()).getAsFloat());
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(this.getName(), this.getValue());
    }

    @Override
    public void render() {
        final ImFloat nextValue = new ImFloat(this.getValue());
        ImGui.setNextItemWidth(ImGui.getColumnWidth() - 2);
        if (ImGui.sliderScalar("##" + this.getName() + this.getParent().getName(), ImGuiDataType.Float, nextValue, this.getMinValue(), this.getMaxValue(), "%.3f")) {
            this.setValue(nextValue.get());
        }
    }

}
