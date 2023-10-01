package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.widget.ServerInfoWidget;
import de.vandalismdevelopment.vandalism.util.timer.impl.ms.MsTimer;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.minecraft.SharedConstants;

import java.net.BindException;
import java.net.UnknownHostException;

public class ServerPingerImGuiMenu extends ImGuiMenu {

    private final ImString hostname;
    private final ImInt port, queryPort, protocol, autoPingTime;
    private final MsTimer autoPingTimer;
    private State currentState, queryState;
    private boolean autoPing;
    private final ServerInfoWidget serverInfoWidget;

    public ServerPingerImGuiMenu() {
        super("Server Pinger");
        this.hostname = new ImString(253);
        this.port = new ImInt(25565);
        this.queryPort = new ImInt(25565);
        this.protocol = new ImInt(SharedConstants.getProtocolVersion());
        this.autoPingTime = new ImInt(8000);
        this.autoPingTimer = new MsTimer();
        this.currentState = State.WAITING_INPUT;
        this.queryState = State.WAITING_INPUT;
        this.autoPing = false;
        this.serverInfoWidget = new ServerInfoWidget();
    }

    @Override
    public void render() {
        if (ImGui.begin("Server Pinger", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("State: " + this.currentState.getMessage());
            ImGui.text("Query State: " + this.queryState.getMessage());
            ImGui.inputText("Hostname##serverpinger", this.hostname);
            ImGui.sameLine();
            if (ImGui.button("Clear##Hostnameserverpinger")) {
                this.hostname.clear();
            }
            if (ImGui.inputInt("Port##serverpinger", this.port, 1)) {
                this.port.set(Math.max(1, Math.min(this.port.get(), 65535)));
            }
            ImGui.sameLine();
            if (ImGui.button("Reset##Portserverpinger")) {
                this.port.set(25565);
            }
            if (ImGui.inputInt("Query Port##serverpinger", this.queryPort, 1)) {
                this.queryPort.set(Math.max(1, Math.min(this.queryPort.get(), 65535)));
            }
            ImGui.sameLine();
            if (ImGui.button("Reset##QueryPortserverpinger")) {
                this.queryPort.set(25565);
            }
            ImGui.inputInt("Protocol##serverpinger", this.protocol, 1);
            ImGui.sameLine();
            if (ImGui.button("Reset##Protocolserverpinger")) {
                this.protocol.set(SharedConstants.getProtocolVersion());
            }
            if (ImGui.inputInt("Auto Ping Time##serverpinger", this.autoPingTime, 1)) {
                this.autoPingTime.set(Math.max(1000, Math.min(this.autoPingTime.get(), 60000)));
            }
            if (this.autoPing && !this.hostname.isEmpty()) {
                if (this.currentState != State.WAITING_RESPONSE) {
                    ImGui.text("Pinging in " + (this.autoPingTime.get() - this.autoPingTimer.getElapsedTime()) + "ms");
                    if (this.autoPingTimer.hasReached(this.autoPingTime.get(), true)) {
                        this.ping();
                    }
                } else ImGui.text("Pinging...");
            }
            if (!this.hostname.isEmpty() && this.currentState != State.WAITING_RESPONSE) {
                if (ImGui.button("Auto Ping: " + (this.autoPing ? "Disable" : "Enable") + "##serverpinger")) {
                    this.autoPing = !this.autoPing;
                }
                if (!this.autoPing) {
                    ImGui.sameLine();
                    if (ImGui.button("Ping##serverpinger")) {
                        this.ping();
                    }
                }
            }
            if (this.serverInfoWidget.getMcPingResponse() != null) {
                ImGui.sameLine();
                if (ImGui.button("Clear##serverpinger")) {
                    this.clear();
                }
            }
            this.serverInfoWidget.render();
            ImGui.end();
        }
    }

    private void clear() {
        this.serverInfoWidget.setMcPingResponse(null);
        this.serverInfoWidget.setQueryPingResponse(null);
        this.currentState = State.WAITING_INPUT;
        this.queryState = State.WAITING_INPUT;
    }

    private void ping() {
        if (!this.hostname.isEmpty()) {
            this.clear();
            this.currentState = State.WAITING_RESPONSE;
            this.queryState = State.WAITING_RESPONSE;
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
                            this.queryState = State.BIND_FAILED;
                        } else if (t instanceof UnknownHostException) {
                            this.queryState = State.UNKNOWN_HOST;
                        } else if (t instanceof ConnectionRefusedException) {
                            this.queryState = State.CONNECTION_REFUSED;
                        } else if (t instanceof ConnectTimeoutException) {
                            this.queryState = State.CONNECTION_TIMED_OUT;
                        } else if (t instanceof DataReadException) {
                            this.queryState = State.DATA_READ_FAILED;
                        } else if (t instanceof PacketReadException) {
                            this.queryState = State.PACKET_READ_FAILED;
                        } else {
                            this.queryState = State.FAILED;
                            Vandalism.getInstance().getLogger().error("Failed to ping query " + this.hostname.get() + ":" + this.queryPort.get(), t);
                        }
                    })
                    .finishHandler(response -> {
                        this.serverInfoWidget.setQueryPingResponse(null);
                        this.queryState = State.SUCCESS;
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
