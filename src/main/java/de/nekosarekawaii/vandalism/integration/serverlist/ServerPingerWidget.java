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

package de.nekosarekawaii.vandalism.integration.serverlist;

import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.game.PingState;
import de.nekosarekawaii.vandalism.util.server.ServerUtil;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.responses.QueryPingResponse;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.awt.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerPingerWidget implements MinecraftWrapper {

    private static final String BASE64_START = "data:image/png;base64,";

    private static final MSTimer PING_TIMER = new MSTimer();

    private static final MultiplayerScreen FAKE_MULTIPLAYER_SCREEN = new MultiplayerScreen(new TitleScreen());

    private static final MultiplayerServerListWidget WIDGET = new MultiplayerServerListWidget(
            FAKE_MULTIPLAYER_SCREEN,
            MinecraftClient.getInstance(),
            1,
            50,
            10,
            20
    );

    private static final int MAGICAL_OFFSET = 2;
    private static final int ELEMENT_WIDTH = 300;
    private static final int ELEMENT_HEIGHT = 37;

    public static boolean IN_USE = false;

    private static final int GRAY = Color.GRAY.getRGB();
    private static final int GREEN = Color.GREEN.getRGB();

    private static final CopyOnWriteArrayList<String> PLUGIN_DATA = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<String> FORGE_MOD_DATA = new CopyOnWriteArrayList<>();

    private static void setServerInfo(final ServerInfo serverInfo) {
        if (serverInfo == null) {
            return;
        }
        final ServerList serverList = new ServerList(mc);
        serverList.add(serverInfo, false);
        WIDGET.setServers(serverList);
    }

    public static void draw(final ServerInfo currentServerInfo, final DrawContext context, final int mouseX, final int mouseY, final float delta, final int startY) {
        if (currentServerInfo == null) {
            return;
        }
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.serverPingerWidget.getValue()) {
            if (WIDGET.servers.isEmpty()) {
                return;
            }
            IN_USE = true;
            currentServerInfo.setStatus(ServerInfo.Status.SUCCESSFUL);
            WIDGET.setY(startY);
            final int pingDelay = enhancedServerListSettings.serverPingerWidgetDelay.getValue();
            if (PING_TIMER.hasReached(pingDelay, true)) {
                ping(currentServerInfo);
            }
            WIDGET.setWidth(mc.currentScreen.width);
            final int x = mc.getWindow().getScaledWidth() / 2 - ELEMENT_WIDTH / 2 - MAGICAL_OFFSET;
            final int y = WIDGET.getY() + MAGICAL_OFFSET;
            final int x2 = x + ELEMENT_WIDTH + 4;
            final int y2 = WIDGET.getY() + ELEMENT_HEIGHT;
            final float progress = (ELEMENT_WIDTH / 100f) * PING_TIMER.getDelta() * (100f / pingDelay);
            context.enableScissor(x, y, x2, y2 + MAGICAL_OFFSET);
            WIDGET.render(context, mouseX, mouseY, delta);
            context.drawHorizontalLine(x, x2, y2 + 1, GRAY);
            context.drawHorizontalLine(x, (int) (x + progress), y2 + 1, GREEN);
            context.disableScissor();
            final Screen.PositionedTooltip tooltip = FAKE_MULTIPLAYER_SCREEN.tooltip;
            if (tooltip != null) {
                final List<OrderedText> lines = new ArrayList<>(tooltip.tooltip());
                if (!lines.isEmpty()) {
                    for (final String line : PLUGIN_DATA) lines.add(Text.literal(line).asOrderedText());
                    for (final String line : FORGE_MOD_DATA) lines.add(Text.literal(line).asOrderedText());
                    context.drawTooltip(mc.textRenderer, lines, tooltip.positioner(), mouseX, mouseY);
                }
                FAKE_MULTIPLAYER_SCREEN.tooltip = null;
            }
            IN_USE = false;
        }
    }

    public static void ping(final ServerInfo currentServerInfo) {
        if (currentServerInfo == null) return;
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.serverPingerWidget.getValue()) {
            IN_USE = true;
            PING_TIMER.reset();
            currentServerInfo.setStatus(ServerInfo.Status.SUCCESSFUL);
            currentServerInfo.playerCountLabel = ScreenTexts.EMPTY;
            currentServerInfo.label = Text.translatable("multiplayer.status.pinging");
            currentServerInfo.ping = -2L;
            currentServerInfo.playerListSummary = Collections.emptyList();
            currentServerInfo.setFavicon(null);
            setServerInfo(currentServerInfo);
            final int serverPingerWidgetDelay = enhancedServerListSettings.serverPingerWidgetDelay.getValue();
            final String address = currentServerInfo.address;
            try {
                final Pair<String, Integer> addressParts = ServerUtil.splitServerAddress(address);
                MCPing.pingQuery()
                        .address(addressParts.getLeft(), enhancedServerListSettings.serverPingerQueryPingPort.getValue())
                        .timeout(serverPingerWidgetDelay, serverPingerWidgetDelay)
                        .exceptionHandler(t -> {
                            PLUGIN_DATA.clear();
                            Vandalism.getInstance().getLogger().error("Failed to query ping server: {}", address, t);
                        })
                        .finishHandler(response -> {
                            PLUGIN_DATA.clear();
                            final int maxPlugins = 20;
                            final QueryPingResponse.Plugins plugins = response.plugins;
                            if (plugins != null) {
                                PLUGIN_DATA.add("Plugins:");
                                final String[] pluginData = plugins.sample;
                                for (int i = 0; i < pluginData.length; i++) {
                                    final String plugin = pluginData[i];
                                    PLUGIN_DATA.add(" " + plugin);
                                    if (i == maxPlugins) {
                                        PLUGIN_DATA.add(" and " + (pluginData.length - maxPlugins) + " more plugins...");
                                        break;
                                    }
                                }
                            }
                        }).getAsync();
                MCPing.pingModern(SharedConstants.getProtocolVersion())
                        .address(address)
                        .timeout(serverPingerWidgetDelay, serverPingerWidgetDelay)
                        .exceptionHandler(t -> {
                            FORGE_MOD_DATA.clear();
                            currentServerInfo.ping = -1L;
                            switch (t) {
                                case UnknownHostException unknownHostException ->
                                        currentServerInfo.label = Text.literal(Formatting.DARK_RED + PingState.UNKNOWN_HOST.getMessage());
                                case ConnectionRefusedException connectionRefusedException ->
                                        currentServerInfo.label = Text.literal(Formatting.DARK_RED + PingState.CONNECTION_REFUSED.getMessage());
                                case ConnectTimeoutException connectTimeoutException ->
                                        currentServerInfo.label = Text.literal(Formatting.DARK_RED + PingState.CONNECTION_TIMED_OUT.getMessage());
                                case DataReadException dataReadException ->
                                        currentServerInfo.label = Text.literal(Formatting.DARK_RED + PingState.DATA_READ_FAILED.getMessage());
                                case PacketReadException packetReadException ->
                                        currentServerInfo.label = Text.literal(Formatting.DARK_RED + PingState.PACKET_READ_FAILED.getMessage());
                                case null, default -> {
                                    currentServerInfo.label = Text.literal(Formatting.DARK_RED + PingState.FAILED.getMessage());
                                    Vandalism.getInstance().getLogger().error("Failed to ping server: {}", address, t);
                                }
                            }
                            setServerInfo(currentServerInfo);
                        })
                        .finishHandler(response -> {
                            currentServerInfo.version = Text.literal(response.version.name);
                            currentServerInfo.ping = response.server.ping;
                            final String descriptionString = response.description;
                            try {
                                final MutableText description = Text.Serialization.fromJson(descriptionString, DynamicRegistryManager.EMPTY);
                                if (description != null) {
                                    currentServerInfo.label = description;
                                }
                            } catch (final Exception ignored) {
                                currentServerInfo.label = Text.literal(descriptionString);
                            }
                            final String base64FaviconString = response.favicon;
                            if (base64FaviconString != null) {
                                if (!base64FaviconString.startsWith(BASE64_START)) {
                                    Vandalism.getInstance().getLogger().error("Server {} has responded with an unknown base64 server icon format.", address);
                                } else {
                                    try {
                                        final String faviconString = base64FaviconString.substring(BASE64_START.length()).replaceAll("\n", "");
                                        final byte[] faviconBytes = Base64.getDecoder().decode(faviconString.getBytes(StandardCharsets.UTF_8));
                                        currentServerInfo.setFavicon(faviconBytes.length < 1 ? null : faviconBytes);
                                    } catch (IllegalArgumentException e) {
                                        Vandalism.getInstance().getLogger().error("Server {} has responded with an malformed base64 server icon.", address, e);
                                    }
                                }
                            }
                            final int playersOnline = response.players.online;
                            final int maxPlayers = response.players.max;
                            currentServerInfo.playerCountLabel = MultiplayerServerListPinger.createPlayerCountText(playersOnline, maxPlayers);
                            final MCPingResponse.Players.Player[] players = response.players.sample;
                            if (players.length > 0) {
                                final List<Text> list = new ArrayList<>(players.length);
                                for (final MCPingResponse.Players.Player player : response.players.sample) {
                                    list.add(Text.literal(player.name));
                                }
                                if (players.length < playersOnline) {
                                    list.add(Text.translatable("multiplayer.status.and_more", playersOnline - players.length));
                                }
                                currentServerInfo.playerListSummary = list;
                            } else {
                                currentServerInfo.playerListSummary = List.of();
                            }
                            FORGE_MOD_DATA.clear();
                            final int maxForgeMods = 20;
                            if (response.modinfo != null) {
                                FORGE_MOD_DATA.add("Forge Mod Type: " + response.modinfo.type);
                                final MCPingResponse.ModInfo.Mod[] mods = response.modinfo.modList;
                                if (mods.length > 0) {
                                    FORGE_MOD_DATA.add("Forge Mods:");
                                    for (int i = 0; i < mods.length; i++) {
                                        final MCPingResponse.ModInfo.Mod mod = mods[i];
                                        FORGE_MOD_DATA.add(" " + mod.modid + " v" + mod.version);
                                        if (i == maxForgeMods) {
                                            FORGE_MOD_DATA.add(" and " + (mods.length - maxForgeMods) + " more mods...");
                                            break;
                                        }
                                    }
                                }
                            }
                            if (response.forgeData != null) {
                                FORGE_MOD_DATA.add("Forge FML Net Version: " + response.forgeData.fmlNetworkVersion);
                                final MCPingResponse.ForgeData.Channel[] channels = response.forgeData.channels;
                                if (channels.length > 0) {
                                    FORGE_MOD_DATA.add("Forge Channels: " + channels.length);
                                }
                                final MCPingResponse.ForgeData.Mod[] mods = response.forgeData.mods;
                                if (mods.length > 0) {
                                    FORGE_MOD_DATA.add("Forge Mods:");
                                    for (int i = 0; i < mods.length; i++) {
                                        final MCPingResponse.ForgeData.Mod mod = mods[i];
                                        FORGE_MOD_DATA.add(" " + mod.modId + " | " + mod.modmarker);
                                        if (i == maxForgeMods) {
                                            FORGE_MOD_DATA.add(" and " + (mods.length - maxForgeMods) + " more mods...");
                                            break;
                                        }
                                    }
                                }
                            }
                            setServerInfo(currentServerInfo);
                        }).getAsync();
            } catch (final Throwable t) {
                currentServerInfo.ping = -1L;
                currentServerInfo.label = Text.literal(Formatting.DARK_RED + PingState.FAILED.getMessage());
                setServerInfo(currentServerInfo);
                Vandalism.getInstance().getLogger().error("Failed to ping server: {}", address, t);
            }
            PING_TIMER.reset();
            IN_USE = false;
        }
    }

}

