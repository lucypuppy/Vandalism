package me.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class LastServerUtils {

    private static ServerInfo lastServerInfo = null;

    public static void connectToLastServer() {
        if (lastServerInfo == null) return;
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), MinecraftClient.getInstance(), ServerAddress.parse(lastServerInfo.address), lastServerInfo, false);
    }

    public static boolean lastServerExists() {
        return lastServerInfo != null;
    }

    public static ServerInfo getLastServerInfo() {
        return lastServerInfo;
    }

    public static void setLastServerInfo(final ServerInfo serverInfo) {
        lastServerInfo = serverInfo;
    }

}
