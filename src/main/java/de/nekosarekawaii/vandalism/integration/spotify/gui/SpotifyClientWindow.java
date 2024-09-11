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

package de.nekosarekawaii.vandalism.integration.spotify.gui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyManager;
import de.nekosarekawaii.vandalism.util.Percentage;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Util;

public class SpotifyClientWindow extends ClientWindow {

    private final ImBoolean showClientSecret;

    public SpotifyClientWindow() {
        super("Spotify", Category.MISC, 550f, 200f, ImGuiWindowFlags.MenuBar);
        this.showClientSecret = new ImBoolean(false);
    }

    @Override
    public void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        final SpotifyManager spotifyManager = Vandalism.getInstance().getSpotifyManager();
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("Config" + id + "configMenu")) {
                final int sharedFlag = ImGuiInputTextFlags.CallbackResize;
                final ImString clientId = new ImString(spotifyManager.getClientId());
                if (ImGui.inputText("Client ID" + id + "clientId", clientId, sharedFlag)) {
                    if (!spotifyManager.getClientId().equals(clientId.get())) {
                        spotifyManager.logout();
                        spotifyManager.setClientId(clientId.get());
                    }
                }
                final ImString clientSecret = new ImString(spotifyManager.getClientSecret());
                if (ImGui.inputText("Client Secret" + id + "clientSecret", clientSecret, sharedFlag | (!this.showClientSecret.get() ? ImGuiInputTextFlags.Password : 0))) {
                    if (!spotifyManager.getClientSecret().equals(clientSecret.get())) {
                        spotifyManager.logout();
                    }
                    spotifyManager.setClientSecret(clientSecret.get());
                }
                ImGui.sameLine();
                ImGui.checkbox("Show Client Secret" + id + "showClientSecret", this.showClientSecret);
                final ImInt httpServerPort = new ImInt(spotifyManager.getHttpServerPort());
                if (ImGui.inputInt("HTTP Server Port" + id + "httpServerPort", httpServerPort)) {
                    spotifyManager.setHttpServerPort(httpServerPort.get());
                }
                if (ImUtils.subButton("Copy redirect URI" + id + "copyRedirectUri")) {
                    this.mc.keyboard.setClipboard(spotifyManager.getRedirectUri());
                }
                if (spotifyManager.isLoggedIn()) {
                    if (ImUtils.subButton("Logout" + id + "logout")) {
                        spotifyManager.logout();
                    }
                } else if (!spotifyManager.getClientId().isEmpty() && !spotifyManager.getClientSecret().isEmpty()) {
                    if (ImUtils.subButton("Login" + id + "login")) {
                        spotifyManager.login();
                    }
                }
                ImGui.endMenu();
            }
            if (ImGui.button("Developer Dashboard" + id + "developerDashboard")) {
                Util.getOperatingSystem().open("https://developer.spotify.com/dashboard/");
            }
            if (ImGui.button("Manage Apps" + id + "manageApps")) {
                Util.getOperatingSystem().open("https://spotify.com/us/account/apps/");
            }
            ImGui.endMenuBar();
        }
        spotifyManager.update();
        final SpotifyData spotifyData = spotifyManager.getCurrentSpotifyData();
        AbstractTexture image = this.mc.getTextureManager().getTexture(FabricBootstrap.MOD_ICON);
        if (spotifyData.getImage() != null) {
            image = spotifyData.getImage();
        }
        ImGui.image(image.getGlId(), 80, 80);
        if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
            this.mc.keyboard.setClipboard(spotifyData.getImageUrl());
        }
        if (spotifyData.isPaused()) {
            ImGui.sameLine(15);
            ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.5f);
            ImGui.beginChild(id + "playbackPauseAnimation", -1, 80, false, ImGuiWindowFlags.NoScrollbar);
            ImGui.popStyleColor();
            ImGui.spacing();
            ImGui.sameLine();
            ImGui.spacing();
            ImGui.spacing();
            ImGui.spacing();
            ImGui.sameLine(24);
            final float alpha = System.currentTimeMillis() % 1000 < 500 ? 0.7f : 0f;
            ImGui.pushStyleColor(ImGuiCol.Button, 1.0f, 1.0f, 1.0f, alpha);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1.0f, 1.0f, 1.0f, alpha);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 1.0f, 1.0f, 1.0f, alpha);
            ImGui.button(id + "playbackPauseAnimation1", 10, 50);
            ImGui.sameLine();
            ImGui.button(id + "playbackPauseAnimation2", 10, 50);
            ImGui.popStyleColor(3);
            ImGui.endChild();
        }
        ImGui.sameLine(95);
        ImGui.beginChild(id + "playbackData", -1, 80, false, ImGuiWindowFlags.NoScrollbar);
        ImGui.spacing();
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        final String waitingForData = "Waiting for data...";
        ImGui.button("  " + (!spotifyData.getType().isEmpty() ? spotifyData.getName() : waitingForData), -1, 30);
        ImGui.popStyleColor();
        if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
            this.mc.keyboard.setClipboard(spotifyData.getName());
        }
        String artists = String.join(", ", spotifyData.getArtists());
        if (artists.isEmpty()) {
            artists = waitingForData;
        }
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.button("  " + artists, -1, 30);
        ImGui.popStyleColor();
        if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
            this.mc.keyboard.setClipboard(artists);
        }
        ImGui.endChild();
        final long time = spotifyData.getTime();
        final long progress = spotifyData.getProgress();
        long currentProgress;
        final long currentTime = System.currentTimeMillis();
        if (spotifyData.isPaused()) {
            currentProgress = spotifyData.getLastTime() - (time - progress);
        } else {
            currentProgress = currentTime - (time - progress);
            spotifyData.setLastTime(currentTime);
        }
        if (currentProgress > spotifyData.getDuration()) {
            currentProgress = spotifyData.getDuration();
        }
        /*
        if (ImGui.button(FontAwesomeIcons.StepBackward)) {
            spotifyManager.previous();
        }
        ImGui.sameLine(ImGui.getWindowWidth() / 2);
        if (spotifyData.isPaused()) {
            if (ImGui.button(FontAwesomeIcons.Play)) {
                spotifyManager.play();
            }
        } else {
            if (ImGui.button(FontAwesomeIcons.Pause)) {
                spotifyManager.pause();
            }
        }
        ImGui.sameLine(ImGui.getWindowWidth() - 45);
        if (ImGui.button(FontAwesomeIcons.StepForward)) {
            spotifyManager.next();
        }
        */
        ImGui.progressBar(Percentage.percentage(currentProgress, spotifyData.getDuration()) / 100, -1, 2);
        final long durationSeconds = spotifyData.getDuration() / 1000 % 60;
        ImGui.text(String.format("%d:%02d", currentProgress / 1000 / 60, currentProgress / 1000 % 60));
        ImGui.sameLine(ImGui.getWindowWidth() - 50);
        ImGui.text(String.format("%d:%02d", spotifyData.getDuration() / 1000 / 60, durationSeconds));
    }

}
