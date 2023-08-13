package me.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import com.google.gson.JsonSyntaxException;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import me.nekosarekawaii.foxglove.util.timer.impl.ms.MsTimer;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.responses.QueryPingResponse;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.net.BindException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ServerPingerMenu {

    private final static ImString hostname = new ImString(253);
    private final static ImInt port = new ImInt(25565), queryPort = new ImInt(25565), protocol = new ImInt(SharedConstants.getProtocolVersion()), autoPingTime = new ImInt(8000);
    private final static MsTimer autoPingTimer = new MsTimer();
    private final static List<String> versionName = new ArrayList<>(), motd = new ArrayList<>();
    private static MCPingResponse mcPingResponse = null;
    private static QueryPingResponse queryPingResponse = null;
    private static State currentState = State.WAITING_INPUT, queryState = State.WAITING_INPUT;
    private static boolean autoPing = false, showPlayerList = false, showMods = false, showPlugins = false;

    public static void render() {
        if (ImGui.begin("Server Pinger", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("State: " + currentState.getMessage());
            ImGui.text("Query State: " + queryState.getMessage());
            ImGui.inputText("Hostname##serverpinger", hostname);
            ImGui.sameLine();
            if (ImGui.button("Clear##Hostnameserverpinger")) {
                hostname.clear();
            }
            if (ImGui.inputInt("Port", port, 1)) {
                port.set(Math.max(1, Math.min(port.get(), 65535)));
            }
            ImGui.sameLine();
            if (ImGui.button("Reset##Portserverpinger")) {
                port.set(25565);
            }
            if (ImGui.inputInt("Query Port##serverpinger", queryPort, 1)) {
                queryPort.set(Math.max(1, Math.min(queryPort.get(), 65535)));
            }
            ImGui.sameLine();
            if (ImGui.button("Reset##QueryPortserverpinger")) {
                queryPort.set(25565);
            }
            ImGui.inputInt("Protocol##serverpinger", protocol, 1);
            ImGui.sameLine();
            if (ImGui.button("Reset##Protocolserverpinger")) {
                protocol.set(SharedConstants.getProtocolVersion());
            }
            if (ImGui.inputInt("Auto Ping Time##serverpinger", autoPingTime, 1)) {
                autoPingTime.set(Math.max(1000, Math.min(autoPingTime.get(), 60000)));
            }
            if (autoPing && !hostname.isEmpty()) {
                if (currentState != State.WAITING_RESPONSE) {
                    ImGui.text("Pinging in " + (autoPingTime.get() - autoPingTimer.getElapsedTime()) + "ms");
                    if (autoPingTimer.hasReached(autoPingTime.get(), true)) {
                        ping();
                    }
                } else ImGui.text("Pinging...");
            }
            if (!hostname.isEmpty() && currentState != State.WAITING_RESPONSE) {
                if (ImGui.button("Auto Ping: " + (autoPing ? "Disable" : "Enable") + "##serverpinger")) {
                    autoPing = !autoPing;
                }
                if (!autoPing) {
                    ImGui.sameLine();
                    if (ImGui.button("Ping##serverpinger")) {
                        ping();
                    }
                }
            }
            if (mcPingResponse != null) {
                if (ImGui.button("Clear##ServerInfoserverpinger")) {
                    clear();
                }
            }
            if (mcPingResponse != null) {
                if (mcPingResponse.players.sample.length > 0) {
                    if (ImGui.button("Player List: " + (showPlayerList ? "Disable" : "Enable") + "##serverpinger")) {
                        showPlayerList = !showPlayerList;
                    }
                }
                if ((mcPingResponse.modInfo != null && mcPingResponse.modInfo.modList.length > 0) || (mcPingResponse.forgeData != null && mcPingResponse.forgeData.mods.length > 0)) {
                    if (ImGui.button("Mods: " + (showMods ? "Disable" : "Enable") + "##serverpinger")) {
                        showMods = !showMods;
                    }
                }
                if (queryPingResponse != null && queryPingResponse.plugins.sample.length > 0) {
                    if (ImGui.button("Plugins: " + (showPlugins ? "Disable" : "Enable") + "##serverpinger")) {
                        showPlugins = !showPlugins;
                    }
                }
                if (ImGui.beginListBox("##ServerInfoserverpinger", 0, 500)) {
                    ImGui.text("[Server Address]");
                    ImGui.text(mcPingResponse.server.ip + ":" + mcPingResponse.server.port);
                    ImGui.newLine();
                    ImGui.text("[Protocol]");
                    ImGui.text(String.valueOf(mcPingResponse.version.protocol));
                    if (!versionName.isEmpty()) {
                        ImGui.newLine();
                        ImGui.text("[Version Name]");
                        for (final String v : versionName) {
                            ImGui.text(v);
                        }
                    }
                    ImGui.newLine();
                    ImGui.text("[Max / Online Players]");
                    ImGui.text(mcPingResponse.players.max + " / " + mcPingResponse.players.online);
                    if (!motd.isEmpty()) {
                        ImGui.newLine();
                        ImGui.text("[MOTD]");
                        for (final String v : motd) {
                            ImGui.text(v);
                        }
                    }
                    ImGui.endListBox();
                }
            }
            ImGui.end();
        }
        if (showPlayerList && mcPingResponse != null && mcPingResponse.players.sample.length > 0) {
            if (ImGui.begin("Player List##serverpinger", ImGuiWindowFlags.NoCollapse)) {
                if (ImGui.beginListBox("##PlayerListserverpinger", 600, 500)) {
                    for (final MCPingResponse.Players.Player player : mcPingResponse.players.sample) {
                        final String playerText = player.name + " (" + player.id + ")";
                        ImGui.text(playerText);
                        ImGui.sameLine();
                        if (ImGui.button("Copy##" + playerText + "serverpinger"))
                            MinecraftClient.getInstance().keyboard.setClipboard(playerText);
                    }
                    ImGui.endListBox();
                }
                ImGui.end();
            }
        }
        if (showMods && mcPingResponse != null && ((mcPingResponse.modInfo != null && mcPingResponse.modInfo.modList.length > 0) || (mcPingResponse.forgeData != null && mcPingResponse.forgeData.mods.length > 0))) {
            if (ImGui.begin("Mods##serverpinger", ImGuiWindowFlags.NoCollapse)) {
                if (ImGui.beginListBox("##Modsserverpinger", 600, 650)) {
                    if (mcPingResponse.modInfo != null) {
                        ImGui.text("[Mod Info Mods]");
                        for (final MCPingResponse.ModInfo.Mod mod : mcPingResponse.modInfo.modList) {
                            final String modText = mod.modid + " (" + mod.version + ")";
                            ImGui.text(modText);
                            ImGui.sameLine();
                            if (ImGui.button("Copy##" + modText + "serverpinger"))
                                MinecraftClient.getInstance().keyboard.setClipboard(modText);
                        }
                        ImGui.newLine();
                    }
                    if (mcPingResponse.forgeData != null) {
                        ImGui.text("[Forge Data Mods]");
                        for (final MCPingResponse.ForgeData.Mod mod : mcPingResponse.forgeData.mods) {
                            final String modText = mod.modId + " (" + mod.modmarker + ")";
                            ImGui.text(mod.modId + " (" + mod.modmarker + ")");
                            ImGui.sameLine();
                            if (ImGui.button("Copy##" + modText + "serverpinger"))
                                MinecraftClient.getInstance().keyboard.setClipboard(modText);
                        }
                    }
                    ImGui.endListBox();
                }
                ImGui.end();
            }
        }
        if (showPlugins && queryPingResponse != null && queryPingResponse.plugins.sample.length > 0) {
            if (ImGui.begin("Plugins##serverpinger", ImGuiWindowFlags.NoCollapse)) {
                if (ImGui.beginListBox("##Pluginsserverpinger", 350, 500)) {
                    for (final String plugin : queryPingResponse.plugins.sample) {
                        ImGui.text(plugin);
                        ImGui.sameLine();
                        if (ImGui.button("Copy##" + plugin + "serverpinger"))
                            MinecraftClient.getInstance().keyboard.setClipboard(plugin);
                    }
                    ImGui.endListBox();
                }
                ImGui.end();
            }
        }
    }

    private static void clear() {
        mcPingResponse = null;
        queryPingResponse = null;
        versionName.clear();
        motd.clear();
        currentState = State.WAITING_INPUT;
        queryState = State.WAITING_INPUT;
    }

    private static void ping() {
        if (!hostname.isEmpty()) {
            clear();
            currentState = State.WAITING_RESPONSE;
            queryState = State.WAITING_RESPONSE;
            MCPing.pingModern(protocol.get())
                    .address(hostname.get(), port.get())
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        if (t instanceof UnknownHostException) {
                            currentState = State.UNKNOWN_HOST;
                        } else if (t instanceof ConnectionRefusedException) {
                            currentState = State.CONNECTION_REFUSED;
                        } else if (t instanceof ConnectTimeoutException) {
                            currentState = State.CONNECTION_TIMED_OUT;
                        } else if (t instanceof DataReadException) {
                            currentState = State.DATA_READ_FAILED;
                        } else if (t instanceof PacketReadException) {
                            currentState = State.PACKET_READ_FAILED;
                        } else {
                            currentState = State.FAILED;
                            t.printStackTrace();
                        }
                    })
                    .finishHandler(response -> {
                        mcPingResponse = response;
                        final List<StringVisitable> versionNameLines = MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(mcPingResponse.version.name, 310, Style.EMPTY);
                        for (final StringVisitable versionNameLine : versionNameLines) {
                            versionName.add(versionNameLine.getString());
                        }
                        final String descriptionString = response.description;
                        try {
                            final MutableText description = Text.Serializer.fromJson(descriptionString);
                            if (description != null) {
                                final List<StringVisitable> descriptionLines = MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(description, 340, Style.EMPTY);
                                for (final StringVisitable descriptionLine : descriptionLines) {
                                    motd.add(descriptionLine.getString());
                                }
                            }
                        } catch (final JsonSyntaxException ignored) {
                            motd.add(descriptionString);
                        }
                        currentState = State.SUCCESS;
                    })
                    .getAsync();
            MCPing.pingQuery()
                    .address(hostname.get(), queryPort.get())
                    .timeout(5000, 5000)
                    .exceptionHandler(t -> {
                        if (t instanceof BindException) {
                            queryState = State.BIND_FAILED;
                        } else if (t instanceof UnknownHostException) {
                            queryState = State.UNKNOWN_HOST;
                        } else if (t instanceof ConnectionRefusedException) {
                            queryState = State.CONNECTION_REFUSED;
                        } else if (t instanceof ConnectTimeoutException) {
                            queryState = State.CONNECTION_TIMED_OUT;
                        } else if (t instanceof DataReadException) {
                            queryState = State.DATA_READ_FAILED;
                        } else if (t instanceof PacketReadException) {
                            queryState = State.PACKET_READ_FAILED;
                        } else {
                            queryState = State.FAILED;
                            t.printStackTrace();
                        }
                    })
                    .finishHandler(response -> {
                        queryPingResponse = response;
                        queryState = State.SUCCESS;
                    })
                    .getAsync();
        } else currentState = State.WAITING_INPUT;
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
