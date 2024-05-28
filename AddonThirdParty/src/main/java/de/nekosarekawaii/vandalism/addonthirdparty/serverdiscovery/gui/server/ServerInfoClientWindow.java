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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.ServerDiscoveryUtil;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.request.impl.ServerInfoRequest;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.impl.ServerInfoResponse;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.util.common.TimeFormatter;
import de.nekosarekawaii.vandalism.util.game.PingState;
import de.nekosarekawaii.vandalism.util.game.server.ServerUtil;
import de.nekosarekawaii.vandalism.util.render.imgui.ImUtils;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
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
import net.minecraft.util.Pair;

import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInfoClientWindow extends StateClientWindow implements DataListWidget {

    private static final ImGuiInputTextCallback IP_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            final int eventCharInt = imGuiInputTextCallbackData.getEventChar();
            if (eventCharInt == 0) return;
            final char eventChar = (char) eventCharInt;
            if (!Character.isLetterOrDigit(eventChar) && eventChar != '.' && eventChar != ':') {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString ip = new ImString(253);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ServerInfoResponse serverInfo = null;
    private String lastIP = "";
    private boolean waitingForResponse = false;
    private int filteredPlayers = 0;

    private final CopyOnWriteArrayList<ListDataEntry> playerDataEntries = new CopyOnWriteArrayList<>();

    public ServerInfoClientWindow() {
        super("Server Info", Category.SERVER);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        super.onRender(context, mouseX, mouseY, delta);
        ImGui.text("IP");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(
                id + "ip",
                this.ip,
                ImGuiInputTextFlags.CallbackCharFilter,
                IP_FILTER
        );
        final ServerInfo lastServer = ServerUtil.getLastServerInfo();
        if (ServerUtil.lastServerExists() && !this.waitingForResponse) {
            if (ImUtils.subButton("Use " + (this.mc.player != null ? "Current" : "Last") + " Server" + id + "useLastOrCurrentServer")) {
                this.ip.set(lastServer.address);
            }
        }
        final String ipValue = this.ip.get();
        if (!ipValue.isBlank() && !this.waitingForResponse) {
            if (ImUtils.subButton("Get" + id + "get")) {
                this.setState("Requesting data for " + ipValue + "...");
                this.serverInfo = null;
                this.lastIP = ipValue;
                this.filteredPlayers = 0;
                this.playerDataEntries.clear();
                this.executor.submit(() -> {
                    this.waitingForResponse = true;
                    final Pair<String, Integer> resolvedAddress = ServerUtil.resolveServerAddress(ipValue);
                    final String ip = resolvedAddress.getLeft();
                    final int port = resolvedAddress.getRight();
                    final Response response = ServerDiscoveryUtil.request(new ServerInfoRequest(ip, port));
                    if (response instanceof final ServerInfoResponse serverInfoResponse) {
                        if (serverInfoResponse.isError()) {
                            this.setState("Error: " + serverInfoResponse.error);
                        } else {
                            this.serverInfo = serverInfoResponse;
                            this.updatePlayerDataEntries();
                            this.setState("Success!");
                        }
                    } else {
                        this.setState(response.error);
                    }
                    this.waitingForResponse = false;
                });
            }
        }
        if (this.serverInfo != null) {
            final StringBuilder dataString = new StringBuilder();
            final String description = ServerUtil.fixDescription(this.serverInfo.description);
            if (!description.isEmpty()) {
                dataString.append("Description: ");
                dataString.append(description);
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
            dataString.append(TimeFormatter.formatDateTime(Instant.ofEpochSecond(this.serverInfo.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime()));
            final String data = dataString.toString();
            ImGui.text("Server Info");
            ImGui.separator();
            ImGui.beginChild(id + "data", -1, ImGui.getTextLineHeightWithSpacing() * 4, true, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.spacing();
            ImGui.sameLine(5);
            ImGui.text(data);
            ImGui.endChild();
            if (ImGui.button("Connect" + id + "connect", ImGui.getColumnWidth() / 3f, ImGui.getTextLineHeightWithSpacing())) {
                ServerUtil.connect(this.lastIP);
            }
            ImGui.sameLine();
            if (ImGui.button("Copy Data" + id + "copyData", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                this.mc.keyboard.setClipboard(data);
            }
            if (this.serverInfo.players != null && !this.serverInfo.players.isEmpty()) {
                if (!this.lastIP.isBlank() && !this.waitingForResponse) {
                    final String lastIpValue = this.lastIP;
                    ImGui.sameLine();
                    if (ImGui.button("Filter Online Players" + id + "filterOnlinePlayers", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                        this.setState("Filtering online players...");
                        this.waitingForResponse = true;
                        this.executor.submit(() -> {
                            final Pair<String, Integer> resolvedAddress = ServerUtil.resolveServerAddress(lastIpValue);
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
                                            this.updatePlayerDataEntries();
                                        }
                                        this.setState("Successfully filtered " + this.filteredPlayers + " online players!");
                                        this.waitingForResponse = false;
                                    }).getSync();
                        });
                    }
                }
                ImGui.text("Players (" + this.serverInfo.players.size() + ")" + (this.filteredPlayers > 0 ? " | Filtered (" + this.filteredPlayers + ")" : ""));
                ImGui.separator();
                this.renderDataList(id + "playerList", -1, ImGui.getColumnWidth() - 20, 60f, this.playerDataEntries);
            } else {
                ImGui.text("No players found.");
            }
        }
    }

    private void updatePlayerDataEntries() {
        this.playerDataEntries.clear();
        if (this.serverInfo == null || this.serverInfo.players == null) return;
        for (final ServerInfoResponse.Player player : this.serverInfo.players) {
            final CopyOnWriteArrayList<Pair<String, String>> list = new CopyOnWriteArrayList<>();
            list.add(new Pair<>("Name", player.name));
            list.add(new Pair<>("UUID", player.uuid));
            list.add(new Pair<>("Last Seen", TimeFormatter.formatDateTime(Instant.ofEpochSecond(player.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime())));
            this.playerDataEntries.add(new ListDataEntry(list));
        }
    }

    @Override
    public boolean filterDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            return ServerUtil.isAnonymous(listDataEntry.getFirst().getRight(), listDataEntry.getSecond().getRight());
        }
        return false;
    }

    @Override
    public boolean shouldHighlightDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String playerName = listDataEntry.getFirst().getRight();
            final String playerUUID = listDataEntry.getSecond().getRight();
            return ServerUtil.isSelf(playerName, playerUUID) || Vandalism.getInstance().getFriendsManager().isFriend(playerName);
        }
        return false;
    }

    @Override
    public float[] getDataEntryHighlightColor(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String playerName = listDataEntry.getFirst().getRight();
            final String playerUUID = listDataEntry.getSecond().getRight();
            if (ServerUtil.isSelf(playerName, playerUUID)) {
                return new float[]{0.8f, 0.1f, 0.1f, 0.30f};
            } else if (Vandalism.getInstance().getFriendsManager().isFriend(playerName)) {
                return new float[]{0.9f, 0.5f, 0.1f, 0.40f};
            }
        }
        return null;
    }

    @Override
    public void onDataEntryClick(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            Vandalism.getInstance().getAccountManager().loginCracked(listDataEntry.getFirst().getRight(), listDataEntry.getSecond().getRight());
        }
    }

    @Override
    public void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String playerName = listDataEntry.getFirst().getRight();
            final String playerUUID = listDataEntry.getSecond().getRight();
            final String lastSeen = listDataEntry.getThird().getRight();
            final int buttonWidth = 150, buttonHeight = 28;
            if (ImGui.button("Add" + id + "player" + playerName + "add", buttonWidth, buttonHeight)) {
                Vandalism.getInstance().getAccountManager().loginCracked(playerName, playerUUID, true);
            }
            if (ImGui.button("Copy Name" + id + "player" + playerName + "copyName", buttonWidth, buttonHeight)) {
                this.mc.keyboard.setClipboard(playerName);
            }
            if (ImGui.button("Copy UUID" + id + "player" + playerName + "copyUuid", buttonWidth, buttonHeight)) {
                this.mc.keyboard.setClipboard(playerUUID);
            }
            if (ImGui.button("Copy Data" + id + "player" + playerName + "copyData", buttonWidth, buttonHeight)) {
                this.mc.keyboard.setClipboard("Name: " + playerName + "\nUUID: " + playerUUID + "\nLast Seen: " + lastSeen);
            }
        }
    }

}
