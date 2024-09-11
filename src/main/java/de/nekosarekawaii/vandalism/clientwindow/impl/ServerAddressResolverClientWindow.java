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

package de.nekosarekawaii.vandalism.clientwindow.impl;

import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.field.IPFieldWidget;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.lenni0451.mcping.ServerAddress;
import net.minecraft.client.gui.DrawContext;

public class ServerAddressResolverClientWindow extends ClientWindow implements IPFieldWidget {

    private final ImString ip = this.createImIP();
    private final ImString lastData = new ImString();

    public ServerAddressResolverClientWindow() {
        super("Server Address Resolver", Category.SERVER, 700f, 600f);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        this.renderField(id);
        if (this.isValidIP()) {
            final String ip = this.getImIP().get();
            if (StringUtils.containsLetter(ip)) {
                if (ImUtils.subButton("Resolve Server Address" + id + "resolveAddress")) {
                    this.lastData.clear();
                    new Thread(() -> {
                        try {
                            final ServerAddress serverAddress = ServerAddress.parse(ip, 25565);
                            String oldAddress = serverAddress.getSocketAddress().toString();
                            if (oldAddress.contains("./")) oldAddress = oldAddress.replace("./", "/");
                            if (oldAddress.contains("/")) oldAddress = oldAddress.replace("/", "\n");
                            serverAddress.resolve();
                            String newAddress = serverAddress.getSocketAddress().toString();
                            if (newAddress.contains("./")) newAddress = newAddress.replace("./", "/");
                            if (newAddress.contains("/")) newAddress = newAddress.replace("/", "\n");
                            this.lastData.set(oldAddress + "\n\n" + newAddress);
                        } catch (final Exception e) {
                            this.lastData.set("Error: " + e.getMessage());
                        }
                    }).start();
                }
            }
        }
        if (!this.lastData.get().isBlank()) {
            if (ImUtils.subButton("Clear" + id + "clear")) {
                this.lastData.clear();
            }
            ImGui.separator();
            ImGui.text("Resolved Data");
            ImGui.setNextItemWidth(-1);
            ImGui.inputTextMultiline(id + "resolvedData", this.lastData, -1, -1, ImGuiInputTextFlags.ReadOnly);
        }
    }

    @Override
    public ImString getImIP() {
        return this.ip;
    }

    @Override
    public boolean shouldResolve() {
        return false;
    }

}
