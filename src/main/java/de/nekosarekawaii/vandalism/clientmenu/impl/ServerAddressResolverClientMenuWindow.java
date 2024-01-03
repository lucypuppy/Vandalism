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

package de.nekosarekawaii.vandalism.clientmenu.impl;

import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.lenni0451.mcping.ServerAddress;
import net.minecraft.client.gui.DrawContext;

import java.util.concurrent.Executors;

public class ServerAddressResolverClientMenuWindow extends ClientMenuWindow {

    private static final ImGuiInputTextCallback HOSTNAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '.' &&
                            imGuiInputTextCallbackData.getEventChar() != '-'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString hostname, lastData;

    public ServerAddressResolverClientMenuWindow() {
        super("Server Address Resolver", Category.SERVER_UTILS);
        this.hostname = new ImString(253);
        this.lastData = new ImString();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (ImGui.begin(
                "Server Address Resolver##serveraddressresolver",
                ImGuiWindowFlags.NoCollapse
        )) {
            ImGui.inputText(
                    "Hostname##serveraddressresolverhostname",
                    this.hostname,
                    ImGuiInputTextFlags.CallbackCharFilter,
                    HOSTNAME_FILTER
            );
            if (
                    !this.hostname.get().isBlank() &&
                            this.hostname.get().length() >= 4 &&
                            this.hostname.get().contains(".") &&
                            this.hostname.get().indexOf(".") < this.hostname.get().length() - 2
            ) {
                if (ImGui.button("Resolve Server Address##serveraddressresolverresolve")) {
                    this.lastData.clear();
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            final ServerAddress serverAddress = ServerAddress.parse(this.hostname.get(), 25565);
                            String oldAddress = serverAddress.getSocketAddress().toString();
                            if (oldAddress.contains("./")) oldAddress = oldAddress.replace("./", "/");
                            if (oldAddress.contains("/")) oldAddress = oldAddress.replace("/", "\n");
                            serverAddress.resolve();
                            String newAddress = serverAddress.getSocketAddress().toString();
                            if (newAddress.contains("./")) newAddress = newAddress.replace("./", "/");
                            if (newAddress.contains("/")) newAddress = newAddress.replace("/", "\n");
                            this.lastData.set(oldAddress + "\n\n" + newAddress);
                        } catch (Exception e) {
                            this.lastData.set("Error: " + e.getMessage());
                        }
                    });
                }
                ImGui.sameLine();
            }
            if (!this.lastData.get().isBlank()) {
                if (ImGui.button("Clear##serveraddressresolverclear")) {
                    this.lastData.clear();
                }
                ImGui.separator();
                ImGui.text("Data");
                ImGui.setNextItemWidth(-1);
                ImGui.inputTextMultiline("##serveraddressresolverdata", this.lastData, -1, 100, ImGuiInputTextFlags.ReadOnly);
            }
            ImGui.end();
        }
    }

}
