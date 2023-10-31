package de.vandalismdevelopment.vandalism.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;

public class ServerUtil {

    private static ServerInfo LAST_SERVER_INFO = null;

    public static void connectToLastServer() {
        if (LAST_SERVER_INFO == null) return;
        if (MinecraftClient.getInstance().world != null) {
            disconnect();
        }
        ConnectScreen.connect(
                new MultiplayerScreen(new TitleScreen()),
                MinecraftClient.getInstance(),
                ServerAddress.parse(LAST_SERVER_INFO.address),
                LAST_SERVER_INFO,
                false
        );
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
        final boolean singlePlayer = MinecraftClient.getInstance().isInSingleplayer();
        final ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        if (MinecraftClient.getInstance().world != null) MinecraftClient.getInstance().world.disconnect();
        if (singlePlayer)
            MinecraftClient.getInstance().disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        else MinecraftClient.getInstance().disconnect();
        final TitleScreen titleScreen = new TitleScreen();
        if (singlePlayer) MinecraftClient.getInstance().setScreen(titleScreen);
        else if (serverInfo != null && serverInfo.isRealm()) {
            MinecraftClient.getInstance().setScreen(new RealmsMainScreen(titleScreen));
        } else MinecraftClient.getInstance().setScreen(new MultiplayerScreen(titleScreen));
    }

}
