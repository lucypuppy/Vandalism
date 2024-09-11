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

package de.nekosarekawaii.vandalism.clientwindow.impl.port;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.PingState;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import lombok.Getter;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

public class PortResult extends ListDataEntry implements MinecraftWrapper {

    @Getter
    private final int port;

    @Getter
    private final String address;

    @Getter
    private MCPingResponse mcPingResponse;

    @Getter
    private String description;

    @Getter
    private PingState currentState;

    private final MSTimer stateTimer = new MSTimer();

    public PortResult(final int port, final String address) {
        super(new CopyOnWriteArrayList<>());
        this.getList().add(new Pair<>("Address", address + ":" + port));
        this.port = port;
        this.address = address;
        this.clear();
    }

    public void render() {
        if (this.currentState != PingState.WAITING_RESPONSE) {
            if (this.stateTimer.hasReached(15000, true)) {
                this.resetState();
            }
        }

    }

    private void resetState() {
        this.currentState = PingState.WAITING_INPUT;
    }

    private void clear() {
        this.mcPingResponse = null;
        this.description = "";
        this.resetState();
    }

    public void ping(final Runnable onFinished) {
        if (!this.address.isBlank()) {
            this.clear();
            MCPing.pingModern(SharedConstants.getProtocolVersion())
                    .address(this.address, this.port)
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        switch (t) {
                            case UnknownHostException unknownHostException ->
                                    this.currentState = PingState.UNKNOWN_HOST;
                            case ConnectionRefusedException connectionRefusedException ->
                                    this.currentState = PingState.CONNECTION_REFUSED;
                            case ConnectTimeoutException connectTimeoutException ->
                                    this.currentState = PingState.CONNECTION_TIMED_OUT;
                            case DataReadException dataReadException -> this.currentState = PingState.DATA_READ_FAILED;
                            case PacketReadException packetReadException ->
                                    this.currentState = PingState.PACKET_READ_FAILED;
                            case null, default -> {
                                this.currentState = PingState.FAILED;
                                Vandalism.getInstance().getLogger().error("Failed to ping {}:{}", this.address, this.port, t);
                            }
                        }
                    })
                    .finishHandler(response -> {
                        this.mcPingResponse = response;
                        if (this.mcPingResponse != null) {
                            final String descriptionString = mcPingResponse.description;
                            try {
                                final MutableText description = Text.Serialization.fromJson(descriptionString, DynamicRegistryManager.EMPTY);
                                if (description != null) this.description = description.getString();
                            } catch (final Exception ignored) {
                                this.description = descriptionString;
                            }
                        }
                        this.currentState = PingState.SUCCESS;
                        onFinished.run();
                    }).getSync();
            this.currentState = PingState.WAITING_RESPONSE;
        } else {
            this.currentState = PingState.WAITING_INPUT;
        }
    }

    public void renderContextMenu(final String id) {
        final float buttonWidth = ImUtils.modulateDimension(200), buttonHeight = ImUtils.modulateDimension(28);
        final String address = this.address + ':' + this.mcPingResponse.server.port;
        ImGui.text(address);
        ImGui.separator();
        ImGui.spacing();
        if (ImGui.button("Connect" + id + "connect", buttonWidth, buttonHeight)) {
            ServerUtil.connect(address);
        }
        final ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(this.mcPingResponse.version.protocol);
        if (protocolVersion.isKnown()) {
            if (ImGui.button("Connect with Server Version" + id + "connectWithServerVersion", buttonWidth, buttonHeight)) {
                ServerUtil.connectWithVFPFix(address, protocolVersion, true);
            }
        }
        if (ImGui.button("Add to the Server List" + id + "addToServerList", buttonWidth, buttonHeight)) {
            final net.minecraft.client.option.ServerList serverList = new net.minecraft.client.option.ServerList(MinecraftClient.getInstance());
            serverList.loadFile();
            serverList.add(new ServerInfo(
                    "Port Scan Result (" + this.mcPingResponse.server.port + ")",
                    address,
                    ServerInfo.ServerType.OTHER
            ), false);
            serverList.saveFile();
        }
        if (ImGui.button("Copy Address" + id + "copyAddress", buttonWidth, buttonHeight)) {
            this.mc.keyboard.setClipboard(address);
        }
        if (ImGui.button("Copy Data" + id + "copyData", buttonWidth, buttonHeight)) {
            final StringBuilder serverInfoBuilder = new StringBuilder();
            serverInfoBuilder.append("Server Address: ").append(address).append('\n');
            String resolvedServerAddress = this.mcPingResponse.server.ip;
            if (resolvedServerAddress.endsWith(".")) {
                resolvedServerAddress = resolvedServerAddress.substring(0, resolvedServerAddress.length() - 1);
            }
            serverInfoBuilder.append("Resolved Server Address: ").append(resolvedServerAddress).append('\n');
            if (this.mcPingResponse.version != null) {
                serverInfoBuilder.append("Protocol: ").append(this.mcPingResponse.version.protocol).append('\n');
                serverInfoBuilder.append("Version: ").append(ServerUtil.fixVersionName(this.mcPingResponse.version.name, true)).append('\n');
            }
            if (this.mcPingResponse.players != null) {
                serverInfoBuilder.append("Players: ");
                serverInfoBuilder.append(this.mcPingResponse.players.online).append('/');
                serverInfoBuilder.append(this.mcPingResponse.players.max).append('\n');
            }
            if (this.description != null) {
                serverInfoBuilder.append("Description: ").append(this.description).append('\n');
            }
            if (this.mcPingResponse.players != null && this.mcPingResponse.players.sample.length > 0) {
                serverInfoBuilder.append("Player List: ").append('\n');
                for (final MCPingResponse.Players.Player player : this.mcPingResponse.players.sample) {
                    serverInfoBuilder.append(" - ").append(player.name).append('\n');
                }
            }
            if (this.mcPingResponse.modinfo != null && this.mcPingResponse.modinfo.modList.length > 0) {
                serverInfoBuilder.append("Mods: ").append('\n');
                for (final MCPingResponse.ModInfo.Mod mod : this.mcPingResponse.modinfo.modList) {
                    serverInfoBuilder.append(" - ").append(mod.modid).append(" (").append(mod.version).append(")\n");
                }
            }
            if (this.mcPingResponse.forgeData != null && this.mcPingResponse.forgeData.mods.length > 0) {
                serverInfoBuilder.append("Forge Mods: ").append('\n');
                for (final MCPingResponse.ForgeData.Mod mod : this.mcPingResponse.forgeData.mods) {
                    serverInfoBuilder.append(" - ").append(mod.modId).append(" (").append(mod.modmarker).append(")\n");
                }
            }
            this.mc.keyboard.setClipboard(serverInfoBuilder.toString());
        }
    }

}
