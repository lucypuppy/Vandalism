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

package de.nekosarekawaii.vandalism.base.value;

import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.ButtonValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.SeparatorValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

import java.util.List;

public interface ValueParent extends IName {

    List<Value<?>> getValues();

    default Value<?> byName(final String name) {
        for (final Value<?> value : this.getValues()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    default void renderValues() {
        this.renderValues(true);
    }

    default void renderValues(final boolean renderNames) {
        for (final Value<?> value : this.getValues()) {
            this.renderValue(value, renderNames);
        }
    }

    default void renderValue(final Value<?> value) {
        this.renderValue(value, true);
    }

    default void renderValue(final Value<?> value, final boolean renderName) {
        if (value.isVisible() == null || value.isVisible().getAsBoolean()) {
            if (value instanceof final ValueGroup valueGroup) {
                value.render();
                if (!valueGroup.wasOpen()) {
                    this.renderValueDescription(value);
                }
                return;
            }
            final boolean isRenderValue = value instanceof ButtonValue || value instanceof SeparatorValue;
            if (renderName && !isRenderValue) {
                ImGui.text(value.getName());
                this.renderValueDescription(value);
                if (!(value instanceof ColorValue)) {
                    ImGui.sameLine();
                }
            }
            value.render();
            if (!(value instanceof ColorValue) && !(value instanceof StringValue) && !(value instanceof ModuleModeValue)) {
                this.renderValueDescription(value);
                if (!isRenderValue && (ImGui.isItemClicked(ImGuiMouseButton.Middle))) {
                    value.resetValue();
                }
            }
        }
    }

    default void renderValueDescription(final Value<?> value) {
        if (value.getDescription() == null) {
            return;
        }
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(value.getDescription());
            ImGui.endTooltip();
        }
    }

}
