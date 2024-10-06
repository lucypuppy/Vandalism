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

package de.nekosarekawaii.vandalism.base.value;

import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.ButtonValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.SeparatorValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleModeValue;
import de.nekosarekawaii.vandalism.util.IName;
import de.nekosarekawaii.vandalism.util.StringUtils;
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
        this.renderValues(renderNames, "");
    }

    default void renderValues(final String containingText) {
        this.renderValues(true, containingText);
    }

    default void renderValues(final boolean renderNames, final String containingText) {
        for (final Value<?> value : this.getValues()) {
            this.renderValue(value, renderNames, containingText);
        }
    }

    default void renderValuesExcept(final Value<?>... excepts) {
        for (final Value<?> value : this.getValues()) {
            boolean render = true;
            for (final Value<?> except : excepts) {
                if (value == except) {
                    render = false;
                    break;
                }
            }
            if (render) {
                this.renderValue(value);
            }
        }
    }

    default void renderValue(final Value<?> value) {
        this.renderValue(value, true);
    }

    default void renderValue(final Value<?> value, final boolean renderName) {
        this.renderValue(value, renderName, "");
    }

    default void renderValue(final Value<?> value, final boolean renderName, final String containingText) {
        if (value.isVisible() == null || value.isVisible().getAsBoolean()) {
            final String name = value.getName();
            if (!containingText.isEmpty() && (
                    !StringUtils.contains(name, containingText) &&
                            !StringUtils.contains(name.replace(" ", ""), containingText) &&
                            (value.getDescription() == null || !StringUtils.contains(value.getDescription(), containingText))
            )) {
                return;
            }
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
            if (!(value instanceof ColorValue) && !(value instanceof StringValue) && !(value instanceof ModuleModeValue) && !(value instanceof KeyBindValue)) {
                this.renderValueDescription(value);
                if (!isRenderValue && (ImGui.isItemClicked(ImGuiMouseButton.Middle))) {
                    value.resetValue();
                }
            }
        }
    }

    default void renderValueDescription(final Value<?> value) {
        if (value.getDescription() != null && ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(value.getDescription());
            ImGui.endTooltip();
        }
    }

}
