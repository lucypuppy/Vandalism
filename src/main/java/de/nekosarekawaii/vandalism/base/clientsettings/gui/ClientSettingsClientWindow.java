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

package de.nekosarekawaii.vandalism.base.clientsettings.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ClientSettingsClientWindow extends ClientWindow {

    private final ClientSettings clientSettings;

    private final ImString searchInput = new ImString();

    public ClientSettingsClientWindow(final ClientSettings clientSettings) {
        super("Client Settings", Category.CONFIG, 600f, 500f);
        this.clientSettings = clientSettings;
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##clientsettings";
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(id + "searchInput", this.searchInput);
        ImGui.separator();
        final String searchInput = this.searchInput.get();
        if (ImGui.beginTabBar(id)) {
            for (final Value<?> value : this.clientSettings.getValues()) {
                if (value instanceof final ValueGroup valueGroup) {
                    final String name = valueGroup.getName();
                    final String tabId = "##" + name + "settings";
                    boolean containsSearchInput = false;
                    if (!searchInput.isBlank()) {
                        containsSearchInput = valueGroup.getValues().stream().anyMatch(v ->
                                v.getName().toLowerCase().contains(searchInput.toLowerCase())
                                        || (v.getDescription() != null && v.getDescription().toLowerCase().contains(searchInput.toLowerCase())));
                    }
                    if (containsSearchInput) {
                        ImGui.pushStyleColor(ImGuiCol.Tab, 1.0f, 0.0f, 0.0f, 0.4f);
                        ImGui.pushStyleColor(ImGuiCol.TabHovered, 1.0f, 0.0f, 0.0f, 0.4f);
                        ImGui.pushStyleColor(ImGuiCol.TabActive, 1.0f, 0.0f, 0.0f, 0.4f);
                    }
                    if (ImGui.beginTabItem(name + tabId + "tab")) {
                        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.0f);
                        ImGui.beginChild(tabId + "values", ImGui.getColumnWidth(), (-ImGui.getTextLineHeightWithSpacing()) * 3, true);
                        if (searchInput.isBlank()) valueGroup.renderValues();
                        else valueGroup.renderValues(searchInput);
                        ImGui.endChild();
                        ImGui.popStyleColor();
                        ImGui.separator();
                        final List<Value<?>> values = valueGroup.getValues();
                        if (ImGui.button("Copy " + name + " Settings" + tabId + "copysettingsbutton", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                            final JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("settings", name);
                            final JsonObject valuesJsonObject = new JsonObject();
                            ConfigWithValues.saveValues(valuesJsonObject, values);
                            jsonObject.add("values", valuesJsonObject);
                            mc.keyboard.setClipboard(jsonObject.toString());
                        }
                        ImGui.sameLine();
                        if (ImGui.button("Paste " + name + " Settings" + tabId + "pastesettingsbutton", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                            final String clipboard = mc.keyboard.getClipboard();
                            if (clipboard != null && !clipboard.isBlank()) {
                                try {
                                    final JsonObject jsonObject = JsonParser.parseString(clipboard).getAsJsonObject();
                                    if (jsonObject.has("settings")) {
                                        if (jsonObject.get("settings").getAsString().equals(name)) {
                                            if (jsonObject.has("values")) {
                                                ConfigWithValues.loadValues(jsonObject.getAsJsonObject("values"), values);
                                            }
                                        }
                                    }
                                } catch (final Exception exception) {
                                    Vandalism.getInstance().getLogger().error("Failed to paste settings from clipboard.", exception);
                                }
                            }
                        }
                        if (ImUtils.subButton("Reset " + name + " Settings" + tabId + "button")) {
                            for (final Value<?> valueGroupValue : valueGroup.getValues()) {
                                valueGroupValue.resetValue();
                            }
                        }
                        ImGui.endTabItem();
                    }
                    if (containsSearchInput) {
                        ImGui.popStyleColor(3);
                    }
                } else {
                    this.clientSettings.renderValue(value);
                }
            }
            ImGui.endTabBar();
        }
    }

}
