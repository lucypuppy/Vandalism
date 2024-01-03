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

package de.nekosarekawaii.vandalism.base.clientsettings.gui;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;

public class ClientSettingsClientMenuWindow extends ClientMenuWindow {

    private final ClientSettings clientSettings;

    public ClientSettingsClientMenuWindow(final ClientSettings clientSettings) {
        super("Client Settings", Category.CONFIGURATION);

        this.clientSettings = clientSettings;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin(getName());
        if (ImGui.beginTabBar("##config")) {
            for (final Value<?> value : clientSettings.getValues()) {
                if (value instanceof final ValueGroup valueGroup) {
                    if (ImGui.beginTabItem(valueGroup.getName())) {
                        if (ImGui.button("Reset " + valueGroup.getName())) {
                            for (final Value<?> valueGroupValue : valueGroup.getValues()) {
                                valueGroupValue.resetValue();
                            }
                        }
                        ImGui.separator();
                        if (ImGui.beginChild("##" + valueGroup.getName())) {
                            valueGroup.renderValues();
                            ImGui.endChild();
                        }
                        ImGui.endTabItem();
                    }
                } else {
                    clientSettings.renderValue(value);
                }
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }

}
