package de.vandalismdevelopment.vandalism.util.minecraft.impl;

import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftUtil;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class ServerUtil extends MinecraftUtil {

    private static GameMenuScreen GAME_MENU_SCREEN = null;
    private static ServerInfo LAST_SERVER_INFO = null;

    public static void connectToLastServer() {
        if (LAST_SERVER_INFO == null) return;
        if (world() != null) {
            disconnect();
        }
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), mc(), ServerAddress.parse(LAST_SERVER_INFO.address), LAST_SERVER_INFO, false);
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
            GAME_MENU_SCREEN.init(mc(), mc().getWindow().getScaledWidth(), mc().getWindow().getScaledHeight());
        }
        GAME_MENU_SCREEN.disconnect();
    }

}
