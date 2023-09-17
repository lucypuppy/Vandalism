package de.foxglovedevelopment.foxglove.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;

public class ServerUtils {

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

    public static void disconnect() {
        disconnect(true);
    }

    public static void disconnect(final boolean gui) {
        final boolean inSingleplayer = MinecraftClient.getInstance().isInSingleplayer();
        if (MinecraftClient.getInstance().world != null) MinecraftClient.getInstance().world.disconnect();
        if (inSingleplayer)
            MinecraftClient.getInstance().disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        else MinecraftClient.getInstance().disconnect();
        if (gui) {
            final TitleScreen titleScreen = new TitleScreen();
            if (inSingleplayer) MinecraftClient.getInstance().setScreen(titleScreen);
            else if (MinecraftClient.getInstance().isConnectedToRealms())
                MinecraftClient.getInstance().setScreen(new RealmsMainScreen(titleScreen));
            else MinecraftClient.getInstance().setScreen(new MultiplayerScreen(titleScreen));
        }
    }

}
