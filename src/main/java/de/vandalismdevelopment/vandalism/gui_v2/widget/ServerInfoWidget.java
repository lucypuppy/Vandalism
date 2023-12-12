package de.vandalismdevelopment.vandalism.gui_v2.widget;

import com.google.gson.JsonSyntaxException;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import imgui.ImGui;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.responses.QueryPingResponse;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ServerInfoWidget implements MinecraftWrapper {

    private MCPingResponse mcPingResponse;

    private QueryPingResponse queryPingResponse;

    private boolean showMods, showPlayerList, showPlugins;

    private String hostname, motd;

    public ServerInfoWidget() {
        this.mcPingResponse = null;
        this.showMods = false;
        this.showPlayerList = false;
        this.showPlugins = false;
        this.motd = "";
        this.queryPingResponse = null;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
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

    public void renderTableEntry(final boolean isSingle) {
        if (this.mcPingResponse == null || this.mcPingResponse.server == null) return;
        final String uniqueId = "##" + (isSingle ? "single" : "multi") + this.mcPingResponse.server.ip + ":" + this.mcPingResponse.server.port + "serverinfo";
        final ServerInfosTableColumn[] serverInfosTableColumns = ServerInfosTableColumn.values();
        ImGui.tableNextRow();
        for (int i = 0; i < serverInfosTableColumns.length; i++) {
            ImGui.tableSetColumnIndex(i);
            final ServerInfosTableColumn serverInfosTableColumn = serverInfosTableColumns[i];
            switch (serverInfosTableColumn) {
                case SERVER_ADDRESS -> {
                    ImGui.textWrapped(this.hostname);
                }
                case PORT -> {
                    ImGui.textWrapped(String.valueOf(this.mcPingResponse.server.port));
                }
                case RESOLVED_SERVER_ADDRESS -> {
                    String resolvedServerAddress = this.mcPingResponse.server.ip;
                    if (resolvedServerAddress.endsWith(".")) {
                        resolvedServerAddress = resolvedServerAddress.substring(0, resolvedServerAddress.length() - 1);
                    }
                    ImGui.textWrapped(resolvedServerAddress);
                }
                case PROTOCOL -> {
                    if (this.mcPingResponse.version != null) {
                        ImGui.textWrapped(String.valueOf(this.mcPingResponse.version.protocol));
                    }
                }
                case VERSION -> {
                    if (this.mcPingResponse.version != null) {
                        ImGui.textWrapped(this.mcPingResponse.version.name);
                    }
                }
                case ONLINE_PLAYERS -> {
                    if (this.mcPingResponse.players != null) {
                        ImGui.textWrapped(String.valueOf(this.mcPingResponse.players.online));
                    }
                }
                case MAX_PLAYERS -> {
                    if (this.mcPingResponse.players != null) {
                        ImGui.textWrapped(String.valueOf(this.mcPingResponse.players.max));
                    }
                }
                case MOTD -> {
                    ImGui.textWrapped(this.motd);
                }
                case ACTIONS -> {
                    ImGui.spacing();
                    final int buttonWidth = 0, buttonHeight = 28;
                    ImGui.button("..." + uniqueId + "actions", 0, 25);
                    if (ImGui.beginPopupContextItem(uniqueId + "popup", ImGuiPopupFlags.MouseButtonLeft)) {
                        final String address = this.hostname + ':' + this.mcPingResponse.server.port;
                        ImGui.text(address);
                        ImGui.separator();
                        ImGui.spacing();
                        if (ImGui.button("Copy Address" + uniqueId + "copyaddress", buttonWidth, buttonHeight)) {
                            this.mc.keyboard.setClipboard(address);
                        }
                        if (ImGui.button("Copy Info" + uniqueId + "copyinfo", buttonWidth, buttonHeight)) {
                            final StringBuilder serverInfoBuilder = new StringBuilder();
                            serverInfoBuilder.append("Server Address: ").append(address).append('\n');
                            String resolvedServerAddress = this.mcPingResponse.server.ip;
                            if (resolvedServerAddress.endsWith(".")) {
                                resolvedServerAddress = resolvedServerAddress.substring(0, resolvedServerAddress.length() - 1);
                            }
                            serverInfoBuilder.append("Resolved Server Address: ").append(resolvedServerAddress).append('\n');
                            if (this.mcPingResponse.version != null) {
                                serverInfoBuilder.append("Protocol: ").append(this.mcPingResponse.version.protocol).append('\n');
                                serverInfoBuilder.append("Version: ").append(this.mcPingResponse.version.name).append('\n');
                            }
                            if (this.mcPingResponse.players != null) {
                                serverInfoBuilder.append("Players: ");
                                serverInfoBuilder.append(this.mcPingResponse.players.online).append('/');
                                serverInfoBuilder.append(this.mcPingResponse.players.max).append('\n');
                            }
                            if (this.motd != null) {
                                serverInfoBuilder.append("MOTD: ").append(this.motd).append('\n');
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
                            if (this.queryPingResponse != null && this.queryPingResponse.plugins.sample.length > 0) {
                                serverInfoBuilder.append("Plugins: ").append('\n');
                                for (final String plugin : this.queryPingResponse.plugins.sample) {
                                    serverInfoBuilder.append(" - ").append(plugin).append('\n');
                                }
                            }
                            this.mc.keyboard.setClipboard(serverInfoBuilder.toString());
                        }
                        if (this.mcPingResponse.players.sample.length > 0) {
                            if (ImGui.button("Player List: " + (this.showPlayerList ? "Disable" : "Enable") + uniqueId, buttonWidth, buttonHeight)) {
                                this.showPlayerList = !this.showPlayerList;
                            }
                        }
                        if ((this.mcPingResponse.modinfo != null && this.mcPingResponse.modinfo.modList.length > 0) || (this.mcPingResponse.forgeData != null && this.mcPingResponse.forgeData.mods.length > 0)) {
                            if (ImGui.button("Mods: " + (this.showMods ? "Disable" : "Enable") + uniqueId, buttonWidth, buttonHeight)) {
                                this.showMods = !this.showMods;
                            }
                        }
                        if (this.queryPingResponse != null && this.queryPingResponse.plugins.sample.length > 0) {
                            if (ImGui.button("Plugins: " + (this.showPlugins ? "Disable" : "Enable") + uniqueId, buttonWidth, buttonHeight)) {
                                this.showPlugins = !this.showPlugins;
                            }
                        }
                        ImGui.endPopup();
                    }
                    ImGui.spacing();
                }
                default -> {
                }
            }
        }
    }

    public void renderSubData() {
        if (this.mcPingResponse == null) return;
        final String uniqueId = "##" + this.mcPingResponse.server.ip + ":" + this.mcPingResponse.server.port + "serverinfo";
        if (this.showPlayerList && this.mcPingResponse.players.sample.length > 0) {
            if (ImGui.begin(
                    "Player List of " + this.mcPingResponse.server.ip + ":" + this.mcPingResponse.server.port + uniqueId,
                    ImGuiWindowFlags.NoCollapse
            )) {
                if (ImGui.button("Close Player List" + uniqueId)) {
                    this.showPlayerList = false;
                }
                final PlayersTableColumn[] playersTableColumns = PlayersTableColumn.values();
                final int maxPlayerTableColumns = playersTableColumns.length;
                if (ImGui.beginTable("players" + uniqueId, maxPlayerTableColumns,
                        ImGuiTableFlags.Borders |
                                ImGuiTableFlags.Resizable |
                                ImGuiTableFlags.RowBg |
                                ImGuiTableFlags.ContextMenuInBody
                )) {
                    for (final PlayersTableColumn playersTableColumn : playersTableColumns) {
                        ImGui.tableSetupColumn(playersTableColumn.getName());
                    }
                    ImGui.tableHeadersRow();
                    for (final MCPingResponse.Players.Player player : this.mcPingResponse.players.sample) {
                        ImGui.tableNextRow();
                        for (int i = 0; i < maxPlayerTableColumns; i++) {
                            ImGui.tableSetColumnIndex(i);
                            final PlayersTableColumn playersTableColumn = playersTableColumns[i];
                            switch (playersTableColumn) {
                                case USERNAME -> ImGui.text(player.name);
                                case UUID -> ImGui.text(player.id);
                                case ACTIONS -> {
                                    ImGui.spacing();
                                    final int buttonWidth = 0, buttonHeight = 28;
                                    if (ImGui.button("Copy Data" + uniqueId + player.name, buttonWidth, buttonHeight)) {
                                        this.mc.keyboard.setClipboard(player.name + " (" + player.id + ")");
                                    }
                                    ImGui.spacing();
                                }
                                default -> {
                                }
                            }
                        }
                    }
                    ImGui.endTable();
                }
                ImGui.end();
            }
        }
        if (
                this.showMods && this.mcPingResponse != null && ((this.mcPingResponse.modinfo != null && this.mcPingResponse.modinfo.modList.length > 0) ||
                        (this.mcPingResponse.forgeData != null && this.mcPingResponse.forgeData.mods.length > 0))
        ) {
            if (ImGui.begin(
                    "Mods of " + this.mcPingResponse.server.ip + ":" + this.mcPingResponse.server.port + uniqueId,
                    ImGuiWindowFlags.NoCollapse
            )) {
                if (ImGui.button("Close Mods" + uniqueId)) {
                    this.showMods = false;
                }
                final ModsTableColumn[] modsTableColumns = ModsTableColumn.values();
                final int maxModTableColumns = modsTableColumns.length;
                if (ImGui.beginTable("mods" + uniqueId, maxModTableColumns,
                        ImGuiTableFlags.Borders |
                                ImGuiTableFlags.Resizable |
                                ImGuiTableFlags.RowBg |
                                ImGuiTableFlags.ContextMenuInBody
                )) {
                    for (final ModsTableColumn modsTableColumn : modsTableColumns) {
                        ImGui.tableSetupColumn(modsTableColumn.getName());
                    }
                    ImGui.tableHeadersRow();
                    for (final MCPingResponse.ModInfo.Mod mod : this.mcPingResponse.modinfo.modList) {
                        ImGui.tableNextRow();
                        for (int i = 0; i < maxModTableColumns; i++) {
                            ImGui.tableSetColumnIndex(i);
                            final ModsTableColumn modsTableColumn = modsTableColumns[i];
                            switch (modsTableColumn) {
                                case MOD_ID -> ImGui.text(mod.modid);
                                case MOD_VERSION -> ImGui.text(mod.version);
                                case ACTIONS -> {
                                    ImGui.spacing();
                                    final int buttonWidth = 0, buttonHeight = 28;
                                    if (ImGui.button("Copy Data" + uniqueId + mod.modid, buttonWidth, buttonHeight)) {
                                        this.mc.keyboard.setClipboard(mod.modid + " (" + mod.version + ")");
                                    }
                                    ImGui.spacing();
                                }
                                default -> {
                                }
                            }
                        }
                    }
                    ImGui.endTable();
                }
                if (this.mcPingResponse.forgeData != null && this.mcPingResponse.forgeData.mods.length > 0) {
                    final ForgeDataModsTableColumn[] forgeDataModsTableColumns = ForgeDataModsTableColumn.values();
                    final int maxForgeDataModTableColumns = forgeDataModsTableColumns.length;
                    if (ImGui.beginTable("forgedatamods" + uniqueId, maxForgeDataModTableColumns,
                            ImGuiTableFlags.Borders |
                                    ImGuiTableFlags.Resizable |
                                    ImGuiTableFlags.RowBg |
                                    ImGuiTableFlags.ContextMenuInBody
                    )) {
                        for (final ForgeDataModsTableColumn forgeDataModsTableColumn : forgeDataModsTableColumns) {
                            ImGui.tableSetupColumn(forgeDataModsTableColumn.getName());
                        }
                        ImGui.tableHeadersRow();
                        for (final MCPingResponse.ForgeData.Mod mod : this.mcPingResponse.forgeData.mods) {
                            ImGui.tableNextRow();
                            for (int i = 0; i < maxForgeDataModTableColumns; i++) {
                                ImGui.tableSetColumnIndex(i);
                                final ForgeDataModsTableColumn forgeDataModsTableColumn = forgeDataModsTableColumns[i];
                                switch (forgeDataModsTableColumn) {
                                    case MOD_ID -> ImGui.text(mod.modId);
                                    case MOD_MARKER -> ImGui.text(mod.modmarker);
                                    case ACTIONS -> {
                                        ImGui.spacing();
                                        final int buttonWidth = 0, buttonHeight = 28;
                                        if (ImGui.button("Copy Data" + uniqueId + mod.modId, buttonWidth, buttonHeight)) {
                                            this.mc.keyboard.setClipboard(mod.modId + " (" + mod.modmarker + ")");
                                        }
                                        ImGui.spacing();
                                    }
                                    default -> {
                                    }
                                }
                            }
                        }
                        ImGui.endTable();
                    }
                }
                ImGui.end();
            }
        }
        if (this.showPlugins && this.queryPingResponse != null && this.queryPingResponse.plugins.sample.length > 0) {
            if (ImGui.begin(
                    "Plugins of " + this.queryPingResponse.server.ip + ":" + this.queryPingResponse.server.port + uniqueId,
                    ImGuiWindowFlags.NoCollapse
            )) {
                if (ImGui.button("Close Plugins" + uniqueId)) {
                    this.showPlugins = false;
                }
                final PluginsTableColumn[] pluginsTableColumns = PluginsTableColumn.values();
                final int maxPluginTableColumns = pluginsTableColumns.length;
                if (ImGui.beginTable("plugins" + uniqueId, maxPluginTableColumns,
                        ImGuiTableFlags.Borders |
                                ImGuiTableFlags.Resizable |
                                ImGuiTableFlags.RowBg |
                                ImGuiTableFlags.ContextMenuInBody
                )) {
                    for (final PluginsTableColumn pluginsTableColumn : pluginsTableColumns) {
                        ImGui.tableSetupColumn(pluginsTableColumn.getName());
                    }
                    ImGui.tableHeadersRow();
                    for (final String plugin : this.queryPingResponse.plugins.sample) {
                        ImGui.tableNextRow();
                        for (int i = 0; i < maxPluginTableColumns; i++) {
                            ImGui.tableSetColumnIndex(i);
                            final PluginsTableColumn pluginsTableColumn = pluginsTableColumns[i];
                            switch (pluginsTableColumn) {
                                case NAME -> ImGui.text(plugin);
                                case ACTIONS -> {
                                    ImGui.spacing();
                                    final int buttonWidth = 0, buttonHeight = 28;
                                    if (ImGui.button("Copy Data" + uniqueId + plugin, buttonWidth, buttonHeight)) {
                                        this.mc.keyboard.setClipboard(plugin);
                                    }
                                    ImGui.spacing();
                                }
                                default -> {
                                }
                            }
                        }
                    }
                    ImGui.endTable();
                }
                ImGui.end();
            }
        }
    }

}


