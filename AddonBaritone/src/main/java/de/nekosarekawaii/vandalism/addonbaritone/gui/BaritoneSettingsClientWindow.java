/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.addonbaritone.gui;

import de.nekosarekawaii.vandalism.addonbaritone.settings.BaritoneSettingMapper;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

public class BaritoneSettingsClientWindow extends ClientWindow {

    private final BaritoneSettingMapper baritoneSettingMapper;
    private final ImString searchInput = new ImString();

    public BaritoneSettingsClientWindow(final BaritoneSettingMapper baritoneSettingMapper) {
        super("Baritone Settings", Category.MISC);
        this.baritoneSettingMapper = baritoneSettingMapper;
    }

    @Override
    protected void init() {
        this.baritoneSettingMapper.updateSettings();
    }

    @Override
    protected void onEnable() {
        this.baritoneSettingMapper.updateSettings();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        final String searchIdentifier = "##BaritoneSearchInput";
        final int windowFlags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;

        ImGui.begin("Baritone Settings##VeryCoolBaritoneSettings", windowFlags);
        ImGui.separator();
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(searchIdentifier + "input", this.searchInput);
        ImGui.separator();
        ImGui.beginChild(searchIdentifier + "scrolllist", -1, -1, true);

        for (final Value<?> value : this.baritoneSettingMapper.getValues()) {
            if (this.searchInput.isEmpty() || value.getName().toLowerCase().contains(searchInput.get().toLowerCase())) {
                this.baritoneSettingMapper.renderValue(value, true);
            }
        }

        ImGui.endChild();
        ImGui.end();
    }

}
