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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.field.IPFieldWidget;
import de.nekosarekawaii.vandalism.util.DateUtil;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import lombok.Getter;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Pair;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// TODO: Add alternative mode (like this website https://www.ipfingerprints.com/portscan.php)
// TODO: Add tabs like the tabs from the server discovery
// TODO: Add port scan randomization to bypass some firewalls
public class PortScannerClientWindow extends StateClientWindow implements DataListWidget, IPFieldWidget {

    private final ImString ip = this.createImIP();

    private final ImString progress = new ImString(200);
    private final ImInt minPort = new ImInt(1);
    private final ImInt maxPort = new ImInt(65535);
    private final ImInt threads = new ImInt(128);
    private final ImInt timeout = new ImInt(500);
    private final ImString portsContainer = new ImString(500);
    private final List<Integer> ports = new CopyOnWriteArrayList<>();
    private final List<PortResult> portResults = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ListDataEntry> portScanDataEntries = new CopyOnWriteArrayList<>();
    private int currentPort = -1;
    private int currentPortResult = -1;

    public PortScannerClientWindow() {
        super("Port Scanner", Category.SERVER, 700f, 600f);
    }

    private void reset() {
        this.progress.clear();
        this.ports.clear();
        this.portResults.clear();
        this.portScanDataEntries.clear();
        this.currentPort = this.minPort.get() - 1;
        this.currentPortResult = -1;
        this.resetState();
    }

    private boolean isRunning() {
        return this.getState().equals(State.RUNNING.getMessage());
    }

