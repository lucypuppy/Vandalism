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

package de.nekosarekawaii.vandalism.integration.serverdiscovery.gui.server;

import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import imgui.ImGui;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerDiscoveryClientWindow extends ClientWindow {

    private static final String DEFAULT_SERVER_TAB_NAME = "Server Tab";

    private final ConcurrentHashMap<String, ServerDiscoveryTab> serversTabs = new ConcurrentHashMap<>();
    private final ImString serverTabName = new ImString();
    private String currentServerTab = "";

    public ServerDiscoveryClientWindow() {
        super("Server Discovery", Category.SERVER, 450f, 400f, ImGuiWindowFlags.MenuBar);
        this.serversTabs.put(DEFAULT_SERVER_TAB_NAME, new ServerDiscoveryTab());
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        if (ImGui.beginMenuBar()) {
            for (final Map.Entry<String, ServerDiscoveryTab> entry : this.serversTabs.entrySet()) {
                entry.getValue().renderMenu(entry.getKey());
            }
            if (!this.currentServerTab.isEmpty()) {
                if (ImGui.button("Close Tab" + id + "closeServerTab")) {
                    this.serversTabs.remove(this.currentServerTab);
                    this.currentServerTab = "";
                }
            }
            if (ImGui.button("Create" + id + "createServerTab")) {
                String name = this.serverTabName.get();
                if (name.isEmpty()) {
                    name = DEFAULT_SERVER_TAB_NAME;
                }
                if (this.serversTabs.containsKey(name)) {
                    int i = 2;
                    while (this.serversTabs.containsKey(name + " " + i)) {
                        i++;
                    }
                    name = name + " " + i;
                }
                this.serversTabs.put(name, new ServerDiscoveryTab());
                this.serverTabName.set("");
            }
            ImGui.text("Enter Name:");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText(id + "serverTabName", this.serverTabName);
            ImGui.endMenuBar();
        }
        if (!this.serversTabs.isEmpty()) {
            if (ImGui.beginTabBar(id + "serverTabBar", ImGuiTabBarFlags.AutoSelectNewTabs)) {
                for (final Map.Entry<String, ServerDiscoveryTab> entry : this.serversTabs.entrySet()) {
                    final String name = entry.getKey();
                    if (entry.getValue().render(id + name, name)) {
                        this.currentServerTab = name;
                    }
                }
                ImGui.endTabBar();
            }
        } else ImGui.text("Press create to create a new server tab.");
    }


    public boolean isCurrentServerTab(final String name) {
        return this.currentServerTab.equals(name);
    }

}
