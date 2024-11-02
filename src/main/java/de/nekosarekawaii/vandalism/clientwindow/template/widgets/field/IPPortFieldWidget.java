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

package de.nekosarekawaii.vandalism.clientwindow.template.widgets.field;

import de.nekosarekawaii.vandalism.util.math.MathUtil;
import imgui.ImGui;
import imgui.type.ImInt;

public interface IPPortFieldWidget extends IPFieldWidget {

    default ImInt createImPort() {
        return new ImInt(25565);
    }

    ImInt getImPort();

    default boolean isValidPort() {
        return MathUtil.isBetween(this.getImPort().get(), 0, 65535);
    }

    @Override
    default void onDataSplit(final String[] data, final boolean resolved) {
        IPFieldWidget.super.onDataSplit(data, resolved);
        if (data.length > 1) {
            try {
                final int newPort = Integer.parseInt(data[1]);
                if (resolved && newPort == 25565) return;
                this.getImPort().set(newPort);
            } catch (final NumberFormatException ignored) {
            }
        }
    }

    @Override
    default void renderField(final String id) {
        IPFieldWidget.super.renderField(id);
        ImGui.text("Port");
        ImGui.setNextItemWidth(-1);
        ImGui.inputInt(id + "Port", this.getImPort());
    }

}