    private void stop() {
        this.resetState();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        for (final PortResult portResult : this.portResults) portResult.render();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        super.onRender(context, mouseX, mouseY, delta);
        if (this.isRunning()) {
            ImGui.text("Progress");
            ImGui.progressBar(((float) this.currentPort / (float) (this.maxPort.get() - this.minPort.get())) / 100f);
            ImGui.separator();
        } else {
            this.renderField(id);
        }
        if (this.isValidIP()) {
            if (!this.isRunning()) {
                ImGui.text("Min Port");
                ImGui.sameLine(ImGui.getColumnWidth() / 2f + 27f);
                ImGui.text("Max Port");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() / 2f);
                if (ImGui.inputInt(id + "minPort", this.minPort, 1)) {
                    this.minPort.set(Math.max(1, Math.min(this.minPort.get(), this.maxPort.get() - 1)));
                }
                ImGui.setNextItemWidth(-1);
                ImGui.sameLine();
                if (ImGui.inputInt(id + "maxPort", this.maxPort, 1)) {
                    this.maxPort.set(Math.max(this.minPort.get() + 1, Math.min(this.maxPort.get(), 65535)));
                }
                ImGui.text("Threads");
                ImGui.sameLine(ImGui.getColumnWidth() / 2f + 27f);
                ImGui.text("Timeout");
                ImGui.setNextItemWidth(ImGui.getColumnWidth() / 2f);
                if (ImGui.inputInt(id + "threads", this.threads, 1)) {
                    this.threads.set(Math.max(1, Math.min(this.threads.get(), 1000)));
                }
                ImGui.setNextItemWidth(-1);
                ImGui.sameLine();
                if (ImGui.inputInt(id + "timeout", this.timeout, 1)) {
                    this.timeout.set(Math.max(1, Math.min(this.timeout.get(), 10000)));
                }
                boolean resetVisible = this.minPort.get() != 1 || this.maxPort.get() != 65535 || this.threads.get() != 128 || this.timeout.get() != 500;
                if (resetVisible) {
                    if (ImGui.button("Reset Values" + id + "resetValues", ImGui.getColumnWidth() / (this.ports.isEmpty() ? 2f : 3f), ImGui.getTextLineHeightWithSpacing())) {
                        this.minPort.set(1);
                        this.maxPort.set(65535);
                        this.threads.set(128);
                        this.timeout.set(500);
                    }
                }
                boolean clearVisible = !this.ports.isEmpty();
                if (clearVisible) {
                    if (resetVisible) {
                        ImGui.sameLine();
                    }
                    if (ImGui.button("Clear" + id + "clear", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                        this.reset();
                    }
                }
                if (resetVisible || clearVisible) {
                    ImGui.sameLine();
                }
                if (ImGui.button("Start" + id + "start", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    this.reset();
                    this.setState(State.RUNNING.getMessage());
                    for (int i = 0; i < this.threads.get(); i++) {
                        final int threadID = i + 1;
                        Vandalism.getInstance().getLogger().info("Starting Port Scanner Thread #{}...", threadID);
                        new Thread(() -> {
                            try {
                                while (this.isRunning() && this.currentPort <= this.maxPort.get()) {
                                    this.currentPort++;
                                    final int port = this.currentPort;
                                    try {
                                        final Socket socket = new Socket();
                                        socket.connect(new InetSocketAddress(this.getImIP().get(), port), this.timeout.get());
                                        socket.close();
                                        synchronized (this.ports) {
                                            if (!this.ports.contains(port)) {
                                                this.ports.add(port);
                                            }
                                        }
                                    } catch (final Exception ignored) {
                                    }
                                }
                                this.stop();
                                Vandalism.getInstance().getLogger().info("Port Scanner Thread #{} finished.", threadID);
                            } catch (final Exception e) {
                                Vandalism.getInstance().getLogger().error("Port Scanner Thread #{} failed.", threadID, e);
                                this.setState(State.FAILED.getMessage() + e.getClass().getSimpleName() + ": " + e.getMessage());
                            }
                        }, "Port Scanner Thread #" + threadID).start();
                    }
                }
                ImGui.spacing();
            }
        }
        if (this.isRunning()) {
            if (ImUtils.subButton("Stop" + id + "stop")) {
                this.stop();
            }
        }
        if (!this.ports.isEmpty()) {
            if (this.currentPortResult > -1) {
                ImGui.separator();
                ImGui.text("Pinging Progress");
                ImGui.progressBar((float) this.currentPortResult / (float) this.ports.size());
                ImGui.separator();
            }
            ImGui.text("Open Ports: " + this.ports.size());
            this.portsContainer.set(String.join(" ", this.ports.stream().map(port -> port + " ").toList()));
            String portsContainer = this.portsContainer.get();
            if (portsContainer.endsWith(" ")) {
                this.portsContainer.set(portsContainer.substring(0, portsContainer.length() - 1));
                portsContainer = this.portsContainer.get();
            }
            ImGui.beginChild(id + "openPorts" + "Child", -1, ImGui.getTextLineHeight() + ImGui.getTextLineHeight() * this.ports.size() / 10f, true);
            ImGui.textWrapped(portsContainer);
            ImGui.endChild();
            if (ImGui.button("Copy All Ports" + id + "copyAllPorts", ImGui.getColumnWidth() / (!this.portResults.isEmpty() ? 3 : 2), ImGui.getTextLineHeightWithSpacing())) {
                mc.keyboard.setClipboard(portsContainer);
            }
            if (this.currentPortResult < 0) {
                ImGui.sameLine();
                if (ImGui.button("Ping All Ports" + id + "pingAllPorts", ImGui.getColumnWidth() / (!this.portResults.isEmpty() ? 2 : 1), ImGui.getTextLineHeightWithSpacing())) {
                    this.portResults.clear();
                    this.portScanDataEntries.clear();
                    this.currentPortResult = 0;
                    new Thread(() -> {
                        for (final int port : this.ports) {
                            try {
                                final PortResult portResult = new PortResult(port, this.getImIP().get());
                                portResult.ping(() -> {
                                    final MCPingResponse pingResponse = portResult.getMcPingResponse();
                                    if (pingResponse != null) {
                                        final List<Pair<String, String>> list = portResult.getList();
                                        list.add(new Pair<>("Description", ServerUtil.fixDescription(portResult.getDescription())));
                                        list.add(new Pair<>("Version", pingResponse.version.name));
                                        list.add(new Pair<>("Protocol", String.valueOf(pingResponse.version.protocol)));
                                        list.add(new Pair<>("Players", pingResponse.players.online + "/" + pingResponse.players.max));
                                    }
                                    this.portScanDataEntries.add(portResult);
                                    this.portResults.add(portResult);
                                    this.currentPortResult++;
                                });
                            } catch (final Exception e) {
                                Vandalism.getInstance().getLogger().error("Failed to ping port {}.", port, e);
                            }
                        }
                        this.currentPortResult = -1;
                    }, "Port Minecraft Checker Thread").start();
                }
            }
            if (!this.portResults.isEmpty()) {
                if (this.currentPortResult < 0) {
                    ImGui.sameLine();
                    if (ImUtils.subButton("Add All Servers" + id + "addAllServers")) {
                        final net.minecraft.client.option.ServerList serverList = new net.minecraft.client.option.ServerList(MinecraftClient.getInstance());
                        serverList.loadFile();
                        for (final PortResult portResult : this.portResults) {
                            serverList.add(new ServerInfo(
                                    "Port Scan Result (" + portResult.getPort() + ")",
                                    portResult.getAddress() + ":" + portResult.getPort(),
                                    ServerInfo.ServerType.OTHER
                            ), false);
                        }
                        serverList.saveFile();
                    }
                }
                if (DateUtil.isAprilFools()) {
                    ImGui.text("Excel Simulator " + DateUtil.getYear());
                }
                ImGui.text("Server: " + this.portResults.size());
                this.renderDataList(id, -1, 90f, this.portScanDataEntries);
            }
        } else {
            ImGui.text("No valid results.");
        }
    }

    @Override
    public ImString getImIP() {
        return this.ip;
    }

    @Override
    public void onDataSplit(final String[] data, final boolean resolved) {
        IPFieldWidget.super.onDataSplit(data, resolved);
        if (data.length > 1) {
            try {
                final int port = Integer.parseInt(data[1]);
                this.minPort.set(Math.max(1, Math.min(port - 500, 65535)));
                this.maxPort.set(Math.max(port, Math.min(port + 500, 65535)));
            } catch (final Exception ignored) {
            }
        }
    }

    @Override
    public boolean filterDataEntry(final DataEntry dataEntry) {
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
        float[] color = {0.1f, 0.8f, 0.1f, 0.30f};
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            if (ServerUtil.lastServerExists() && ServerUtil.getLastServerInfo().address.equals(listDataEntry.getFirst().getRight())) {
                color = new float[]{0.8f, 0.1f, 0.1f, 0.30f};
            }
        }
        return color;
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
            for (final PortResult portResult : this.portResults) {
                final String addressPort = portResult.getAddress() + ":" + portResult.getPort();
                if (addressPort.equals(address)) {
                    portResult.renderContextMenu(id + "contextMenuContent");
                    break;
                }
            }
        }
    }

    @Getter
    private enum State {

        RUNNING("Running..."),
        FAILED("Failed: ");

        private final String message;

        State(final String message) {
            this.message = message;
        }

    }

}
