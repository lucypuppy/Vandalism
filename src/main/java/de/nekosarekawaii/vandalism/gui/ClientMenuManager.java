package de.nekosarekawaii.vandalism.gui;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.gui.base.ClientMenuScreen;
import de.nekosarekawaii.vandalism.gui.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.gui.config.ClientMenuConfig;
import de.nekosarekawaii.vandalism.gui.impl.ServerAddressResolverClientMenuWindow;
import de.nekosarekawaii.vandalism.gui.impl.ServerPingerClientMenuWindow;
import de.nekosarekawaii.vandalism.gui.impl.irc.IrcClientMenuWindow;
import de.nekosarekawaii.vandalism.gui.impl.namehistory.NameHistoryClientMenuWindow;
import de.nekosarekawaii.vandalism.gui.impl.nbteditor.NbtEditorClientMenuWindow;
import de.nekosarekawaii.vandalism.gui.impl.port.PortScannerClientMenuWindow;
import de.nekosarekawaii.vandalism.util.imgui.ImLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

public class ClientMenuManager extends Storage<ClientMenuWindow> implements KeyboardInputListener, Render2DListener {

    public ClientMenuManager(final ConfigManager configManager, final File runDirectory) {
        configManager.add(new ClientMenuConfig(this));
        DietrichEvents2.global().subscribe(this, KeyboardInputEvent.ID, Render2DEvent.ID);

        ImLoader.init(runDirectory);
    }

    @Override
    public void init() {
        this.add(
                new IrcClientMenuWindow(),
                new NameHistoryClientMenuWindow(),
                new NbtEditorClientMenuWindow(),
                new PortScannerClientMenuWindow(),
                new ServerAddressResolverClientMenuWindow(),
                new ServerPingerClientMenuWindow()
        );
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action == GLFW.GLFW_PRESS && Vandalism.getInstance().getClientSettings().getMenuSettings().menuKey.getValue() == key && !(MinecraftClient.getInstance().currentScreen instanceof ChatScreen)) {
            openScreen();
        }
    }

    public void openScreen() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen != null) {
            if (screen instanceof ConnectScreen || screen instanceof LevelLoadingScreen || screen instanceof ClientMenuScreen) {
                return;
            } else if (screen instanceof ChatScreen || screen instanceof HandledScreen<?> && !(screen instanceof InventoryScreen)) {
                screen = null;
            }
        }
        MinecraftClient.getInstance().setScreen(new ClientMenuScreen(this, screen));
    }

    public List<ClientMenuWindow> getByCategory(final ClientMenuWindow.Category category) {
        return this.getList().stream().filter(imWindow -> imWindow.getCategory() == category).toList();
    }

    public List<ClientMenuWindow.Category> getCategories() {
        return this.getList().stream().map(ClientMenuWindow::getCategory).distinct().toList();
    }

}
