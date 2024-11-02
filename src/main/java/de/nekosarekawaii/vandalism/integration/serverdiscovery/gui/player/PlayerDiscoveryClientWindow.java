/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.serverdiscovery.gui.player;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.impl.WhereIsRequest;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.impl.WhereIsResponse;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.TimeFormatter;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.math.MathUtil;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.util.Pair;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerDiscoveryClientWindow extends StateClientWindow implements DataListWidget {

    private static final ImGuiInputTextCallback USERNAME_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            final int eventChar = imGuiInputTextCallbackData.getEventChar();
            if (eventChar == 0) return;
            if (!Character.isLetterOrDigit(eventChar) && eventChar != '_' && eventChar != '§' && eventChar != '.') {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString username = new ImString(MinecraftConstants.MAX_USERNAME_LENGTH);
    private String lastUsername = "";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final CopyOnWriteArrayList<ListDataEntry> playerRecordDataEntries = new CopyOnWriteArrayList<>();
    private final ImString searchField = new ImString();
    private boolean waitingForResponse = false;

    public PlayerDiscoveryClientWindow() {
        super("Player Discovery", Category.SERVER, 550f, 600f);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        super.onRender(context, mouseX, mouseY, delta);
        ImGui.text("Username");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(id + "username", this.username, ImGuiInputTextFlags.CallbackCharFilter, USERNAME_NAME_FILTER);
        final String usernameValue = this.username.get();
        if (!usernameValue.isBlank() && MathUtil.isBetween(usernameValue.length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH) && !this.waitingForResponse) {
            if (ImGui.button("Search" + id + "search", ImGui.getColumnWidth() / (!this.playerRecordDataEntries.isEmpty() ? 2f : 1f), ImGui.getTextLineHeightWithSpacing())) {
                this.setState("Searching for " + usernameValue + "...");
                this.playerRecordDataEntries.clear();
                this.lastUsername = usernameValue;
                this.executor.submit(() -> {
                    this.waitingForResponse = true;
                    final Response response = Vandalism.getInstance().getServerDiscoveryManager().request(new WhereIsRequest(this.lastUsername));
                    if (response instanceof final WhereIsResponse whereIsResponse) {
                        if (whereIsResponse.isError()) {
                            this.setState("Error: " + whereIsResponse.error);
                        } else {
                            final List<WhereIsResponse.Record> data = whereIsResponse.data;
                            if (data.isEmpty()) {
                                this.setState(this.lastUsername + " not found on any server.");
                            } else {
                                this.setState("Found " + this.lastUsername + " on " + data.size() + " servers.");
                                for (final WhereIsResponse.Record record : data) {
                                    for (final WhereIsResponse.Record containedRecord : data) {
                                        if (containedRecord.server.equals(record.server)) {
                                            if (containedRecord.last_seen < record.last_seen) {
                                                record.last_seen = containedRecord.last_seen;
                                            }
                                            break;
                                        }
                                    }
                                    final CopyOnWriteArrayList<Pair<String, String>> list = new CopyOnWriteArrayList<>();
                                    list.add(new Pair<>("Server", record.server));
                                    list.add(new Pair<>("Last Seen", TimeFormatter.formatDateTime(Instant.ofEpochSecond(record.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime())));
                                    this.playerRecordDataEntries.add(new ListDataEntry(list));
                                }
                            }
                        }
                    } else {
                        this.setState(response.error);
                    }
                    this.waitingForResponse = false;
                });
            }
        }
        if (!this.playerRecordDataEntries.isEmpty()) {
            ImGui.sameLine();
            if (ImUtils.subButton("Clear" + id + "clear")) {
                this.playerRecordDataEntries.clear();
                this.resetState();
            }
            ImGui.spacing();
            ImGui.text("Search");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText(id + "searchField", this.searchField);
            ImGui.separator();
            if (ImUtils.subButton("Add All Servers" + id + "addAllServers")) {
                final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                serverList.loadFile();
                int i = 0;
                for (final ListDataEntry listDataEntry : this.playerRecordDataEntries) {
                    i++;
                    serverList.add(new ServerInfo(
                            "Player Discovery " + this.lastUsername + " (" + (i < 10 ? "0" + i : i) + ")",
                            listDataEntry.getFirst().getRight(),
                            ServerInfo.ServerType.OTHER
                    ), false);
                }
                serverList.saveFile();
            }
            this.renderDataList(id + "playerRecords", -1f, 45f, this.playerRecordDataEntries);
        }
    }

    @Override
    public boolean filterDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            return listDataEntry.getFirst().getRight().isEmpty() || (!this.searchField.get().isBlank() && !StringUtils.contains(listDataEntry.getData(), this.searchField.get()));
        }
        return false;
    }

    @Override
    public boolean shouldHighlightDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            return ServerUtil.lastServerExists() && ServerUtil.getLastServerInfo().address.equals(listDataEntry.getFirst().getRight());
        }
        return false;
    }

    @Override
    public float[] getDataEntryHighlightColor(final DataEntry dataEntry) {
        return new float[]{0.8f, 0.1f, 0.1f, 0.30f};
    }

    @Override
    public void onDataEntryClick(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            ServerUtil.connect(listDataEntry.getFirst().getRight());
        }
    }

    @Override
    public void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String address = listDataEntry.getFirst().getRight();
            final float buttonWidth = ImUtils.modulateDimension(200), buttonHeight = ImUtils.modulateDimension(28);
            if (ImGui.button("Add to the Server List" + id + "addToServerList", buttonWidth, buttonHeight)) {
                final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                serverList.loadFile();
                serverList.add(new ServerInfo(
                        "Player Discovery " + this.lastUsername + " (" + (index < 10 ? "0" + index : index) + ")",
                        address,
                        ServerInfo.ServerType.OTHER
                ), false);
                serverList.saveFile();
            }
            if (ImGui.button("Copy Address" + id + "copyAddress", buttonWidth, buttonHeight)) {
                mc.keyboard.setClipboard(address);
            }
            if (ImGui.button("Copy Data" + id + "copyData", buttonWidth, buttonHeight)) {
                mc.keyboard.setClipboard(listDataEntry.getData());
            }
        }
    }

}
