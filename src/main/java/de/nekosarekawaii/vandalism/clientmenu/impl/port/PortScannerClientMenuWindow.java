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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.impl.widget.ServerInfosTableColumn;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Pair;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

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
        super("Port Scanner", Category.SERVER);
        this.hostname = new ImString(253);
        this.progress = new ImString(200);
        this.state = new ImString(100);
        this.state.set(State.WAITING_INPUT.getMessage());
        this.minPort = new ImInt(1);
        this.maxPort = new ImInt(65535);
        this.threads = new ImInt(128);
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
        ImGui.begin("Port Scanner##portscanner");
        ImGui.text("Current State: " + this.state.get());
        ImGui.separator();
        if (this.isRunning()) {
            ImGui.text("Progress");
            ImGui.progressBar((float) this.checkedPort / (float) this.maxPort.get());
            ImGui.separator();
        } else {
            ImGui.text("Hostname");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText(
                    "##portscannerhostname",
                    this.hostname,
                    ImGuiInputTextFlags.CallbackCharFilter,
                    HOSTNAME_FILTER
            );
            final ServerInfo currentServer = this.mc.getCurrentServerEntry();
            if (currentServer != null) {
                if (ImGui.button("Use Current Server##portscannerusecurrentserver", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    this.hostname.set(currentServer.address);
                }
            }
        }
        if (
                !this.hostname.get().isBlank() &&
                        this.hostname.get().length() >= 4 &&
                        this.hostname.get().contains(".") &&
                        this.hostname.get().indexOf(".") < this.hostname.get().length() - 2
        ) {
            if (!this.isRunning()) {
                if (this.hostname.get().contains(":")) {
                    final String[] data = this.hostname.get().split(":");
                    this.hostname.set(data[0]);
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
                    if (ImGui.button("Reset Values##portscannerresetvalues", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                        this.minPort.set(1);
                        this.maxPort.set(65535);
                        this.threads.set(500);
                    }
                    ImGui.sameLine();
                }
                ImGui.spacing();
                if (!this.ports.isEmpty()) {
                    if (ImGui.button("Clear##portscannerclear", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                        this.reset();
                    }
                    ImGui.sameLine();
                }
                if (ImGui.button("Start##portscannerstart", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    this.reset();
                    this.state.set(State.RUNNING.getMessage());
                    final Pair<String, Integer> serverAddress = ServerConnectionUtil.resolveServerAddress(this.hostname.get());
                    final String resolvedAddress = serverAddress.getLeft();
                    for (int i = 0; i < this.threads.get(); i++) {
                        final int id = i;
                        Vandalism.getInstance().getLogger().info("Starting Port Scanner Thread #" + id + "...");
                        new Thread(() -> {
                            try {
                                while (this.isRunning() && this.currentPort < this.maxPort.get()) {
                                    this.currentPort++;
                                    final int port = this.currentPort;
                                    try {
                                        final Socket socket = new Socket();
                                        socket.connect(new InetSocketAddress(resolvedAddress, port), 500);
                                        socket.close();
                                        synchronized (this.ports) {
                                            if (!this.ports.containsKey(port)) {
                                                final PortResult portResult = new PortResult(port, resolvedAddress);
                                                portResult.ping(() -> this.ports.put(port, portResult));
                                            }
                                        }
                                    } catch (Exception ignored) {}
                                    if (this.checkedPort < port) {
                                        this.checkedPort = port;
                                    }
                                }
                                this.stop();
                                Vandalism.getInstance().getLogger().info("Port Scanner Thread #" + id + " finished.");
                            } catch (Exception e) {
                                Vandalism.getInstance().getLogger().error("Port Scanner Thread #" + id + " failed.", e);
                                this.state.set(State.FAILED.getMessage() + e.getClass().getSimpleName() + ": " + e.getMessage());
                            }
                        }, "Port Scanner Thread #" + id).start();
                    }
                }
                ImGui.spacing();
            }
        }
        if (this.isRunning()) {
            if (ImGui.button("Stop##portscannerstop", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                this.stop();
            }
            ImGui.spacing();
        }
        ImGui.separator();
        ImGui.text("Results");
        ImGui.separator();
        if (!this.ports.isEmpty()) {
            if (ImUtils.subButton("Add all servers##portsaddallserversportscanner")) {
                final net.minecraft.client.option.ServerList serverList = new net.minecraft.client.option.ServerList(MinecraftClient.getInstance());
                serverList.loadFile();
                for (final PortResult portResult : this.ports.values()) {
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
                for (final PortResult portResult : this.ports.values()) {
                    portResult.renderTableEntry();
                }
                ImGui.endTable();
            }
        } else {
            ImGui.text("No valid Results.");
        }
        ImGui.end();
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
