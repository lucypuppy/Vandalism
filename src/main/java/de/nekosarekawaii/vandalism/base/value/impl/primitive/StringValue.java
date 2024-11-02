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

package de.nekosarekawaii.vandalism.base.value.impl.primitive;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.type.ImBoolean;
import imgui.type.ImString;

public class StringValue extends Value<String> {

    private final boolean containsPassword;
    private final ImBoolean showPassword;

    public StringValue(ValueParent parent, String name, String description, String defaultValue) {
        super(parent, name, description, defaultValue);
        this.containsPassword = false;
        this.showPassword = new ImBoolean(false);
    }

    public StringValue(ValueParent parent, String name, String description, String defaultValue, final boolean containsPassword) {
        super(parent, name, description, defaultValue);
        this.containsPassword = containsPassword;
        this.showPassword = new ImBoolean(false);
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        this.setValue(mainNode.get(this.getName()).getAsString());
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(this.getName(), this.getValue());
    }

    @Override
    public void render() {
        final String id = "##" + this.getName() + this.getParent().getName();
        final int flags = ImGuiInputTextFlags.CallbackResize | (!this.showPassword.get() && this.containsPassword ? ImGuiInputTextFlags.Password : 0);
        final ImString input = new ImString(this.getValue());
        ImGui.setNextItemWidth(ImGui.getColumnWidth() - 2);
        if (ImGui.inputText(id, input, flags)) {
            this.setValue(input.get());
        }
        if (this.getDescription() != null && ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(this.getDescription());
            ImGui.endTooltip();
        }
        if (ImGui.isItemClicked(ImGuiMouseButton.Middle)) {
            this.resetValue();
        }
        if (this.containsPassword) {
            ImGui.sameLine();
            ImGui.checkbox(id + "showPassword", this.showPassword);
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Shows the content of " + this.getName() + ".");
                ImGui.endTooltip();
            }
        }
    }

}
