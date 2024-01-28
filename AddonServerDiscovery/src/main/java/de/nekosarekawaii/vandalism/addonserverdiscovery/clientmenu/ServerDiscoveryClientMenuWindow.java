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

package de.nekosarekawaii.vandalism.addonserverdiscovery.clientmenu;

import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import re.catgirls.serverdiscovery.ServerDiscovery;
import re.catgirls.serverdiscovery.entry.ServerEntry;
import re.catgirls.serverdiscovery.search.NumberRangeType;
import re.catgirls.serverdiscovery.search.SearchQueryBuilder;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerDiscoveryClientMenuWindow extends ClientMenuWindow {

    public ServerDiscoveryClientMenuWindow() {
        super("Server Discovery", Category.SERVER);
    }

    /* api stuff */
    private final CopyOnWriteArrayList<ServerEntry> entries = new CopyOnWriteArrayList<>();

    private final ServerDiscovery api = new ServerDiscovery(
            "https://api.catgirls.re",
            "eD0gW4x01PBahgNnChWqPtyzp6P7ApUe"
    );

    /* find settings */
    private final ImInt minimumPlayers = new ImInt(0);
    private final ImInt maximumPlayers = new ImInt(10);
    private final ImInt serverLimit = new ImInt(1000);

    private final ImString brandFilter = new ImString(128);
    private final ImString motdFilter = new ImString(128);
    private final ImString playerNameFilter = new ImString(16);

    private NumberRangeType playerNumberRange = NumberRangeType.ANY;
    private boolean crackedOnly = false;
    private boolean onlineOnly = false;

    /* ui variables */
    private ServerEntry hoveredEntry = null;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ImGui.begin("Server Discovery", ImGuiWindowFlags.MenuBar);

        if (ImGui.beginMenuBar()) {

            /* Find servers menu */
            if (ImGui.beginMenu("Find Servers")) {
                ImGui.inputText("Brand Filter", brandFilter);
                ImGui.inputText("MOTD Filter", motdFilter);

                if (ImGui.beginCombo("Number Range Type", StringUtils.normalizeEnumName(playerNumberRange.name()))) {
                    for (NumberRangeType value : NumberRangeType.values()) {
                        if (ImGui.selectable(StringUtils.normalizeEnumName(value.name()), value == playerNumberRange)) {
                            playerNumberRange = value;
                        }
                    }

                    ImGui.endCombo();
                }

                switch (playerNumberRange) {
                    case EQUALS -> ImGui.inputInt("Players", minimumPlayers);
                    case LESS_THAN -> ImGui.inputInt("Max Players", maximumPlayers);
                    case GREATER_THAN -> ImGui.inputInt("Min Players", minimumPlayers);
                    case BETWEEN -> {
                        ImGui.inputInt("Minimum Players", minimumPlayers);
                        ImGui.inputInt("Maximum Range", maximumPlayers);
                    }
                }

                ImGui.inputInt("Server limit", serverLimit);

                if (ImGui.checkbox("Cracked only", crackedOnly)) {
                    onlineOnly = false;
                    crackedOnly = !crackedOnly;
                }

                if (ImGui.checkbox("Online only", onlineOnly)) {
                    crackedOnly = false;
                    onlineOnly = !onlineOnly;
                }

                ImGui.separator();

                if (ImUtils.subButton("Find servers")) {
                    final var query = new SearchQueryBuilder();

                    if (crackedOnly) query.offlineOnly();
                    if (onlineOnly) query.onlineOnly();
                    if (brandFilter.isNotEmpty()) query.containsInBrand(brandFilter.get());
                    if (motdFilter.isNotEmpty()) query.containsMotd(motdFilter.get());

                    performServerSearch(query);
                }

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Find Player")) {
                ImGui.inputText("Player name", playerNameFilter);
                ImGui.inputInt("Server limit", serverLimit);

                ImGui.separator();

                if (ImUtils.subButton("Find Player") && !playerNameFilter.isEmpty()) {
                    performServerSearch(new SearchQueryBuilder().containsPlayer(playerNameFilter.get()));
                }

                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        if (ImGui.beginTable("serverdiscoverytable", ServerDiscoveryRows.values().length, ImGuiTableFlags.Borders
                | ImGuiTableFlags.Resizable
                | ImGuiTableFlags.RowBg
                | ImGuiTableFlags.ScrollY
                | ImGuiTableFlags.SizingStretchProp)) {

            for (ServerDiscoveryRows value : ServerDiscoveryRows.values()) {
                ImGui.tableSetupColumn(StringUtils.normalizeEnumName(value.name()));
            }

            ImGui.tableHeadersRow();

            for (ServerEntry entry : entries) {
                ImGui.tableNextRow();

                ImGui.tableNextColumn();
                ImGui.text(entry.address());

                if (ImGui.isItemHovered()) {
                    hoveredEntry = entry;
                    ImGui.openPopup("serverentry-popup");
                }

                ImGui.tableNextColumn();
                ImGui.text(Objects.requireNonNull(Formatting.strip(entry.motd())).replaceAll("\n", " | "));

                ImGui.tableNextColumn();
                ImGui.text(Formatting.strip(entry.players().isBlank() ? "None" : entry.players()) + " | " + entry.playerCount());

                ImGui.tableNextColumn();
                ImGui.text(Formatting.strip(entry.brand()));

                ImGui.tableNextColumn();

                final var color = entry.online() ? Color.GREEN : Color.RED;
                ImGui.textColored(color.getRed(), color.getGreen(), color.getBlue(), 255, String.valueOf(entry.online()));
            }

            ImGui.endTable();
        }

        renderServerPopup();

        ImGui.end();
    }

    /**
     * Render the popup for the hovered entry.
     */
    private void renderServerPopup() {
        if (hoveredEntry == null) return;

        if (ImGui.beginPopupContextItem("serverentry-popup")) {
            ImGui.text(hoveredEntry.address());
            ImGui.separator();
            ImGui.text("MOTD: " + Formatting.strip(hoveredEntry.motd()));
            ImGui.text("Brand: " + hoveredEntry.brand());
            ImGui.text("Player count: " + hoveredEntry.playerCount());
            ImGui.text("Player list: " + Formatting.strip(hoveredEntry.players()));
            ImGui.separator();

            if (ImGui.button("Copy remote address")) {
                mc.keyboard.setClipboard(hoveredEntry.address());
                ImGui.closeCurrentPopup();
            }

            ImGui.button("Add to server list");

            ImGui.endPopup();
        }
    }

    private void performServerSearch(final SearchQueryBuilder query) {
        CompletableFuture.runAsync(() -> {
            try {
                entries.clear();
                entries.addAll(api.getServers(query, serverLimit.get(), playerNumberRange, minimumPlayers.get(), maximumPlayers.get()));
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to perform search query", e);
            }
        });
    }

    /**
     * Enum containing the rows of the server discovery table.
     */
    enum ServerDiscoveryRows {
        ADDRESS, MOTD, PLAYERS, BRAND, ONLINE
    }

}
