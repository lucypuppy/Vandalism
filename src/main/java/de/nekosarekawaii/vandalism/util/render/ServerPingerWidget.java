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

package de.nekosarekawaii.vandalism.util.render;

import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

import java.awt.*;

public class ServerPingerWidget implements MinecraftWrapper {

    private static final MSTimer PING_TIMER = new MSTimer();

    private static final MultiplayerServerListWidget WIDGET = new MultiplayerServerListWidget(
            new MultiplayerScreen(new TitleScreen()),
            MinecraftClient.getInstance(),
            1,
            50,
            10,
            20
    );

    private static final int MAGICAL_OFFSET = 2;
    private static final int ELEMENT_WIDTH = 304;
    private static final int ELEMENT_HEIGHT = 42;

    @Deprecated
    private static final String SHIT = "§ö§1§3";

    public static void draw(final ServerInfo currentServerInfo, final DrawContext context, final int mouseX, final int mouseY, final float delta, final int startY) {
        if (currentServerInfo == null) return;
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (!menuSettings.serverPingerWidget.getValue()) return;
        WIDGET.setY(startY);
        final int pingDelay = menuSettings.serverPingerWidgetDelay.getValue();
        if (PING_TIMER.hasReached(pingDelay, true)) {
            ping(currentServerInfo);
        }
        WIDGET.setWidth(mc.currentScreen.width);
        final int x = WIDGET.getWidth() / 2 /* screen width / 2 */ - ELEMENT_WIDTH / 2;
        final int y = WIDGET.getY() + MAGICAL_OFFSET;
        final int x2 = x + ELEMENT_WIDTH;
        final int y2 = WIDGET.getY() + ELEMENT_HEIGHT;
        final float progress = (ELEMENT_WIDTH / 100f) * PING_TIMER.getDelta() * (100f / pingDelay);
        context.enableScissor(x, y, x2, y2 + MAGICAL_OFFSET);
        WIDGET.render(context, -1, -1, delta);
        context.drawHorizontalLine(x, x2, y2 + 1, Color.GRAY.getRGB());
        context.drawHorizontalLine(x, (int) (x + progress), y2 + 1, Color.GREEN.getRGB());
        context.disableScissor();
    }

    public static void ping(final ServerInfo currentServerInfo) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (!menuSettings.serverPingerWidget.getValue()) return;
        final ServerList serverList = new ServerList(mc);
        currentServerInfo.online = false;
        if (!currentServerInfo.name.contains(SHIT)) {
            currentServerInfo.name += SHIT;
        }
        serverList.add(currentServerInfo, false);
        WIDGET.setServers(serverList);
        PING_TIMER.reset();
    }

    public static boolean shouldSave(final ServerInfo serverInfo) {
        return !serverInfo.name.contains(SHIT);
    }

}

