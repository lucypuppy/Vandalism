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

package de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.server;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.ServerDiscoveryUtil;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.request.impl.ServerInfoRequest;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.impl.ServerInfoResponse;
import de.nekosarekawaii.vandalism.base.account.type.SessionAccount;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.util.game.PingState;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import de.nekosarekawaii.vandalism.util.render.imgui.ImUtils;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInfoClientWindow extends StateClientWindow {

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

    private final ImString ip;

    private final ExecutorService executor;

    private ServerInfoResponse serverInfo;
    private String lastIP;

    private boolean waitingForResponse;

    private int filteredPlayers;

    public ServerInfoClientWindow() {
        super("Server Info", Category.SERVER);
        this.ip = new ImString(253);
        this.executor = Executors.newSingleThreadExecutor();
        this.serverInfo = null;
        this.lastIP = "";
        this.waitingForResponse = false;
        this.filteredPlayers = 0;
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.onRender(context, mouseX, mouseY, delta);
        ImGui.text("IP");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(
                "##serverinfoip",
                this.ip,
                ImGuiInputTextFlags.CallbackCharFilter,
                IP_FILTER
        );
        final ServerInfo currentServer = ServerConnectionUtil.getLastServerInfo();
        if (currentServer != null && !this.waitingForResponse) {
            if (ImGui.button("Use " + (this.mc.player != null ? "Current" : "Last") + " Server##serverinfousecurrentserver", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                this.ip.set(currentServer.address);
            }
        }
        final String ipValue = this.ip.get();
        if (!ipValue.isBlank() && !this.waitingForResponse) {
            if (ImUtils.subButton("Get##serverinfoddatarequest")) {
                this.setState("Requesting data for " + ipValue + "...");
                this.serverInfo = null;
                this.lastIP = ipValue;
                this.filteredPlayers = 0;
                this.executor.submit(() -> {
                    this.waitingForResponse = true;
                    final Pair<String, Integer> resolvedAddress = ServerConnectionUtil.resolveServerAddress(ipValue);
                    final String ip = resolvedAddress.getLeft();
                    final int port = resolvedAddress.getRight();
                    final Response response = ServerDiscoveryUtil.request(new ServerInfoRequest(ip, port));
                    if (response instanceof final ServerInfoResponse serverInfoResponse) {
                        if (serverInfoResponse.isError()) {
                            this.setState("Error: " + serverInfoResponse.error);
                        } else {
                            this.serverInfo = serverInfoResponse;
                            this.setState("Success!");
                        }
                    } else {
                        this.setState("API User is rate limited!");
                    }
                    this.waitingForResponse = false;
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
            if (this.serverInfo.players != null && !this.serverInfo.players.isEmpty()) {
                if (!this.lastIP.isBlank() && !this.waitingForResponse) {
                    final String lastIpValue = this.lastIP;
                    if (ImUtils.subButton("Filter Online Players##serverinfofilteronlineplayers")) {
                        this.setState("Filtering online players...");
                        this.waitingForResponse = true;
                        this.executor.submit(() -> {
                            final Pair<String, Integer> resolvedAddress = ServerConnectionUtil.resolveServerAddress(lastIpValue);
                            final String ip = resolvedAddress.getLeft();
                            final int port = resolvedAddress.getRight();
                            MCPing.pingModern(this.serverInfo.protocol)
                                    .address(ip, port)
                                    .timeout(5000, 5000)
                                    .exceptionHandler(t -> {
                                        switch (t) {
                                            case UnknownHostException unknownHostException ->
                                                    this.setState(PingState.UNKNOWN_HOST.getMessage());
                                            case ConnectionRefusedException connectionRefusedException ->
                                                    this.setState(PingState.CONNECTION_REFUSED.getMessage());
                                            case ConnectTimeoutException connectTimeoutException ->
                                                    this.setState(PingState.CONNECTION_TIMED_OUT.getMessage());
                                            case DataReadException dataReadException ->
                                                    this.setState(PingState.DATA_READ_FAILED.getMessage());
                                            case PacketReadException packetReadException ->
                                                    this.setState(PingState.PACKET_READ_FAILED.getMessage());
                                            case null, default -> {
                                                this.setState(PingState.FAILED.getMessage());
                                                Vandalism.getInstance().getLogger().error("Failed to ping {}:{}", ip, port, t);
                                            }
                                        }
                                        this.waitingForResponse = false;
                                    }).finishHandler(response -> {
                                        if (this.serverInfo.players != null && !this.serverInfo.players.isEmpty() && response.players != null && response.players.sample != null) {
                                            final MCPingResponse.Players.Player[] sample = response.players.sample;
                                            this.serverInfo.players.removeIf(serverInfoPlayer -> {
                                                for (final MCPingResponse.Players.Player onlinePlayer : sample) {
                                                    if (onlinePlayer.name.equals(serverInfoPlayer.name)) {
                                                        this.filteredPlayers++;
                                                        return true;
                                                    }
                                                }
                                                return false;
                                            });
                                        }
                                        this.setState("Successfully filtered " + this.filteredPlayers + " online players!");
                                        this.waitingForResponse = false;
                                    }).getSync();
                        });
                    }
                }
                ImGui.spacing();
                ImGui.text("Players (" + this.serverInfo.players.size() + ")" + (this.filteredPlayers > 0 ? " | Filtered (" + this.filteredPlayers + ")" : ""));
                ImGui.separator();
                for (final ServerInfoResponse.Player player : this.serverInfo.players) {
                    final String playerName = player.name;
                    final String playerUUID = player.uuid;
                    if (playerName.equals("Anonymous Player") || playerUUID.equals("00000000-0000-0000-0000-000000000000")) {
                        continue;
                    }
                    final String playerData = "Name: " +
                            playerName +
                            "\n" +
                            "UUID: " +
                            playerUUID +
                            "\n" +
                            "Last Seen: " +
                            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(
                                    Instant.ofEpochSecond(player.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime()
                            );
                    final GameProfile gameProfile = this.mc.getGameProfile();
                    final boolean isCurrentAccount = gameProfile.getName().equals(playerName) && gameProfile.getId().toString().equals(playerUUID);
                    if (isCurrentAccount) {
                        final float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
                        ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                        ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
                    }
                    if (ImGui.button("##serverinfoplayer" + playerName, ImGui.getColumnWidth() - 8, 60)) {
                        final SessionAccount sessionAccount = new SessionAccount(
                                playerName,
                                playerUUID,
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
                            final SessionAccount sessionAccount = new SessionAccount(
                                    playerName,
                                    playerUUID,
                                    "",
                                    "",
                                    ""
                            );
                            Vandalism.getInstance().getAccountManager().add(sessionAccount);
                            sessionAccount.logIn();
                        }
                        if (ImGui.button("Copy Name##serverinfoplayer" + playerName + "copyname", buttonWidth, buttonHeight)) {
                            this.mc.keyboard.setClipboard(playerName);
                        }
                        if (ImGui.button("Copy UUID##serverinfoplayer" + playerName + "copyuuid", buttonWidth, buttonHeight)) {
                            this.mc.keyboard.setClipboard(playerUUID);
                        }
                        if (ImGui.button("Copy Data##serverinfoplayer" + playerName + "copydata", buttonWidth, buttonHeight)) {
                            this.mc.keyboard.setClipboard(playerData);
                        }
                        ImGui.endPopup();
                    }
                    ImGui.sameLine(20);
                    ImGui.text(playerData);
                }
            } else {
                ImGui.text("No players found.");
            }
        }
    }

}
