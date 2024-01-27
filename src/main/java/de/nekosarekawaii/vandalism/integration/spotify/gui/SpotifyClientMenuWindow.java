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

package de.nekosarekawaii.vandalism.integration.spotify.gui;

import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyManager;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

public class SpotifyClientMenuWindow extends ClientMenuWindow {

    private final SpotifyManager spotifyManager;

    private final ImBoolean showClientSecret;

    public SpotifyClientMenuWindow(final SpotifyManager spotifyManager) {
        super("Spotify", Category.MISC);
        this.spotifyManager = spotifyManager;
        this.showClientSecret = new ImBoolean(false);
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin(this.getName(), ImGuiWindowFlags.MenuBar);
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("Config")) {
                final int sharedFlag = ImGuiInputTextFlags.CallbackResize;
                final ImString clientId = new ImString(this.spotifyManager.getClientId());
                if (ImGui.inputText("Client ID", clientId, sharedFlag)) {
                    if (!this.spotifyManager.getClientId().equals(clientId.get())) {
                        this.spotifyManager.logout();
                        this.spotifyManager.setClientId(clientId.get());
                    }
                }
                final ImString clientSecret = new ImString(this.spotifyManager.getClientSecret());
                if (ImGui.inputText("Client Secret", clientSecret, sharedFlag | (!this.showClientSecret.get() ? ImGuiInputTextFlags.Password : 0))) {
                    if (!this.spotifyManager.getClientSecret().equals(clientSecret.get())) {
                        this.spotifyManager.logout();
                    }
                    this.spotifyManager.setClientSecret(clientSecret.get());
                }
                ImGui.sameLine();
                ImGui.checkbox("Show Client Secret", this.showClientSecret);
                ImGui.endMenu();
            }
            if (this.spotifyManager.isLoggedIn()) {
                if (ImGui.button("Logout")) {
                    this.spotifyManager.logout();
                }
            }
            else if (!this.spotifyManager.getClientId().isEmpty() && !this.spotifyManager.getClientSecret().isEmpty()) {
                if (ImGui.button("Login")) {
                    this.spotifyManager.login();
                }
            }
            ImGui.endMenuBar();
        }
        if (this.spotifyManager.isLoggedIn()) {

        }
        ImGui.end();
    }

}
