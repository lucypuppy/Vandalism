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

package de.nekosarekawaii.vandalism.base.value.impl.misc;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.template.ValueNoOpConfig;
import imgui.ImGui;

import java.util.function.Consumer;

public class ButtonValue extends ValueNoOpConfig<Consumer<ButtonValue>> {

    public ButtonValue(ValueParent parent, String name, String description, Consumer<ButtonValue> defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void render() {
        if (ImGui.button(this.getName() + "##" + this.getName() + this.getParent().getName())) {
            this.getValue().accept(this);
        }
        ImGui.spacing();
    }

}
