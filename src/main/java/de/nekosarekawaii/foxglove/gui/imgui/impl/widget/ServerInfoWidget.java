package de.nekosarekawaii.foxglove.gui.imgui.impl.widget;

import com.google.gson.JsonSyntaxException;
import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.responses.QueryPingResponse;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ServerInfoWidget implements MinecraftWrapper {

    private MCPingResponse mcPingResponse;

    private QueryPingResponse queryPingResponse;

    private boolean showMods, showPlayerList, showPlugins;

    private String motd;

    public ServerInfoWidget() {
        this.mcPingResponse = null;
        this.showMods = false;
        this.showPlayerList = false;
        this.showPlugins = false;
        this.motd = "";
        this.queryPingResponse = null;
    }

    public void setMcPingResponse(final MCPingResponse mcPingResponse) {
        this.mcPingResponse = mcPingResponse;
        if (mcPingResponse != null) {
            final String descriptionString = mcPingResponse.description;
            try {
                final MutableText description = Text.Serializer.fromJson(descriptionString);
                if (description != null) this.motd = description.getString();
            } catch (final JsonSyntaxException ignored) {
                this.motd = descriptionString;
            }
        }
    }

    public MCPingResponse getMcPingResponse() {
        return this.mcPingResponse;
    }

    public void setQueryPingResponse(final QueryPingResponse queryPingResponse) {
        this.queryPingResponse = queryPingResponse;
    }

    public QueryPingResponse getQueryPingResponse() {
        return this.queryPingResponse;
    }

    public void render() {
        if (this.mcPingResponse == null) return;
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
        if (this.queryPingResponse != null && this.queryPingResponse.plugins.sample.length > 0) {
            if (ImGui.button("Plugins: " + (this.showPlugins ? "Disable" : "Enable") + "##serverpinger")) {
                this.showPlugins = !this.showPlugins;
            }
        }
        if (ImGui.beginListBox("##serverpinger", 0, 500)) {
            if (this.mcPingResponse.server != null) {
                ImGui.text("[Server Address]");
                final String serverAddress = this.mcPingResponse.server.ip + ":" + this.mcPingResponse.server.port;
                ImGui.text(serverAddress);
                ImGui.sameLine();
                if (ImGui.button("Copy Server Address##serverpinger")) {
                    keyboard().setClipboard(serverAddress);
                }
                ImGui.newLine();
            }
            ImGui.text("[Protocol]");
            final MCPingResponse.Version version = this.mcPingResponse.version;
            final String protocol = String.valueOf(version.protocol);
            ImGui.text(protocol);
            ImGui.sameLine();
            if (ImGui.button("Copy Protocol##serverpinger")) {
                keyboard().setClipboard(protocol);
            }
            final String name = version.name;
            if (!name.isEmpty()) {
                ImGui.newLine();
                ImGui.text("[Version Name]");
                ImGui.textWrapped(name);
                ImGui.sameLine();
                if (ImGui.button("Copy Version Name##serverpinger")) {
                    keyboard().setClipboard(name);
                }
            }
            ImGui.newLine();
            ImGui.text("[Online / Max Players]");
            final String populationInfo = this.mcPingResponse.players.online + " / " + this.mcPingResponse.players.max;
            ImGui.textWrapped(populationInfo);
            ImGui.sameLine();
            if (ImGui.button("Copy Population Info##serverpinger")) {
                keyboard().setClipboard(populationInfo);
            }
            if (!this.motd.isEmpty()) {
                ImGui.newLine();
                ImGui.text("[MOTD]");
                ImGui.textWrapped(this.motd);
                ImGui.sameLine();
                if (ImGui.button("Copy MOTD##serverpinger")) {
                    keyboard().setClipboard(this.motd);
                }
            }
            ImGui.endListBox();
        }
        if (this.showPlayerList && this.mcPingResponse != null && this.mcPingResponse.players.sample.length > 0) {
            if (ImGui.begin("Player List##serverpinger", ImGuiWindowFlags.NoCollapse)) {
                if (ImGui.beginListBox("##PlayerListserverpinger", 600, 500)) {
                    for (final MCPingResponse.Players.Player player : this.mcPingResponse.players.sample) {
                        final String playerText = player.name + " (" + player.id + ")";
                        ImGui.text(playerText);
                        ImGui.sameLine();
                        if (ImGui.button("Copy##" + playerText + "serverpinger")) {
                            keyboard().setClipboard(playerText);
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
                                keyboard().setClipboard(modText);
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
                                keyboard().setClipboard(modText);
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
                            keyboard().setClipboard(plugin);
                        }
                    }
                    ImGui.endListBox();
                }
                ImGui.end();
            }
        }
    }

}


