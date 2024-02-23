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

package de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.gui.player;

import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.ServerDiscoveryUtil;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.data.request.impl.WhereIsRequest;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.data.response.Response;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.data.response.impl.WhereIsResponse;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerDiscoveryClientMenuWindow extends ClientMenuWindow {

    private static final ImGuiInputTextCallback USERNAME_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '_'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString username, state;

    private final ExecutorService executor;

    private final List<WhereIsResponse.Record> records = new ArrayList<>();

    private final ImString searchField;

    public PlayerDiscoveryClientMenuWindow() {
        super("Player Discovery", Category.SERVER);
        this.username = new ImString(16);
        this.state = new ImString(200);
        this.resetState();
        this.executor = Executors.newSingleThreadExecutor();
        this.searchField = new ImString();
    }

    private void resetState() {
        this.state.set("Waiting for input...");
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin(this.getName());
        ImGui.text("State");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##playerdiscoverystate", this.state, ImGuiInputTextFlags.ReadOnly);
        ImGui.spacing();
        ImGui.text("Username");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##playerdiscoveryname", this.username,
                ImGuiInputTextFlags.CallbackCharFilter,
                USERNAME_NAME_FILTER
        );
        final String usernameValue = this.username.get();
        if (!usernameValue.isBlank() && usernameValue.length() > 2 && usernameValue.length() < 17) {
            if (ImUtils.subButton("Search##playerdiscoverysearch")) {
                this.state.set("Searching for " + usernameValue + "...");
                this.records.clear();
                this.executor.submit(() -> {
                    final Response response = ServerDiscoveryUtil.request(new WhereIsRequest(usernameValue));
                    if (response == null) {
                        this.state.set("Every API User is rate limited!");
                    }
                    else if (response instanceof final WhereIsResponse whereIsResponse) {
                        if (whereIsResponse.isError()) {
                            this.state.set("Error: " + whereIsResponse.error);
                        } else {
                            final List<WhereIsResponse.Record> data = whereIsResponse.data;
                            if (data.isEmpty()) {
                                this.state.set(usernameValue + " not found on any server.");
                            } else {
                                this.state.set("Found " + usernameValue + " on " + data.size() + " server(s).");
                                for (final WhereIsResponse.Record record : data) {
                                    boolean contains = false;
                                    for (final WhereIsResponse.Record containedRecord : this.records) {
                                        if (containedRecord.server.equals(record.server)) {
                                            contains = true;
                                            if (record.last_seen < containedRecord.last_seen) {
                                                containedRecord.last_seen = record.last_seen;
                                            }
                                            break;
                                        }
                                    }
                                    if (!contains) {
                                        this.records.add(record);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        if (!this.records.isEmpty()) {
            if (ImUtils.subButton("Clear##playerdiscoveryclear")) {
                this.records.clear();
                this.resetState();
            }
            ImGui.spacing();
            ImGui.text("Search");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##playerdiscoverysearchfield", this.searchField);
            ImGui.separator();
            if (ImUtils.subButton("Add all servers")) {
                final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                serverList.loadFile();
                int i = 0;
                for (final WhereIsResponse.Record record : this.records) {
                    i++;
                    serverList.add(new ServerInfo(
                            "Player Discovery " + usernameValue + " (" + (i < 10 ? "0" + i : i) + ")",
                            record.server,
                            ServerInfo.ServerType.OTHER
                    ), false);
                }
                serverList.saveFile();
            }
            ImGui.beginChild("##playerdiscoverydata", -1, -1, true, ImGuiWindowFlags.HorizontalScrollbar);
            int i = 0;
            for (final WhereIsResponse.Record record : this.records) {
                final String address = record.server;
                if (address.isEmpty()) {
                    continue;
                }
                i++;
                final String playerEntryId = "##playerentry" + address + i;
                final boolean isLastServer = ServerConnectionUtil.lastServerExists() && ServerConnectionUtil.getLastServerInfo().address.equals(address);
                if (isLastServer) {
                    final float[] color = new float[]{ 0.8f, 0.1f, 0.1f, 0.30f };
                    ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                    ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                    ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
                }
                if (ImGui.button(playerEntryId, ImGui.getColumnWidth() - 8, 75)) {
                    ServerConnectionUtil.connect(address);
                }
                if (isLastServer) {
                    ImGui.popStyleColor(3);
                }
                ImGui.sameLine();
                final StringBuilder dataString = new StringBuilder();
                dataString.append("Server: ").append(record.server).append("\n");
                dataString.append("UUID: ").append(record.uuid).append("\n");
                dataString.append("Name: ").append(record.name).append("\n");
                dataString.append("Last Seen: ").append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(
                        Instant.ofEpochSecond(record.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime()
                )).append("\n");
                final String data = dataString.toString();
                if (!this.searchField.get().isBlank() && !StringUtils.contains(data, this.searchField.get())) {
                    continue;
                }
                if (ImGui.beginPopupContextItem(playerEntryId + "popup", ImGuiPopupFlags.MouseButtonRight)) {
                    final int buttonWidth = 150, buttonHeight = 28;
                    if (ImGui.button("Add to the Server List" + playerEntryId + "addtoserverlist", buttonWidth, buttonHeight)) {
                        final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                        serverList.loadFile();
                        serverList.add(new ServerInfo(
                                "Player Discovery " + usernameValue + " (" + (i < 10 ? "0" + i : i) + ")",
                                address,
                                ServerInfo.ServerType.OTHER
                        ), false);
                        serverList.saveFile();
                    }
                    if (ImGui.button("Copy Address" + playerEntryId + "copyaddress", buttonWidth, buttonHeight)) {
                        this.mc.keyboard.setClipboard(address);
                    }
                    if (ImGui.button("Copy Data" + playerEntryId + "copydata", buttonWidth, buttonHeight)) {
                        this.mc.keyboard.setClipboard(data);
                    }
                    ImGui.endPopup();
                }
                ImGui.sameLine(10);
                ImGui.text(data);
            }
            ImGui.endChild();
        }
        ImGui.end();
    }

}
