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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.common.TimeFormatter;
import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.integration.imgui.ImUtils;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.Country;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.impl.ServersRequest;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.impl.ServersResponse;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.server.ServerUtil;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.mcping.MCPing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.util.Pair;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerDiscoveryTab implements MinecraftWrapper, DataListWidget {

    private final ImInt asn = new ImInt(0);
    private Country country = Country.ANY;
    private final ImString countrySearch = new ImString();
    private final ImBoolean cracked = new ImBoolean(false);
    private final ImString description = new ImString();
    private ServersRequest.Software software = ServersRequest.Software.ANY;
    private final ImString customSoftware = new ImString();
    private final ImInt minPlayers = new ImInt(0);
    private final ImInt maxPlayers = new ImInt(-1);
    private final ImInt minOnlinePlayers = new ImInt(0);
    private final ImInt maxOnlinePlayers = new ImInt(-1);

    private int protocol = ServersRequest.ANY_PROTOCOL;
    private final ImBoolean ignoreModded = new ImBoolean(false);
    private final ImBoolean onlyBungeeSpoofable = new ImBoolean(false);

    private final ImInt onlineAfter = new ImInt(-1);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean waitingForResponse = false;
    private final ImString serversSearchField = new ImString();

    private String lastAddress = "";

    private final AtomicInteger checkedServers = new AtomicInteger(-1);
    private int lastMaxServers = -1;

    private final MSTimer checkTimeout = new MSTimer();

    private final CopyOnWriteArrayList<ListDataEntry> serverDataEntries = new CopyOnWriteArrayList<>();

    public void renderMenu(final String name) {
        final ServerDiscoveryClientWindow serverDiscoveryClientWindow = Vandalism.getInstance().getClientWindowManager().getByClass(ServerDiscoveryClientWindow.class);
        if (!serverDiscoveryClientWindow.isCurrentServerTab(name)) {
            return;
        }
        if (ImGui.beginMenu("Tab Config")) {
            final String id = "##" + name;
            ImGui.separator();
            ImGui.inputInt("ASN", this.asn, 1);
            if (ImGui.beginCombo("Country", this.country.getCountryName(), ImGuiComboFlags.HeightLargest)) {
                ImGui.separator();
                ImGui.text("Search for Country");
                ImGui.setNextItemWidth(Math.max(350, ImGui.getColumnWidth()));
                ImGui.inputText(id + "countrySearch", this.countrySearch);
                ImGui.separator();
                ImGui.spacing();
                for (final Country country : Country.values()) {
                    final String countryName = country.getCountryName();
                    if (this.countrySearch.isNotEmpty() && !StringUtils.contains(countryName, this.countrySearch.get())) {
                        continue;
                    }
                    if (ImGui.selectable(countryName, country.equals(this.country))) {
                        this.country = country;
                    }
                }
                ImGui.endCombo();
            }
            ImGui.checkbox("Cracked", this.cracked);
            ImGui.inputText("Description", this.description);
            if (ImGui.beginCombo("Software", this.software.getName(), ImGuiComboFlags.HeightLargest)) {
                for (final ServersRequest.Software software : ServersRequest.Software.values()) {
                    final String softwareName = software.getName();
                    if (ImGui.selectable(softwareName, softwareName.equals(this.software.getName()))) {
                        this.software = software;
                    }
                }
                ImGui.endCombo();
            }
            if (this.software == ServersRequest.Software.CUSTOM) {
                ImGui.inputText("Custom Software", this.customSoftware);
            }
            ImGui.inputInt("Min Players", this.minPlayers, 1);
            ImGui.inputInt("Max Players", this.maxPlayers, 1);
            ImGui.inputInt("Min Online Players", this.minOnlinePlayers, 1);
            ImGui.inputInt("Max Online Players", this.maxOnlinePlayers, 1);
            if (ImGui.beginCombo("Version", this.protocol == ServersRequest.ANY_PROTOCOL ? "Any" : ProtocolVersion.getProtocol(this.protocol).getName(), ImGuiComboFlags.HeightLargest)) {
                final List<Integer> protocols = new ArrayList<>();
                for (final ProtocolVersion protocolVersion : ProtocolVersion.getProtocols()) {
                    if (protocolVersion.getVersionType() == VersionType.RELEASE) {
                        protocols.add(protocolVersion.getVersion());
                    }
                }
                protocols.add(ServersRequest.ANY_PROTOCOL);
                for (int i = protocols.size() - 1; i > -1; i--) {
                    final int protocol = protocols.get(i);
                    if (protocol == ServersRequest.ANY_PROTOCOL) {
                        if (ImGui.selectable("Any", this.protocol == ServersRequest.ANY_PROTOCOL)) {
                            this.protocol = protocol;
                        }
                    } else {
                        final String protocolVersionName = ProtocolVersion.getProtocol(protocol).getName();
                        if (ImGui.selectable(protocolVersionName, protocolVersionName.equals(ProtocolVersion.getProtocol(this.protocol).getName()))) {
                            this.protocol = protocol;
                        }
                    }
                }
                ImGui.endCombo();
            }
            ImGui.checkbox("Ignore Modded", this.ignoreModded);
            ImGui.checkbox("Only Bungee Spoofable", this.onlyBungeeSpoofable);
            ImGui.inputInt("Online After", this.onlineAfter, 1);
            if (!this.waitingForResponse) {
                if (ImUtils.subButton("Get")) {
                    final ServersRequest serversRequest = new ServersRequest(
                            this.asn.get(),
                            this.country,
                            this.cracked.get(),
                            this.description.get(),
                            this.software,
                            this.minPlayers.get(),
                            this.maxPlayers.get(),
                            this.minOnlinePlayers.get(),
                            this.maxOnlinePlayers.get(),
                            this.onlineAfter.get(),
                            this.protocol,
                            this.ignoreModded.get(),
                            this.onlyBungeeSpoofable.get()
                    );
                    if (this.software == ServersRequest.Software.CUSTOM) {
                        serversRequest.customSoftware = this.customSoftware.get();
                    }
                    this.executorService.submit(() -> {
                        this.waitingForResponse = true;
                        final Response response = Vandalism.getInstance().getServerDiscoveryManager().request(serversRequest);
                        CopyOnWriteArrayList<Pair<String, String>> errorDisplay = new CopyOnWriteArrayList<>();
                        errorDisplay.add(new Pair<>("Error", "API request failed!"));
                        if (response instanceof final ServersResponse serversResponse) {
                            if (serversResponse.isError()) {
                                errorDisplay.add(new Pair<>("Message", "Response failed due to " + serversResponse.error));
                            } else if (serversResponse.data == null || serversResponse.data.isEmpty()) {
                                errorDisplay.add(new Pair<>("Message", "No servers found!"));
                            } else {
                                for (final ServersResponse.Server server : serversResponse.data) {
                                    boolean contains = false;
                                    for (final ListDataEntry containedServerEntry : this.serverDataEntries) {
                                        if (server.server.equals(containedServerEntry.getFirst().getRight())) {
                                            contains = true;
                                            break;
                                        }
                                    }
                                    if (!contains) {
                                        final CopyOnWriteArrayList<Pair<String, String>> serverEntry = new CopyOnWriteArrayList<>();
                                        serverEntry.add(new Pair<>("Server", server.server));
                                        serverEntry.add(new Pair<>("Description", ServerUtil.fixDescription(server.description)));
                                        serverEntry.add(new Pair<>("Version", ServerUtil.fixVersionName(server.version, true)));
                                        serverEntry.add(new Pair<>("Protocol", String.valueOf(server.protocol)));
                                        serverEntry.add(new Pair<>("Players", server.online_players + "/" + server.max_players));
                                        serverEntry.add(new Pair<>("Cracked", String.valueOf(server.cracked)));
                                        serverEntry.add(new Pair<>("Last Seen", TimeFormatter.formatDateTime(Instant.ofEpochSecond(server.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime())));
                                        this.serverDataEntries.add(new ListDataEntry(serverEntry));
                                    }
                                }
                                errorDisplay = null;
                            }
                        } else {
                            errorDisplay.add(new Pair<>("Message", response.error));
                        }
                        if (errorDisplay != null) {
                            this.serverDataEntries.add(new ListDataEntry(errorDisplay));
                        }
                        this.waitingForResponse = false;
                    });
                }
                if (!this.serverDataEntries.isEmpty()) {
                    if (ImUtils.subButton("Clear")) {
                        this.serverDataEntries.clear();
                    }
                }
            }
            ImGui.endMenu();
        }
    }

    public boolean render(final String id, final String name) {
        boolean isSelected = false;
        boolean containsLastServer = false;
        if (ServerUtil.lastServerExists()) {
            for (final ListDataEntry serverEntry : this.serverDataEntries) {
                if (ServerUtil.getLastServerInfo().address.equals(serverEntry.getFirst().getRight())) {
                    containsLastServer = true;
                    break;
                }
            }
        }
        if (containsLastServer) {
            ImGui.pushStyleColor(ImGuiCol.Tab, 1.0f, 0.0f, 0.0f, 0.4f);
            ImGui.pushStyleColor(ImGuiCol.TabActive, 1.0f, 0.0f, 0.0f, 0.4f);
            ImGui.pushStyleColor(ImGuiCol.TabHovered, 0.8f, 0.0f, 0.0f, 0.4f);
        }
        if (ImGui.beginTabItem(name + id)) {
            isSelected = true;
            if (!this.serverDataEntries.isEmpty() && !this.waitingForResponse) {
                ImGui.text("Search for Servers (" + this.serverDataEntries.size() + ")");
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(id + "searchField", this.serversSearchField);
                ImGui.separator();
                if (this.checkedServers.get() < 0) {
                    if (ImUtils.subButton("Filter Offline Servers" + id + "filterOfflineServers")) {
                        this.checkedServers.set(0);
                        this.lastMaxServers = this.serverDataEntries.size();
                        this.checkTimeout.reset();
                        this.executorService.execute(() -> {
                            for (final ListDataEntry serverEntry : this.serverDataEntries) {
                                final Pair<String, Integer> serverAddress = ServerUtil.resolveServerAddress(serverEntry.getFirst().getRight());
                                final String resolvedAddress = serverAddress.getLeft();
                                final int resolvedPort = serverAddress.getRight();
                                MCPing.pingModern(this.protocol)
                                        .address(resolvedAddress, resolvedPort)
                                        .timeout(5000, 5000)
                                        .exceptionHandler(t -> {
                                            this.serverDataEntries.remove(serverEntry);
                                            this.checkedServers.getAndIncrement();
                                            Vandalism.getInstance().getLogger().info("Filtered offline server {}:{}", resolvedAddress, resolvedPort);
                                        })
                                        .finishHandler(response -> this.checkedServers.getAndIncrement()).getAsync();
                            }
                        });
                    }
                }
                if (ImUtils.subButton("Add All Servers" + id + "addAllServers")) {
                    final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                    serverList.loadFile();
                    int i = 0;
                    for (final ListDataEntry serverEntry : this.serverDataEntries) {
                        i++;
                        serverList.add(new ServerInfo(
                                "Server Discovery (" + (i < 10 ? "0" + i : i) + ")",
                                serverEntry.getFirst().getRight(),
                                ServerInfo.ServerType.OTHER
                        ), false);
                    }
                    serverList.saveFile();
                }
                if (this.checkedServers.get() > -1) {
                    ImGui.text("Offline Filter Progress");
                    ImGui.progressBar((float) this.checkedServers.get() / (float) this.lastMaxServers);
                    ImGui.text(this.checkedServers + " / " + this.lastMaxServers);
                    ImGui.separator();
                    if (this.checkedServers.get() >= this.lastMaxServers) {
                        this.checkedServers.set(-1);
                    } else if (this.checkTimeout.hasReached(60000, true)) {
                        this.checkedServers.set(-1);
                    }
                }
                this.renderDataList(id, -1, -5, 120f, this.serverDataEntries);
            } else {
                if (this.waitingForResponse) {
                    ImGui.text("Waiting for response...");
                } else {
                    ImGui.text("Waiting for input...");
                }
            }
            ImGui.endTabItem();
        }
        if (containsLastServer) ImGui.popStyleColor(3);
        return isSelected;
    }

    @Override
    public boolean filterDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            return !this.serversSearchField.get().isBlank() && !StringUtils.contains(listDataEntry.getData(), this.serversSearchField.get());
        }
        return false;
    }

    @Override
    public boolean shouldHighlightDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String address = listDataEntry.getFirst().getRight();
            return
                    (listDataEntry.getList().size() >= 6 && listDataEntry.getList().get(5).getRight().equals("true")) ||
                            ServerUtil.lastServerExists() && ServerUtil.getLastServerInfo().address.equals(address) ||
                            this.lastAddress.equals(address);
        }
        return false;
    }

    @Override
    public float[] getDataEntryHighlightColor(final DataEntry dataEntry) {
        float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String address = listDataEntry.getFirst().getRight();
            if (ServerUtil.lastServerExists() && ServerUtil.getLastServerInfo().address.equals(address) || this.lastAddress.equals(address)) {
                color = new float[]{0.8f, 0.1f, 0.1f, 0.30f};
            }
        }
        return color;
    }

    @Override
    public void onDataEntryClick(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String address = listDataEntry.getFirst().getRight();
            this.lastAddress = address;
            ServerUtil.connect(address);
        }
    }

    @Override
    public void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String address = listDataEntry.getFirst().getRight();
            final float buttonWidth = ImUtils.modulateDimension(200), buttonHeight = ImUtils.modulateDimension(28);
            try {
                final Pair<String, String> protocolVersionPair = listDataEntry.getFourth();
                if (protocolVersionPair != null) {
                    final ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(Integer.parseInt(protocolVersionPair.getRight()));
                    if (protocolVersion.isKnown()) {
                        if (ImGui.button("Connect with Server Version" + id + "connectWithServerVersion", buttonWidth, buttonHeight)) {
                            this.lastAddress = address;
                            ServerUtil.connectWithVFPFix(address, protocolVersion, false);
                        }
                    }
                }
            } catch (final NumberFormatException ignored) {
            }
            if (ImGui.button("Add to the Server List" + id + "addToServerList", buttonWidth, buttonHeight)) {
                final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                serverList.loadFile();
                serverList.add(new ServerInfo(
                        "Server Discovery (" + (index < 10 ? "0" + index : index) + ")",
                        address,
                        ServerInfo.ServerType.OTHER
                ), false);
                serverList.saveFile();
            }
            if (ImGui.button("Copy Address" + id + "copyAddress", buttonWidth, buttonHeight)) {
                this.mc.keyboard.setClipboard(address);
            }
            if (ImGui.button("Copy Data" + id + "copyData", buttonWidth, buttonHeight)) {
                this.mc.keyboard.setClipboard(listDataEntry.getData());
            }
        }
    }

}
