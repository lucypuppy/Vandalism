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

package de.nekosarekawaii.vandalism.addonbaritone.gui;

import de.nekosarekawaii.vandalism.addonbaritone.settings.BaritoneSettingMapper;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

public class BaritoneSettingsClientWindow extends ClientWindow {

    private final BaritoneSettingMapper baritoneSettingMapper;
    private final ImString searchInput = new ImString();

    public BaritoneSettingsClientWindow(final BaritoneSettingMapper baritoneSettingMapper) {
        super("Baritone Settings", Category.MISC, 600f, 500f, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
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
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        ImGui.separator();
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(id + "input", this.searchInput);
        ImGui.separator();
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.beginChild(id + "scrolllist", -1, -1, true);
        for (final Value<?> value : this.baritoneSettingMapper.getValues()) {
            if (this.searchInput.isEmpty() || value.getName().toLowerCase().contains(this.searchInput.get().toLowerCase())) {
                this.baritoneSettingMapper.renderValue(value, true);
            }
        }
        ImGui.endChild();
        ImGui.popStyleColor();
    }

}
