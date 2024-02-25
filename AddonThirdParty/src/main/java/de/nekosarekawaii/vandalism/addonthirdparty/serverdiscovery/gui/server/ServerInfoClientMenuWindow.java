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

package de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.server;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.ServerDiscoveryUtil;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.request.impl.ServerInfoRequest;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.impl.ServerInfoResponse;
import de.nekosarekawaii.vandalism.base.account.type.SessionAccount;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.util.common.UUIDUtil;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInfoClientMenuWindow extends ClientMenuWindow {

    private static final ImGuiInputTextCallback IP_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '.' &&
                            imGuiInputTextCallbackData.getEventChar() != ':'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString ip, state;

    private final ExecutorService executor;

    private ServerInfoResponse serverInfo;
    private String lastIP;

    public ServerInfoClientMenuWindow() {
        super("Server Info", Category.SERVER);
        this.ip = new ImString(253);
        this.state = new ImString(200);
        this.resetState();
        this.executor = Executors.newSingleThreadExecutor();
        this.serverInfo = null;
    }

    private void resetState() {
        this.state.set("Waiting for input...");
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin(this.getName());
        ImGui.text("State");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##serverinfostate", this.state, ImGuiInputTextFlags.ReadOnly);
        ImGui.spacing();
        ImGui.text("IP");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##serverinfoip", this.ip,
                ImGuiInputTextFlags.CallbackCharFilter,
                IP_FILTER
        );
        final String ipValue = this.ip.get();
        if (!ipValue.isBlank()) {
            if (ImUtils.subButton("Get##serverinfoddatarequest")) {
                this.state.set("Requesting data for " + ipValue + "...");
                this.serverInfo = null;
                this.lastIP = ipValue;
                this.executor.submit(() -> {
                    final Pair<String, Integer> resolvedAddress = ServerConnectionUtil.resolveServerAddress(ipValue);
                    final String ip = resolvedAddress.getLeft();
                    final int port = resolvedAddress.getRight();
                    final Response response = ServerDiscoveryUtil.request(new ServerInfoRequest(ip, port));
                    if (response == null) {
                        this.state.set("Every API User is rate limited!");
                    } else if (response instanceof final ServerInfoResponse serverInfoResponse) {
                        if (serverInfoResponse.isError()) {
                            this.state.set("Error: " + serverInfoResponse.error);
                        } else {
                            this.serverInfo = serverInfoResponse;
                            this.state.set("Success!");
                        }
                    }
                });
            }
        }
        if (this.serverInfo != null) {
            final StringBuilder dataString = new StringBuilder();
            String description = Formatting.strip(this.serverInfo.description);
            if (description != null && !description.isEmpty()) {
                final String colorCodePrefix = String.valueOf(Formatting.FORMATTING_CODE_PREFIX);
                if (description.contains(colorCodePrefix)) {
                    description = description.replace(colorCodePrefix, "");
                }
                if (description.contains("    ")) {
                    description = description.replace("    ", " ");
                }
                if (description.contains("\n")) {
                    description = description.replace("\n", " ");
                }
                if (description.contains("\t")) {
                    description = description.replace("\t", " ");
                }
                if (!description.isEmpty()) {
                    dataString.append("Description: ");
                    final int maxLength = 200;
                    if (description.length() > maxLength) {
                        description = description.substring(0, maxLength);
                        description += "...";
                    }
                    dataString.append(description);
                }
            }
            dataString.append("\n");
            dataString.append("Version: ");
            dataString.append(this.serverInfo.version);
            dataString.append("\n");
            dataString.append("Protocol: ");
            dataString.append(this.serverInfo.protocol);
            dataString.append("\n");
            dataString.append("Players: ");
            dataString.append(this.serverInfo.online_players);
            dataString.append("/");
            dataString.append(this.serverInfo.max_players);
            dataString.append("\n");
            dataString.append("Cracked: ");
            dataString.append(this.serverInfo.cracked);
            dataString.append("\n");
            dataString.append("Last Seen: ");
            dataString.append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(
                    Instant.ofEpochSecond(this.serverInfo.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime()
            ));
            final String data = dataString.toString();
            ImGui.spacing();
            ImGui.text("Server Info");
            ImGui.separator();
            ImGui.beginChild("##serverinfodata", -1, ImGui.getTextLineHeightWithSpacing() * 4, true, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.spacing();
            ImGui.sameLine(5);
            ImGui.text(data);
            ImGui.endChild();
            if (ImGui.button("Connect##serverInfoConnect", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                ServerConnectionUtil.connect(this.lastIP);
            }
            ImGui.sameLine();
            if (ImGui.button("Copy Data##serverinfocopydata", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                this.mc.keyboard.setClipboard(data);
            }
            ImGui.spacing();
            ImGui.text("Players");
            ImGui.separator();
            for (final ServerInfoResponse.Player player : this.serverInfo.players) {
                final String playerName = player.name;
                final StringBuilder playerString = new StringBuilder();
                playerString.append("Name: ");
                playerString.append(playerName);
                playerString.append("\n");
                playerString.append("UUID: ");
                playerString.append(player.uuid);
                playerString.append("\n");
                playerString.append("Last Seen: ");
                playerString.append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(
                        Instant.ofEpochSecond(player.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime()
                ));
                final String playerData = playerString.toString();
                final boolean isCurrentAccount = this.mc.getGameProfile().getName().equals(playerName);
                if (isCurrentAccount) {
                    final float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
                    ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
                }
                if (ImGui.button("##serverinfoplayer" + playerName, ImGui.getColumnWidth() - 8, 60)) {
                    String uuid;
                    try {
                        uuid = UUIDUtil.getUUIDFromName(playerName);
                    } catch (Exception e) {
                        uuid = player.uuid;
                        Vandalism.getInstance().getLogger().error("Failed to get UUID of the player: \"" + playerName + "\" (using fallback UUID).");
                    }
                    final SessionAccount sessionAccount = new SessionAccount(
                            playerName,
                            uuid,
                            "",
                            "",
                            ""
                    );
                    sessionAccount.logIn();
                }
                if (isCurrentAccount) {
                    ImGui.popStyleColor(3);
                }
                if (ImGui.beginPopupContextItem("##serverinfoplayer" + playerName + "popup", ImGuiPopupFlags.MouseButtonRight)) {
                    final int buttonWidth = 150, buttonHeight = 28;
                    if (ImGui.button("Add##serverinfoplayer" + playerName + "add", buttonWidth, buttonHeight)) {
                        String uuid;
                        try {
                            uuid = UUIDUtil.getUUIDFromName(playerName);
                        } catch (Exception e) {
                            uuid = player.uuid;
                            Vandalism.getInstance().getLogger().error("Failed to get UUID of the player: \"" + playerName + "\" (using fallback UUID).");
                        }
                        final SessionAccount sessionAccount = new SessionAccount(
                                playerName,
                                uuid,
                                "",
                                "",
                                ""
                        );
                        Vandalism.getInstance().getAccountManager().add(sessionAccount);
                        sessionAccount.logIn();
                    }
                    if (ImGui.button("Copy Data##serverinfoplayer" + playerName + "copydata", buttonWidth, buttonHeight)) {
                        this.mc.keyboard.setClipboard(playerData);
                    }
                    ImGui.endPopup();
                }
                ImGui.sameLine(20);
                ImGui.text(playerData);
            }
        }
        ImGui.end();
    }

}
