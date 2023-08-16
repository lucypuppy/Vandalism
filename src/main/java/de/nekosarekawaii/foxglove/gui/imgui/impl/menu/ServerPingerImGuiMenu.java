package de.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import com.google.gson.JsonSyntaxException;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
import de.nekosarekawaii.foxglove.util.timer.impl.ms.MsTimer;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.responses.QueryPingResponse;
import net.minecraft.SharedConstants;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.net.BindException;
import java.net.UnknownHostException;

public class ServerPingerImGuiMenu extends ImGuiMenu {

    private final ImString hostname;
    private final ImInt port, queryPort, protocol, autoPingTime;
    private final MsTimer autoPingTimer;
    private ServerAddress serverAddress;
    private MCPingResponse mcPingResponse;
    private String motd;
    private QueryPingResponse queryPingResponse;
    private State currentState, queryState;
    private boolean autoPing, showPlayerList, showMods, showPlugins;

    public ServerPingerImGuiMenu() {
        super("Server Pinger");
        this.hostname = new ImString(253);
        this.port = this.queryPort = new ImInt(25565);
        this.protocol = new ImInt(SharedConstants.getProtocolVersion());
        this.autoPingTime = new ImInt(8000);
        this.autoPingTimer = new MsTimer();
        this.serverAddress = null;
        this.mcPingResponse = null;
        this.motd = "";
        this.queryPingResponse = null;
        this.currentState = this.queryState = State.WAITING_INPUT;
        this.autoPing = this.showPlayerList = this.showMods = this.showPlugins = false;
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
            if (ImGui.button("Clear##serverpinger")) {
                this.clear();
            }
            if (this.mcPingResponse != null) {
                if (this.mcPingResponse.players.sample.length > 0) {
                    if (ImGui.button("Player List: " + (this.showPlayerList ? "Disable" : "Enable") + "##serverpinger")) {
                        this.showPlayerList = !this.showPlayerList;
                    }
                }
                if ((this.mcPingResponse.modInfo != null && this.mcPingResponse.modInfo.modList.length > 0) || (this.mcPingResponse.forgeData != null && this.mcPingResponse.forgeData.mods.length > 0)) {
                    if (ImGui.button("Mods: " + (this.showMods ? "Disable" : "Enable") + "##serverpinger")) {
                        this.showMods = !this.showMods;
                    }
                }
            }
            if (this.queryPingResponse != null && this.queryPingResponse.plugins.sample.length > 0) {
                if (ImGui.button("Plugins: " + (this.showPlugins ? "Disable" : "Enable") + "##serverpinger")) {
                    this.showPlugins = !this.showPlugins;
                }
            }
            if (this.mcPingResponse != null) {
                if (ImGui.beginListBox("##serverpinger", 0, 500)) {
                    if (this.serverAddress != null) {
                        ImGui.text("[Server Address]");
                        final String serverAddress = this.serverAddress.getUnresolvedHost() + ":" + this.serverAddress.getUnresolvedPort() + " | " + this.serverAddress.getHost() + ":" + this.serverAddress.getPort();
                        ImGui.text(serverAddress);
                        ImGui.sameLine();
                        if (ImGui.button("Copy Server Address##serverpinger")) {
                            mc.keyboard.setClipboard(serverAddress);
                        }
                        ImGui.newLine();
                    }
                    ImGui.text("[Protocol]");
                    final MCPingResponse.Version version = this.mcPingResponse.version;
                    final String protocol = String.valueOf(version.protocol);
                    ImGui.text(protocol);
                    ImGui.sameLine();
                    if (ImGui.button("Copy Protocol##serverpinger")) {
                        mc.keyboard.setClipboard(protocol);
                    }
                    final String name = version.name;
                    if (!name.isEmpty()) {
                        ImGui.newLine();
                        ImGui.text("[Version Name]");
                        ImGui.textWrapped(name);
                        ImGui.sameLine();
                        if (ImGui.button("Copy Version Name##serverpinger")) {
                            mc.keyboard.setClipboard(name);
                        }
                    }
                    ImGui.newLine();
                    ImGui.text("[Online / Max Players]");
                    final String populationInfo = this.mcPingResponse.players.online + " / " + this.mcPingResponse.players.max;
                    ImGui.textWrapped(populationInfo);
                    ImGui.sameLine();
                    if (ImGui.button("Copy Population Info##serverpinger")) {
                        mc.keyboard.setClipboard(populationInfo);
                    }
                    if (!this.motd.isEmpty()) {
                        ImGui.newLine();
                        ImGui.text("[MOTD]");
                        ImGui.textWrapped(this.motd);
                        ImGui.sameLine();
                        if (ImGui.button("Copy MOTD##serverpinger")) {
                            mc.keyboard.setClipboard(this.motd);
                        }
                    }
                    ImGui.endListBox();
                }
            }
            ImGui.end();
        }
        if (this.showPlayerList && this.mcPingResponse != null && this.mcPingResponse.players.sample.length > 0) {
            if (ImGui.begin("Player List##serverpinger", ImGuiWindowFlags.NoCollapse)) {
                if (ImGui.beginListBox("##PlayerListserverpinger", 600, 500)) {
                    for (final MCPingResponse.Players.Player player : this.mcPingResponse.players.sample) {
                        final String playerText = player.name + " (" + player.id + ")";
                        ImGui.text(playerText);
                        ImGui.sameLine();
                        if (ImGui.button("Copy##" + playerText + "serverpinger")) {
                            mc.keyboard.setClipboard(playerText);
                        }
                    }
                    ImGui.endListBox();
                }
                ImGui.end();
            }
        }
        if (this.showMods && this.mcPingResponse != null && ((this.mcPingResponse.modInfo != null && this.mcPingResponse.modInfo.modList.length > 0) || (this.mcPingResponse.forgeData != null && this.mcPingResponse.forgeData.mods.length > 0))) {
            if (ImGui.begin("Mods##serverpinger", ImGuiWindowFlags.NoCollapse)) {
                if (ImGui.beginListBox("##Modsserverpinger", 600, 650)) {
                    if (this.mcPingResponse.modInfo != null) {
                        ImGui.text("[Mod Info Mods]");
                        for (final MCPingResponse.ModInfo.Mod mod : this.mcPingResponse.modInfo.modList) {
                            final String modText = mod.modid + " (" + mod.version + ")";
                            ImGui.text(modText);
                            ImGui.sameLine();
                            if (ImGui.button("Copy##" + modText + "serverpinger")) {
                                mc.keyboard.setClipboard(modText);
                            }
                        }
                        ImGui.newLine();
                    }
                    if (this.mcPingResponse.forgeData != null) {
                        ImGui.text("[Forge Data Mods]");
                        for (final MCPingResponse.ForgeData.Mod mod : this.mcPingResponse.forgeData.mods) {
                            final String modText = mod.modId + " (" + mod.modmarker + ")";
                            ImGui.text(mod.modId + " (" + mod.modmarker + ")");
                            ImGui.sameLine();
                            if (ImGui.button("Copy##" + modText + "serverpinger")) {
                                mc.keyboard.setClipboard(modText);
                            }
                        }
                    }
                    ImGui.endListBox();
                }
                ImGui.end();
            }
        }
        if (this.showPlugins && this.queryPingResponse != null && this.queryPingResponse.plugins.sample.length > 0) {
            if (ImGui.begin("Plugins##serverpinger", ImGuiWindowFlags.NoCollapse)) {
                if (ImGui.beginListBox("##Pluginsserverpinger", 350, 500)) {
                    for (final String plugin : this.queryPingResponse.plugins.sample) {
                        ImGui.text(plugin);
                        ImGui.sameLine();
                        if (ImGui.button("Copy##" + plugin + "serverpinger")) {
                            mc.keyboard.setClipboard(plugin);
                        }
                    }
                    ImGui.endListBox();
                }
                ImGui.end();
            }
        }
    }

    private void clear() {
        this.mcPingResponse = null;
        this.queryPingResponse = null;
        this.serverAddress = null;
        this.motd = "";
        this.currentState = this.queryState = State.WAITING_INPUT;
    }

    private void ping() {
        if (!this.hostname.isEmpty()) {
            this.clear();
            this.currentState = this.queryState = State.WAITING_RESPONSE;
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
                            Foxglove.getInstance().getLogger().error("Failed to ping " + this.hostname.get() + ":" + this.port.get(), t);
                        }
                    })
                    .finishHandler(response -> {
                        this.mcPingResponse = response;
                        final String descriptionString = response.description;
                        try {
                            final MutableText description = Text.Serializer.fromJson(descriptionString);
                            if (description != null) this.motd = description.getString();
                        } catch (final JsonSyntaxException ignored) {
                            this.motd = descriptionString;
                        }
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
                            Foxglove.getInstance().getLogger().error("Failed to ping query " + this.hostname.get() + ":" + this.queryPort.get(), t);
                        }
                    })
                    .finishHandler(response -> {
                        this.queryPingResponse = response;
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
