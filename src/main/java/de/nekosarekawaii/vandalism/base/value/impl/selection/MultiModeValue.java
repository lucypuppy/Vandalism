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

package de.nekosarekawaii.vandalism.base.value.impl.selection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.feature.module.gui.ModulesClientMenuWindow;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiModeValue extends Value<List<String>> {

    private final ImString searchInput = new ImString();

    private final List<String> options;

    public MultiModeValue(ValueParent parent, String name, String description, final String... options) {
        this(parent, name, description, new ArrayList<>(), options);
    }

    public MultiModeValue(ValueParent parent, String name, String description, List<String> defaultValue, final String... options) {
        super(parent, name, description, defaultValue, new ArrayList<>(defaultValue)); // Java's Arrays.asList() makes lists unmodifiable
        this.options = Arrays.asList(options);
    }

    @Override
    public void resetValue() {
        this.setValue(new ArrayList<>(this.getDefaultValue()));
    }

    @Override
    public void load(final JsonObject mainNode) {
        this.getValue().clear(); // To prevent duplicates
        final var selectedOptionsNode = mainNode.get(this.getName()).getAsJsonArray();
        for (final JsonElement element : selectedOptionsNode) {
            final String value = element.getAsString();
            if (this.getValue().contains(value)) {
                continue;
            }
            this.getValue().add(value);
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        final var selectedOptionsNode = new JsonArray();
        for (final String value : this.getValue()) {
            selectedOptionsNode.add(value);
        }
        mainNode.add(this.getName(), selectedOptionsNode);
    }

    @Override
    public void render() {
        if (ImGui.beginCombo("##" + this.getName() + this.getParent().getName(), this.getValue().toString().substring(1, this.getValue().toString().length() - 1), ImGuiComboFlags.HeightLargest)) {
            ImGui.separator();
            ImGui.text("Search for " + this.getName());
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##" + this.getName() + this.getParent().getName() + "search", this.searchInput);
            ImGui.separator();
            ImGui.spacing();
            for (final String value : this.options) {
                if (this.searchInput.isNotEmpty() && !StringUtils.contains(value, this.searchInput.get())) {
                    continue;
                }
                final boolean isSelected = this.isSelected(value);
                final float[] color;
                if (isSelected) color = ModulesClientMenuWindow.ACTIVE_COLOR;
                else color = ModulesClientMenuWindow.INACTIVE_COLOR;
                if (isSelected) {
                    ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
                }
                if (ImGui.button(value, -1, 25)) {
                    if (isSelected) this.getValue().remove(value);
                    else this.getValue().add(value);
                }
                if (isSelected) {
                    ImGui.popStyleColor(3);
                }
            }
            ImGui.endCombo();
        }
    }

    public boolean isSelected(final String value) {
        return this.getValue().contains(value);
    }

}