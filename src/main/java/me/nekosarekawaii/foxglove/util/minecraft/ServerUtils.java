package me.nekosarekawaii.foxglove.util.minecraft;

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

    private final static MinecraftClient client = MinecraftClient.getInstance();

    private static ServerInfo lastServerInfo = null;

    public static void connectToLastServer() {
        if (lastServerInfo == null) return;
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), client, ServerAddress.parse(lastServerInfo.address), lastServerInfo, false);
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

    private final static Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");

    public static void disconnect() {
        final boolean inSingleplayer = client.isInSingleplayer();
        if (client.world != null) client.world.disconnect();
        if (inSingleplayer) client.disconnect(new MessageScreen(SAVING_LEVEL_TEXT));
        else client.disconnect();
        final TitleScreen titleScreen = new TitleScreen();
        if (inSingleplayer) client.setScreen(titleScreen);
        else if (client.isConnectedToRealms()) client.setScreen(new RealmsMainScreen(titleScreen));
        else client.setScreen(new MultiplayerScreen(titleScreen));
    }

}
