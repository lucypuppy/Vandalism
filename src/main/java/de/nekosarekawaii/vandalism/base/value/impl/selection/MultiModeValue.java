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

package de.nekosarekawaii.vandalism.base.value.impl.selection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.integration.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultiModeValue extends Value<List<String>> {

    private final ImString searchInput = new ImString();
    private final ImBoolean onlyShowSelected = new ImBoolean(false);

    private final List<String> options;

    public MultiModeValue(ValueParent parent, String name, String description, final String... options) {
        this(parent, name, description, new ArrayList<>(), options);
    }

    public MultiModeValue(ValueParent parent, String name, String description, List<String> defaultValue, final String... options) {
        super(parent, name, description, defaultValue);
        this.setValue(new ArrayList<>(defaultValue)); // Make sure the list is modifiable
        this.options = new ArrayList<>();
        for (final String option : options) {
            if (!this.options.contains(option)) {
                this.options.add(option);
            }
        }
    }

    @Override
    public void resetValue() {
        this.setValue(new ArrayList<>(this.getDefaultValue()));
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        this.getValue().clear(); // To prevent duplicates
        final JsonArray selectedOptionsNode = mainNode.get(this.getName()).getAsJsonArray();
        for (final JsonElement selectedOptionNode : selectedOptionsNode) {
            final String selectedOption = selectedOptionNode.getAsString();
            if (!this.options.contains(selectedOption)) {
                continue;
            }
            if (this.getValue().contains(selectedOption)) {
                continue;
            }
            this.getValue().add(selectedOption);
        }
    }

    @Override
    public void save(final JsonObject mainNode) {
        final JsonArray selectedOptionsNode = new JsonArray();
        for (final String value : this.getValue()) {
            selectedOptionsNode.add(value);
        }
        mainNode.add(this.getName(), selectedOptionsNode);
    }

    @Override
    public void render() {
        ImGui.setNextItemWidth(ImGui.getColumnWidth() - 2);
        final String id = "##" + this.getName() + this.getParent().getName();
        if (ImGui.beginCombo(id, this.getValue().toString().substring(1, this.getValue().toString().length() - 1), ImGuiComboFlags.HeightLargest)) {
            ImGui.separator();
            ImGui.text("Search for " + this.getName() + " (" + this.options.size() + ")");
            ImGui.setNextItemWidth(Math.max(350, ImGui.getColumnWidth()));
            ImGui.inputText(id + "search", this.searchInput);
            int selectedCount = 0;
            for (final String value : this.options) {
                if (this.isSelected(value)) {
                    selectedCount++;
                }
            }
            ImGui.checkbox("Only Show Selected (" + selectedCount + ")", this.onlyShowSelected);
            if (ImGui.button("Select All" + id + "selectAll", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                if (this.searchInput.isEmpty()) {
                    this.getValue().clear();
                    this.getValue().addAll(this.options);
                } else {
                    final List<String> oldValues = new ArrayList<>(this.getValue());
                    this.getValue().clear();
                    for (final String value : this.options) {
                        if (StringUtils.contains(value, this.searchInput.get())) {
                            this.getValue().add(value);
                        }
                    }
                    for (final String oldValue : oldValues) {
                        if (this.getValue().contains(oldValue)) {
                            continue;
                        }
                        this.getValue().add(oldValue);
                    }
                }
            }
            ImGui.sameLine();
            if (ImGui.button("Deselect All" + id + "deselectAll", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                if (this.searchInput.isEmpty()) {
                    this.getValue().clear();
                } else {
                    for (final String value : this.options) {
                        if (StringUtils.contains(value, this.searchInput.get())) {
                            this.getValue().remove(value);
                        }
                    }
                }
            }
            if (ImGui.button("Close" + id + "close", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                ImGui.closeCurrentPopup();
            }
            ImGui.separator();
            ImGui.spacing();
            for (final String value : this.options) {
                if (this.searchInput.isNotEmpty() && !StringUtils.contains(value, this.searchInput.get())) {
                    continue;
                }
                final boolean isSelected = this.isSelected(value);
                if (isSelected) {
                    final Color color = Vandalism.getInstance().getClientSettings().getMenuSettings().multiModeSelectionColor.getColor();
                    final float[] colorArray = new float[]{ color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f };
                    ImGui.pushStyleColor(ImGuiCol.Button, colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
                } else if (this.onlyShowSelected.get()) {
                    continue;
                }
                if (ImUtils.subButton(value)) {
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