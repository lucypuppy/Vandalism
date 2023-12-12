package de.vandalismdevelopment.vandalism.gui_v2.impl;

import de.florianmichael.rclasses.math.integration.MSTimer;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui_v2.widget.ServerInfoWidget;
import de.vandalismdevelopment.vandalism.gui_v2.widget.ServerInfosTableColumn;
import de.vandalismdevelopment.vandalism.gui_v2.ImWindow;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;

import java.net.BindException;
import java.net.UnknownHostException;

public class ServerPingerImGuiMenu extends ImWindow {

    private static final ImGuiInputTextCallback HOSTNAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '.' &&
                            imGuiInputTextCallbackData.getEventChar() != '-'
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

    public ServerPingerImGuiMenu() {
        super("Server Pinger");
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
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.serverInfoWidget.renderSubData();
        if (ImGui.begin(
                "Server Pinger##serverpinger",
                ImGuiWindowFlags.NoCollapse
        )) {
            ImGui.text("State: " + this.currentState.getMessage());
            ImGui.text("Query State: " + this.currentQueryState.getMessage());
            ImGui.inputText(
                    "Hostname##serverpingerhostname",
                    this.hostname,
                    ImGuiInputTextFlags.CallbackCharFilter,
                    HOSTNAME_FILTER
            );
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
                    if (ImGui.button("Auto Ping: " + (this.autoPing ? "Disable" : "Enable") + "##autopingerverpinger")) {
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
            ImGui.end();
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
            this.serverInfoWidget.setHostname(this.hostname.get());
            this.currentState = State.WAITING_RESPONSE;
            this.currentQueryState = State.WAITING_RESPONSE;
            MCPing.pingModern(this.protocol.get())
                    .address(this.hostname.get(), this.port.get())
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        if (t instanceof UnknownHostException) {
                            this.currentState = State.UNKNOWN_HOST;
                        } else if (t instanceof ConnectionRefusedException) {
                            this.currentState = State.CONNECTION_REFUSED;
                        } else if (t instanceof ConnectTimeoutException) {
                            this.currentState = State.CONNECTION_TIMED_OUT;
                        } else if (t instanceof DataReadException) {
                            this.currentState = State.DATA_READ_FAILED;
                        } else if (t instanceof PacketReadException) {
                            this.currentState = State.PACKET_READ_FAILED;
                        } else {
                            this.currentState = State.FAILED;
                            Vandalism.getInstance().getLogger().error("Failed to ping " + this.hostname.get() + ":" + this.port.get(), t);
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
                        if (t instanceof BindException) {
                            this.currentQueryState = State.BIND_FAILED;
                        } else if (t instanceof UnknownHostException) {
                            this.currentQueryState = State.UNKNOWN_HOST;
                        } else if (t instanceof ConnectionRefusedException) {
                            this.currentQueryState = State.CONNECTION_REFUSED;
                        } else if (t instanceof ConnectTimeoutException) {
                            this.currentQueryState = State.CONNECTION_TIMED_OUT;
                        } else if (t instanceof DataReadException) {
                            this.currentQueryState = State.DATA_READ_FAILED;
                        } else if (t instanceof PacketReadException) {
                            this.currentQueryState = State.PACKET_READ_FAILED;
                        } else {
                            this.currentQueryState = State.FAILED;
                            Vandalism.getInstance().getLogger().error("Failed to ping query " + this.hostname.get() + ":" + this.queryPort.get(), t);
                        }
                    })
                    .finishHandler(response -> {
                        this.serverInfoWidget.setQueryPingResponse(response);
                        this.currentQueryState = State.SUCCESS;
                    })
                    .getAsync();
        } else this.currentState = State.WAITING_INPUT;
    }

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

        public String getMessage() {
            return this.message;
        }

    }

}
