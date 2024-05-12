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
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.widget.ServerInfoWidget;
import de.nekosarekawaii.vandalism.clientwindow.impl.widget.ServerInfosTableColumn;
import de.nekosarekawaii.vandalism.util.common.MSTimer;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import lombok.Getter;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;

import java.net.BindException;
import java.net.UnknownHostException;

public class ServerPingerClientWindow extends ClientWindow {

    private static final ImGuiInputTextCallback HOSTNAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '.' &&
                            imGuiInputTextCallbackData.getEventChar() != '-' &&
                            imGuiInputTextCallbackData.getEventChar() != ':'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString hostname;
    private final ImInt port, queryPort, protocol, autoPingTime;
    private final MSTimer autoPingTimer;
    private State currentState, currentQueryState;
    private boolean autoPing;
    private final ServerInfoWidget serverInfoWidget;

    public ServerPingerClientWindow() {
        super("Server Pinger", Category.SERVER);
        this.hostname = new ImString(253);
        this.port = new ImInt(25565);
        this.queryPort = new ImInt(25565);
        this.protocol = new ImInt(SharedConstants.getProtocolVersion());
        this.autoPingTime = new ImInt(8000);
        this.autoPingTimer = new MSTimer();
        this.currentState = State.WAITING_INPUT;
        this.currentQueryState = State.WAITING_INPUT;
        this.autoPing = false;
        this.serverInfoWidget = new ServerInfoWidget();
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.serverInfoWidget.renderSubData();
        ImGui.text("State: " + this.currentState.getMessage());
        ImGui.text("Query State: " + this.currentQueryState.getMessage());
        ImGui.inputText(
                "Hostname##serverpingerhostname",
                this.hostname,
                ImGuiInputTextFlags.CallbackCharFilter,
                HOSTNAME_FILTER
        );
        if (this.hostname.get().contains(":")) {
            final String[] data = this.hostname.get().split(":");
            this.hostname.set(data[0]);
            if (data.length >= 2) {
                try {
                    this.port.set(Integer.parseInt(data[1]));
                } catch (Exception ignored) {
                }
            }
        }
        if (
                !this.hostname.get().isBlank() &&
                        this.hostname.get().length() >= 4 &&
                        this.hostname.get().contains(".") &&
                        this.hostname.get().indexOf(".") < this.hostname.get().length() - 2
        ) {
            if (ImGui.button("Clear Hostname##clearhostnameserverpinger")) {
                this.hostname.clear();
            }
            if (ImGui.inputInt("Port##portserverpinger", this.port, 1)) {
                this.port.set(Math.max(1, Math.min(this.port.get(), 65535)));
            }
            if (this.port.get() != 25565) {
                if (ImGui.button("Reset Port##portresetserverpinger")) {
                    this.port.set(25565);
                }
            }
            if (ImGui.inputInt("Query Port##queryportserverpinger", this.queryPort, 1)) {
                this.queryPort.set(Math.max(1, Math.min(this.queryPort.get(), 65535)));
            }
            if (this.queryPort.get() != 25565) {
                if (ImGui.button("Reset Query Port##queryresetserverpinger")) {
                    this.queryPort.set(25565);
                }
            }
            ImGui.inputInt("Protocol##protocolserverpinger", this.protocol, 1);
            if (this.protocol.get() != SharedConstants.getProtocolVersion()) {
                if (ImGui.button("Reset Protocol##protocolresetserverpinger")) {
                    this.protocol.set(SharedConstants.getProtocolVersion());
                }
            }
            if (ImGui.inputInt("Auto Ping Time##autopingtimeserverpinger", this.autoPingTime, 1)) {
                this.autoPingTime.set(Math.max(1000, Math.min(this.autoPingTime.get(), 60000)));
            }
            if (this.autoPing && !this.hostname.get().isBlank()) {
                if (this.currentState != State.WAITING_RESPONSE) {
                    ImGui.text("Pinging in " + (this.autoPingTime.get() - this.autoPingTimer.getDelta()) + "ms");
                    if (this.autoPingTimer.hasReached(this.autoPingTime.get(), true)) {
                        this.ping();
                    }
                } else ImGui.text("Pinging...");
            }
            if (!this.hostname.get().isBlank() && this.currentState != State.WAITING_RESPONSE) {
                if (ImGui.button("Auto Ping: " + (this.autoPing ? "Deactivate" : "Activate") + "##autopingerverpinger")) {
                    this.autoPing = !this.autoPing;
                }
                if (!this.autoPing) {
                    ImGui.sameLine();
                    if (ImGui.button("Ping##pingserverpinger")) {
                        this.ping();
                    }
                }
            }
            if (this.serverInfoWidget.getMcPingResponse() != null) {
                if (ImGui.button("Clear##clearserverpinger")) {
                    this.clear();
                }
                final ServerInfosTableColumn[] serverInfosTableColumns = ServerInfosTableColumn.values();
                final int maxServerInfosTableColumns = serverInfosTableColumns.length;
                if (ImGui.beginTable("serverinfos##serverinfostableserverpinger", maxServerInfosTableColumns,
                        ImGuiTableFlags.Borders |
                                ImGuiTableFlags.Resizable |
                                ImGuiTableFlags.RowBg |
                                ImGuiTableFlags.ContextMenuInBody
                )) {
                    for (final ServerInfosTableColumn serverInfosTableColumn : serverInfosTableColumns) {
                        ImGui.tableSetupColumn(serverInfosTableColumn.getName());
                    }
                    ImGui.tableHeadersRow();
                    this.serverInfoWidget.renderTableEntry(true);
                    ImGui.endTable();
                }
            }
        }
    }

