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

package de.nekosarekawaii.vandalism.clientmenu.impl.port;

import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.impl.widget.ServerInfosTableColumn;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class PortScannerClientMenuWindow extends ClientMenuWindow {

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

    private final ImString hostname, state, progress;
    private final ImInt minPort, maxPort, threads;
    private final ConcurrentHashMap<Integer, PortResult> ports;
    private int currentPort, checkedPort;

    public PortScannerClientMenuWindow() {
        super("Port Scanner", Category.SERVER_UTILS);
        this.hostname = new ImString(253);
        this.progress = new ImString(200);
        this.state = new ImString(100);
        this.state.set(State.WAITING_INPUT.getMessage());
        this.minPort = new ImInt(1);
        this.maxPort = new ImInt(65535);
        this.threads = new ImInt(500);
        this.ports = new ConcurrentHashMap<>();
    }

    private void reset() {
        this.ports.clear();
        this.currentPort = this.minPort.get() - 1;
        this.checkedPort = this.minPort.get();
        this.progress.clear();
        this.state.set(State.WAITING_INPUT.getMessage());
    }

    private boolean isRunning() {
        return this.state.get().equals(State.RUNNING.getMessage());
    }

    private void stop() {
        this.state.set(State.WAITING_INPUT.getMessage());
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        for (final PortResult portResult : this.ports.values()) {
            portResult.renderSubData();
        }
        if (ImGui.begin("Port Scanner##portscanner", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("Current State:");
            ImGui.text(this.state.get());
            ImGui.separator();
            if (this.isRunning()) {
                ImGui.text("Progress");
                ImGui.progressBar((float) this.checkedPort / (float) this.maxPort.get());
                ImGui.text(this.checkedPort + " / " + this.maxPort.get());
                ImGui.separator();
            } else {
                ImGui.inputText(
                        "Hostname##portscannerhostname",
                        this.hostname,
                        ImGuiInputTextFlags.CallbackCharFilter,
                        HOSTNAME_FILTER
                );
            }
            if (
                    !this.hostname.get().isBlank() &&
                            this.hostname.get().length() >= 4 &&
                            this.hostname.get().contains(".") &&
                            this.hostname.get().indexOf(".") < this.hostname.get().length() - 2
            ) {
                if (!this.isRunning()) {
                    boolean shouldSetFocus = false;
                    if (this.hostname.get().contains(":")) {
                        final String[] data = this.hostname.get().split(":");
                        this.hostname.set(data[0]);
                        shouldSetFocus = true;
                        if (data.length >= 2) {
                            try {
                                final int port = Integer.parseInt(data[1]);
                                this.minPort.set(Math.max(1, Math.min(port - 500, 65535)));
                                this.maxPort.set(Math.max(port, Math.min(port + 500, 65535)));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    ImGui.inputInt("Min Port##portscannerminport", this.minPort, 1);
                    this.minPort.set(Math.max(1, Math.min(this.minPort.get(), this.maxPort.get() - 1)));
                    ImGui.inputInt("Max Port##portscannermaxport", this.maxPort, 1);
                    this.maxPort.set(Math.max(this.minPort.get() + 1, Math.min(this.maxPort.get(), 65535)));
                    if (ImGui.inputInt("Threads##portscannerthreads", this.threads, 1)) {
                        this.threads.set(Math.max(1, Math.min(this.threads.get(), 1000)));
                    }
                    if (this.minPort.get() != 1 || this.maxPort.get() != 65535 || this.threads.get() != 500) {
                        if (ImGui.button("Reset Values##portscannerresetvalues")) {
                            this.minPort.set(1);
                            this.maxPort.set(65535);
                            this.threads.set(500);
                        }
                        ImGui.sameLine();
                    }
                    if (!this.ports.isEmpty()) {
                        if (ImGui.button("Clear##portscannerclear")) {
                            this.reset();
                        }
                        ImGui.sameLine();
                    }
                    if (shouldSetFocus) {
                        ImGui.setKeyboardFocusHere();
                    }
                    if (ImGui.button("Start##portscannerstart")) {
                        this.reset();
                        this.state.set(State.RUNNING.getMessage());
                        for (int i = 0; i < this.threads.get(); i++) {
                            Executors.newSingleThreadExecutor().submit(() -> {
                                try {
                                    while (this.isRunning() && this.currentPort < this.maxPort.get()) {
                                        this.currentPort++;
                                        final int port = this.currentPort;
                                        try {
                                            final Socket socket = new Socket();
                                            socket.connect(new InetSocketAddress(this.hostname.get(), port), 500);
                                            socket.close();
                                            synchronized (this.ports) {
                                                if (!this.ports.containsKey(port)) {
                                                    this.ports.put(port, new PortResult(port, this.hostname.get()));
                                                }
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        if (this.checkedPort < port) {
                                            this.checkedPort = port;
                                        }
                                    }
                                    this.stop();
                                } catch (Exception e) {
                                    this.state.set(State.FAILED.getMessage() + e.getClass().getSimpleName() + ": " + e.getMessage());
                                }
                            });
                        }
                    }
                }
            }
            if (this.isRunning()) {
                if (ImGui.button("Stop##portscannerstop")) {
                    this.stop();
                }
            }
            if (!this.ports.isEmpty()) {
                ImGui.separator();
                ImGui.text("Ports");
                final List<PortResult> uncheckedPortResults = new ArrayList<>();
                final List<PortResult> validPortResults = new ArrayList<>();
                for (final PortResult portResult : this.ports.values()) {
                    if (portResult.getCurrentState() == PortResult.PingState.WAITING_INPUT && portResult.getCurrentQueryState() == PortResult.PingState.WAITING_INPUT) {
                        uncheckedPortResults.add(portResult);
                    }
                    if (portResult.getMcPingResponse() != null) {
                        validPortResults.add(portResult);
                    }
                }
                if (!uncheckedPortResults.isEmpty()) {
                    if (ImGui.button("Ping All Ports##portscannerpingallports")) {
                        for (final PortResult portResult : uncheckedPortResults) {
                            portResult.ping();
                        }
                    }
                }
                final PortsTableColumn[] portsTableColumns = PortsTableColumn.values();
                final int maxPortsTableColumns = portsTableColumns.length;
                if (ImGui.beginTable("ports##portstable", maxPortsTableColumns,
                        ImGuiTableFlags.Borders |
                                ImGuiTableFlags.Resizable |
                                ImGuiTableFlags.RowBg |
                                ImGuiTableFlags.ContextMenuInBody
                )) {
                    for (final PortsTableColumn portsTableColumn : portsTableColumns) {
                        ImGui.tableSetupColumn(portsTableColumn.getName());
                    }
                    ImGui.tableHeadersRow();
                    for (final PortResult portResult : this.ports.values()) {
                        ImGui.tableNextRow();
                        for (int i = 0; i < maxPortsTableColumns; i++) {
                            ImGui.tableSetColumnIndex(i);
                            final PortsTableColumn portsTableColumn = portsTableColumns[i];
                            switch (portsTableColumn) {
                                case PORT -> ImGui.text(String.valueOf(portResult.getPort()));
                                case STATE -> ImGui.text(portResult.getCurrentState().getMessage());
                                case QUERY_STATE -> ImGui.text(portResult.getCurrentQueryState().getMessage());
                                case ACTIONS -> {
                                    final int buttonWidth = 0, buttonHeight = 28;
                                    if (portResult.getCurrentState() == PortResult.PingState.WAITING_INPUT) {
                                        if (ImGui.button("Ping##portsping" + portResult.getPort(), buttonWidth, buttonHeight)) {
                                            portResult.ping();
                                        }
                                    }
                                }
                                default -> {
                                }
                            }
                        }
                    }
                    ImGui.endTable();
                }
                ImGui.separator();
                ImGui.text("Server Infos");
                ImGui.separator();
                if (!validPortResults.isEmpty()) {
                    if (ImGui.button("Add all Results to the Server List##portsaddallresultstoserverlistportscanner")) {
                        final net.minecraft.client.option.ServerList serverList = new net.minecraft.client.option.ServerList(MinecraftClient.getInstance());
                        serverList.loadFile();
                        for (final PortResult portResult : validPortResults) {
                            serverList.add(new ServerInfo(
                                    "Port Scan Result (" + portResult.getPort() + ")",
                                    portResult.getHostname() + ":" + portResult.getPort(),
                                    ServerInfo.ServerType.OTHER
                            ), false);
                        }
                        serverList.saveFile();
                    }
                    final ServerInfosTableColumn[] serverInfosTableColumns = ServerInfosTableColumn.values();
                    final int maxServerInfosTableColumns = serverInfosTableColumns.length;
                    if (ImGui.beginTable("serverinfos##serverinfostableportscanner", maxServerInfosTableColumns,
                            ImGuiTableFlags.Borders |
                                    ImGuiTableFlags.Resizable |
                                    ImGuiTableFlags.RowBg |
                                    ImGuiTableFlags.ContextMenuInBody
                    )) {
                        for (final ServerInfosTableColumn serverInfosTableColumn : serverInfosTableColumns) {
                            ImGui.tableSetupColumn(serverInfosTableColumn.getName());
                        }
                        ImGui.tableHeadersRow();
                        for (final PortResult portResult : validPortResults) {
                            portResult.renderTableEntry();
                        }
                        ImGui.endTable();
                    }
                }
                else {
                    ImGui.text("No valid Results.");
                }
            }
            ImGui.end();
        }
    }

    private enum State {

        RUNNING("Running..."),
        FAILED("Failed: "),
        WAITING_INPUT("Waiting for input...");

        private final String message;

        State(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

    }

}
