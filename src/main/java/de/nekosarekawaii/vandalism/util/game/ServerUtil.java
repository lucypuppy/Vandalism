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

package de.nekosarekawaii.vandalism.util.game;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class ServerUtil implements MinecraftWrapper {

    private static GameMenuScreen GAME_MENU_SCREEN = null;
    private static ServerInfo LAST_SERVER_INFO = null;

    public static void connectToLastServer() {
        if (LAST_SERVER_INFO == null) return;
        if (mc.world != null) {
            disconnect();
        }
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), mc, ServerAddress.parse(LAST_SERVER_INFO.address), LAST_SERVER_INFO, false);
    }

    public static boolean lastServerExists() {
        return LAST_SERVER_INFO != null;
    }

    public static ServerInfo getLastServerInfo() {
        return LAST_SERVER_INFO;
    }

    public static void setLastServerInfo(final ServerInfo serverInfo) {
        LAST_SERVER_INFO = serverInfo;
    }

    public static void disconnect() {
        if (GAME_MENU_SCREEN == null) {
            GAME_MENU_SCREEN = new GameMenuScreen(false);
            GAME_MENU_SCREEN.init(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
        }
        GAME_MENU_SCREEN.disconnect();
    }

    @Deprecated
    public static String fixVersionName(final String input) {
        if (input.contains(" ")) {
            final String[] data = input.split(" ");
            if (data.length > 1) {
                final String name = data[0];
                String versionRange = input.substring(name.length() + 1);
                if (versionRange.contains(", ")) {
                    final String[] versions = versionRange.split(", ");
                    if (versions.length > 1) {
                        versionRange = versions[0] + "-" + versions[versions.length - 1];
                    }
                }
                return name + " " + versionRange;
            }
        }
        return input;
    }

}