    private void clear() {
        this.serverInfoWidget.setMcPingResponse(null);
        this.serverInfoWidget.setQueryPingResponse(null);
        this.currentState = State.WAITING_INPUT;
        this.currentQueryState = State.WAITING_INPUT;
    }

    private void ping() {
        if (!this.hostname.get().isBlank()) {
            this.clear();
            this.serverInfoWidget.setAddress(this.hostname.get());
            this.currentState = State.WAITING_RESPONSE;
            this.currentQueryState = State.WAITING_RESPONSE;
            MCPing.pingModern(this.protocol.get())
                    .address(this.hostname.get(), this.port.get())
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        switch (t) {
                            case UnknownHostException unknownHostException -> this.currentState = State.UNKNOWN_HOST;
                            case ConnectionRefusedException connectionRefusedException ->
                                    this.currentState = State.CONNECTION_REFUSED;
                            case ConnectTimeoutException connectTimeoutException ->
                                    this.currentState = State.CONNECTION_TIMED_OUT;
                            case DataReadException dataReadException -> this.currentState = State.DATA_READ_FAILED;
                            case PacketReadException packetReadException ->
                                    this.currentState = State.PACKET_READ_FAILED;
                            case null, default -> {
                                this.currentState = State.FAILED;
                                Vandalism.getInstance().getLogger().error("Failed to ping {}:{}", this.hostname.get(), this.port.get(), t);
                            }
                        }
                    })
                    .finishHandler(response -> {
                        this.serverInfoWidget.setMcPingResponse(response);
                        this.currentState = State.SUCCESS;
                    })
                    .getAsync();
            MCPing.pingQuery()
                    .address(this.hostname.get(), this.queryPort.get())
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        switch (t) {
                            case BindException bindException -> this.currentQueryState = State.BIND_FAILED;
                            case UnknownHostException unknownHostException ->
                                    this.currentQueryState = State.UNKNOWN_HOST;
                            case ConnectionRefusedException connectionRefusedException ->
                                    this.currentQueryState = State.CONNECTION_REFUSED;
                            case ConnectTimeoutException connectTimeoutException ->
                                    this.currentQueryState = State.CONNECTION_TIMED_OUT;
                            case DataReadException dataReadException -> this.currentQueryState = State.DATA_READ_FAILED;
                            case PacketReadException packetReadException ->
                                    this.currentQueryState = State.PACKET_READ_FAILED;
                            case null, default -> {
                                this.currentQueryState = State.FAILED;
                                Vandalism.getInstance().getLogger().error("Failed to ping query {}:{}", this.hostname.get(), this.queryPort.get(), t);
                            }
                        }
                    })
                    .finishHandler(response -> {
                        this.serverInfoWidget.setQueryPingResponse(response);
                        this.currentQueryState = State.SUCCESS;
                    })
                    .getAsync();
        } else this.currentState = State.WAITING_INPUT;
    }

    @Getter
    private enum State {

        FAILED("There was an error fetching the server info."),
        BIND_FAILED("Cannot assign requested address."),
        UNKNOWN_HOST("Unknown Host."),
        CONNECTION_REFUSED("Connection Refused."),
        CONNECTION_TIMED_OUT("Connection timed out."),
        DATA_READ_FAILED("Failed to read data."),
        PACKET_READ_FAILED("Failed to read packet."),
        SUCCESS("Successfully fetched the server info."),
        WAITING_RESPONSE("Waiting for response..."),
        WAITING_INPUT("Waiting for input...");

        private final String message;

        State(final String message) {
            this.message = message;
        }

    }

}
