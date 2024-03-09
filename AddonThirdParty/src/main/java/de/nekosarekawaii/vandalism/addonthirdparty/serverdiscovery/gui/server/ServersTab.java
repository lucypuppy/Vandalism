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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.math.timer.MSTimer;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.ServerDiscoveryUtil;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.request.impl.ServersRequest;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.impl.ServersResponse;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.mcping.MCPing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.raphimc.viaaprilfools.api.AprilFoolsProtocolVersion;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.vialoader.util.ProtocolVersionList;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServersTab implements MinecraftWrapper {

    private final ImInt asn = new ImInt(0);
    private Country country = Country.ANY;
    private final ImString countrySearch = new ImString();
    private final ImBoolean cracked = new ImBoolean(false);
    private final ImString description = new ImString();
    private ServersRequest.Software software = ServersRequest.Software.ANY;
    private final ImInt minPlayers = new ImInt(0);
    private final ImInt maxPlayers = new ImInt(-1);
    private final ImInt minOnlinePlayers = new ImInt(0);
    private final ImInt maxOnlinePlayers = new ImInt(-1);

    private int protocol = ServersRequest.ANY_PROTOCOL;
    private final ImBoolean ignoreModded = new ImBoolean(false);
    private final ImBoolean onlyBungeeSpoofable = new ImBoolean(false);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean waitingForResponse = false;
    private final ImString serversSearchField = new ImString();
    private final List<ServersResponse.Server> servers = new CopyOnWriteArrayList<>();

    private String lastAddress = "";

    private int checkedServers = -1;
    private int lastMaxServers = -1;

    private final MSTimer checkTimeout = new MSTimer();

    public void renderMenu(final String name) {
        final ServerDiscoveryClientMenuWindow serverDiscoveryClientMenuWindow = Vandalism.getInstance().getClientMenuManager().getByClass(ServerDiscoveryClientMenuWindow.class);
        if (!serverDiscoveryClientMenuWindow.isCurrentServerTab(name)) {
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
            ImGui.inputInt("Min Players", this.minPlayers, 1);
            ImGui.inputInt("Max Players", this.maxPlayers, 1);
            ImGui.inputInt("Min Online Players", this.minOnlinePlayers, 1);
            ImGui.inputInt("Max Online Players", this.maxOnlinePlayers, 1);
            if (ImGui.beginCombo("Version", this.protocol == ServersRequest.ANY_PROTOCOL ? "Any" : ProtocolVersion.getProtocol(this.protocol).getName(), ImGuiComboFlags.HeightLargest)) {
                final List<Integer> protocols = new ArrayList<>();
                protocols.add(ServersRequest.ANY_PROTOCOL);
                for (final ProtocolVersion protocolVersion : ProtocolVersionList.getProtocolsNewToOld()) {
                    if (
                            protocolVersion.olderThan(ProtocolVersion.v1_8) ||
                            AprilFoolsProtocolVersion.PROTOCOLS.contains(protocolVersion) ||
                            protocolVersion.equals(BedrockProtocolVersion.bedrockLatest) ||
                            protocolVersion.equals(ProtocolTranslator.AUTO_DETECT_PROTOCOL)
                    ) {
                        continue;
                    }
                    protocols.add(protocolVersion.getVersion());
                }
                for (final Integer protocol : protocols) {
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
                            this.protocol,
                            this.ignoreModded.get(),
                            this.onlyBungeeSpoofable.get()
                    );
                    this.executorService.submit(() -> {
                        this.waitingForResponse = true;
                        final Response response = ServerDiscoveryUtil.request(serversRequest);
                        ServersResponse.Server errorDisplay = new ServersResponse.Server();
                        errorDisplay.description = "API request failed!";
                        if (response == null) {
                            errorDisplay.version = "Every API User is rate limited!";
                        } else if (response instanceof final ServersResponse serversResponse) {
                            if (serversResponse.isError()) {
                                errorDisplay.version = "Response failed due to " + serversResponse.error;
                            } else if (serversResponse.data == null || serversResponse.data.isEmpty()) {
                                errorDisplay.version = "No servers found!";
                            } else {
                                for (final ServersResponse.Server server : serversResponse.data) {
                                    boolean contains = false;
                                    for (final ServersResponse.Server containedServer : this.servers) {
                                        if (server.server.equals(containedServer.server)) {
                                            contains = true;
                                            break;
                                        }
                                    }
                                    if (!contains) {
                                        this.servers.add(server);
                                    }
                                }
                                errorDisplay = null;
                            }
                        }
                        if (errorDisplay != null) {
                            this.servers.add(errorDisplay);
                        }
                        this.waitingForResponse = false;
                    });
                }
                if (!this.servers.isEmpty()) {
                    if (ImUtils.subButton("Clear")) {
                        this.servers.clear();
                    }
                }
            }
            ImGui.endMenu();
        }
    }

    public boolean render(final String name) {
        boolean isSelected = false;
        boolean containsLastServer = false;
        if (ServerConnectionUtil.lastServerExists()) {
            for (final ServersResponse.Server server : this.servers) {
                if (ServerConnectionUtil.getLastServerInfo().address.equals(server.server)) {
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
        if (ImGui.beginTabItem(name + "##serversTab" + name)) {
            isSelected = true;
            if (!this.servers.isEmpty() && !this.waitingForResponse) {
                ImGui.text("Found " + this.servers.size() + " server/s.");
                ImGui.spacing();
                ImGui.text("Search for servers");
                ImGui.setNextItemWidth(-1);
                ImGui.inputText("##serverSearchField", this.serversSearchField);
                ImGui.separator();
                if (this.checkedServers < 0) {
                    if (ImUtils.subButton("Remove offline servers")) {
                        this.checkedServers = 0;
                        this.lastMaxServers = this.servers.size();
                        this.checkTimeout.reset();
                        this.executorService.submit(() -> {
                            for (final ServersResponse.Server server : this.servers) {
                                final Pair<String, Integer> serverAddress = ServerConnectionUtil.resolveServerAddress(server.server);
                                final String resolvedAddress = serverAddress.getLeft();
                                final int resolvedPort = serverAddress.getRight();
                                MCPing.pingModern(this.protocol)
                                        .address(resolvedAddress, resolvedPort)
                                        .timeout(5000, 5000)
                                        .exceptionHandler(t -> {
                                            this.servers.remove(server);
                                            this.checkedServers++;
                                            Vandalism.getInstance().getLogger().info("Removed offline server " + resolvedAddress + ":" + resolvedPort);
                                        })
                                        .finishHandler(response -> this.checkedServers++).getAsync();
                            }
                        });
                    }
                }
                if (ImUtils.subButton("Add all servers")) {
                    final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                    serverList.loadFile();
                    int i = 0;
                    for (final ServersResponse.Server server : this.servers) {
                        i++;
                        serverList.add(new ServerInfo(
                                "Server Discovery (" + (i < 10 ? "0" + i : i) + ")",
                                server.server,
                                ServerInfo.ServerType.OTHER
                        ), false);
                    }
                    serverList.saveFile();
                }
                if (this.checkedServers > -1) {
                    ImGui.text("Offline Removal Progress");
                    ImGui.progressBar((float) this.checkedServers / (float) this.lastMaxServers);
                    ImGui.text(this.checkedServers + " / " + this.lastMaxServers);
                    ImGui.separator();
                    if (this.checkedServers >= this.lastMaxServers) {
                        this.checkedServers = -1;
                    } else if (this.checkTimeout.hasReached(60000, true)) {
                        this.checkedServers = -1;
                    }
                }
                ImGui.beginChild("##servers", -1, -1, true, ImGuiWindowFlags.HorizontalScrollbar);
                int i = 0;
                boolean hasLastServer = false;
                for (final ServersResponse.Server serverEntry : this.servers) {
                    i++;
                    final String address = serverEntry.server;
                    final boolean cracked = serverEntry.cracked;
                    boolean isLastServer = ServerConnectionUtil.lastServerExists() && ServerConnectionUtil.getLastServerInfo().address.equals(address) || this.lastAddress.equals(address);
                    if (hasLastServer) {
                        isLastServer = false;
                    }
                    if (isLastServer) {
                        hasLastServer = true;
                    }
                    final StringBuilder dataString = new StringBuilder();
                    String description = Formatting.strip(serverEntry.description);
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
                    dataString.append(serverEntry.version);
                    dataString.append("\n");
                    dataString.append("Protocol: ");
                    dataString.append(serverEntry.protocol);
                    dataString.append("\n");
                    dataString.append("Players: ");
                    dataString.append(serverEntry.online_players);
                    dataString.append("/");
                    dataString.append(serverEntry.max_players);
                    dataString.append("\n");
                    dataString.append("Last Seen: ");
                    dataString.append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(
                            Instant.ofEpochSecond(serverEntry.last_seen).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    ));
                    final String data = dataString.toString();
                    if (!this.serversSearchField.get().isBlank() && !StringUtils.contains(data, this.serversSearchField.get())) {
                        continue;
                    }
                    final boolean shouldHighlight = cracked || isLastServer;
                    if (shouldHighlight) {
                        float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
                        if (isLastServer) {
                            color = new float[]{0.8f, 0.1f, 0.1f, 0.30f};
                        }
                        ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                        ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
                    }
                    final String serverEntryId = "##serverEntry" + address;
                    if (ImGui.button(serverEntryId, ImGui.getColumnWidth() - 8, 90)) {
                        this.lastAddress = address;
                        ServerConnectionUtil.connect(address);
                    }
                    if (shouldHighlight) {
                        ImGui.popStyleColor(3);
                    }
                    if (ImGui.beginPopupContextItem(serverEntryId + "popup", ImGuiPopupFlags.MouseButtonRight)) {
                        final int buttonWidth = 150, buttonHeight = 28;
                        final ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(serverEntry.protocol);
                        if (protocolVersion.isKnown()) {
                            if (ImGui.button("Connect with server version" + serverEntryId + "connectwithserverversion", buttonWidth, buttonHeight)) {
                                ProtocolTranslator.setTargetVersion(protocolVersion, true);
                                this.lastAddress = address;
                                ServerConnectionUtil.connect(address);
                            }
                        }
                        if (ImGui.button("Add to the Server List" + serverEntryId + "addtoserverlist", buttonWidth, buttonHeight)) {
                            final ServerList serverList = new ServerList(MinecraftClient.getInstance());
                            serverList.loadFile();
                            serverList.add(new ServerInfo(
                                    "Server Discovery (" + (i < 10 ? "0" + i : i) + ")",
                                    address,
                                    ServerInfo.ServerType.OTHER
                            ), false);
                            serverList.saveFile();
                        }
                        if (ImGui.button("Copy Address" + serverEntryId + "copyaddress", buttonWidth, buttonHeight)) {
                            this.mc.keyboard.setClipboard(address);
                        }
                        if (ImGui.button("Copy Data" + serverEntryId + "copydata", buttonWidth, buttonHeight)) {
                            this.mc.keyboard.setClipboard(data);
                        }
                        ImGui.endPopup();
                    }
                    ImGui.sameLine(10);
                    ImGui.text(data);
                }
                ImGui.endChild();
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

}
