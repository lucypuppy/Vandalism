/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

import com.google.gson.JsonSyntaxException;
import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.clientwindow.impl.port.PortResult;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class ServerPingerWidget implements MinecraftWrapper {

    private static final String Base64_START = "data:image/png;base64,";

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
    private static final int ELEMENT_WIDTH = 304;
    private static final int ELEMENT_HEIGHT = 42;

    public static boolean IN_USE = false;

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
            currentServerInfo.online = true;
            WIDGET.setY(startY);
            final int pingDelay = enhancedServerListSettings.serverPingerWidgetDelay.getValue();
            if (PING_TIMER.hasReached(pingDelay, true)) {
                ping(currentServerInfo);
            }
            WIDGET.setWidth(mc.currentScreen.width);
            final int x = WIDGET.getWidth() / 2 /* screen width / 2 */ - ELEMENT_WIDTH / 2;
            final int y = WIDGET.getY() + MAGICAL_OFFSET;
            final int x2 = x + ELEMENT_WIDTH + 4;
            final int y2 = WIDGET.getY() + ELEMENT_HEIGHT;
            final float progress = (ELEMENT_WIDTH / 100f) * PING_TIMER.getDelta() * (100f / pingDelay);
            context.enableScissor(x, y, x2, y2 + MAGICAL_OFFSET);
            WIDGET.render(context, mouseX, mouseY, delta);
            context.drawHorizontalLine(x, x2, y2 + 1, Color.GRAY.getRGB());
            context.drawHorizontalLine(x, (int) (x + progress), y2 + 1, Color.GREEN.getRGB());
            context.disableScissor();
            final List<Text> tooltip = FAKE_MULTIPLAYER_SCREEN.multiplayerScreenTooltip;
            if (tooltip != null) {
                context.drawTooltip(mc.textRenderer, tooltip, mouseX, mouseY);
                FAKE_MULTIPLAYER_SCREEN.setMultiplayerScreenTooltip(null);
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
            currentServerInfo.online = true;
            currentServerInfo.playerCountLabel = ScreenTexts.EMPTY;
            currentServerInfo.label = Text.translatable("multiplayer.status.pinging");
            currentServerInfo.ping = -2L;
            currentServerInfo.playerListSummary = Collections.emptyList();
            currentServerInfo.setFavicon(null);
            setServerInfo(currentServerInfo);
            final int serverPingerWidgetDelay = enhancedServerListSettings.serverPingerWidgetDelay.getValue();
            MCPing.pingModern(SharedConstants.getProtocolVersion())
                    .address(currentServerInfo.address)
                    .timeout(serverPingerWidgetDelay, serverPingerWidgetDelay)
                    .exceptionHandler(t -> {
                        currentServerInfo.ping = -1L;
                        if (t instanceof UnknownHostException) {
                            currentServerInfo.label = Text.literal(Formatting.DARK_RED + PortResult.PingState.UNKNOWN_HOST.getMessage());
                        } else if (t instanceof ConnectionRefusedException) {
                            currentServerInfo.label = Text.literal(Formatting.DARK_RED + PortResult.PingState.CONNECTION_REFUSED.getMessage());
                        } else if (t instanceof ConnectTimeoutException) {
                            currentServerInfo.label = Text.literal(Formatting.DARK_RED + PortResult.PingState.CONNECTION_TIMED_OUT.getMessage());
                        } else if (t instanceof DataReadException) {
                            currentServerInfo.label = Text.literal(Formatting.DARK_RED + PortResult.PingState.DATA_READ_FAILED.getMessage());
                        } else if (t instanceof PacketReadException) {
                            currentServerInfo.label = Text.literal(Formatting.DARK_RED + PortResult.PingState.PACKET_READ_FAILED.getMessage());
                        } else {
                            currentServerInfo.label = Text.literal(Formatting.DARK_RED + PortResult.PingState.FAILED.getMessage());
                            Vandalism.getInstance().getLogger().error("Failed to ping server: " + currentServerInfo.address, t);
                        }
                        setServerInfo(currentServerInfo);
                    })
                    .finishHandler(response -> {
                        currentServerInfo.version = Text.literal(response.version.name);
                        currentServerInfo.ping = response.server.ping;
                        final String descriptionString = response.description;
                        try {
                            final MutableText description = Text.Serialization.fromJson(descriptionString);
                            if (description != null) {
                                currentServerInfo.label = description;
                            }
                        } catch (JsonSyntaxException ignored) {
                            currentServerInfo.label = Text.literal(descriptionString);
                        }
                        final String base64FaviconString = response.favicon;
                        if (base64FaviconString != null) {
                            if (!base64FaviconString.startsWith(Base64_START)) {
                                Vandalism.getInstance().getLogger().error("Server " + currentServerInfo.address + " has responded with an unknown base64 server icon format.");
                            } else {
                                try {
                                    final String faviconString = base64FaviconString.substring(Base64_START.length()).replaceAll("\n", "");
                                    final byte[] faviconBytes = Base64.getDecoder().decode(faviconString.getBytes(StandardCharsets.UTF_8));
                                    currentServerInfo.setFavicon(faviconBytes.length < 1 ? null : faviconBytes);
                                } catch (IllegalArgumentException e) {
                                    Vandalism.getInstance().getLogger().error("Server " + currentServerInfo.address + " has responded with an malformed base64 server icon.", e);
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
                        setServerInfo(currentServerInfo);
                    }).getAsync();
            PING_TIMER.reset();
            IN_USE = false;
        }
    }

}

