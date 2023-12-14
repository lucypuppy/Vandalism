package de.nekosarekawaii.vandalism.gui;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.gui.base.ImGuiConfig;
import de.nekosarekawaii.vandalism.gui.base.ImGuiScreen;
import de.nekosarekawaii.vandalism.gui.base.ImWindow;
import de.nekosarekawaii.vandalism.gui.impl.ServerAddressResolverImWindow;
import de.nekosarekawaii.vandalism.gui.impl.ServerPingerImWindow;
import de.nekosarekawaii.vandalism.gui.impl.irc.IrcImWindow;
import de.nekosarekawaii.vandalism.gui.impl.namehistory.NameHistoryImWindow;
import de.nekosarekawaii.vandalism.gui.impl.nbteditor.NbtEditorImWindow;
import de.nekosarekawaii.vandalism.gui.impl.port.PortScannerImWindow;
import de.nekosarekawaii.vandalism.gui.loader.ImLoader;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

public class ImGuiManager extends Storage<ImWindow> implements KeyboardInputListener, Render2DListener, MinecraftWrapper {

    public ImGuiManager(final ConfigManager configManager, final File runDirectory) {
        configManager.add(new ImGuiConfig(this));
        DietrichEvents2.global().subscribe(this, KeyboardInputEvent.ID, Render2DEvent.ID);

        ImLoader.init(runDirectory);
    }

    @Override
    public void init() {
        this.add(
                new IrcImWindow(),
                new NameHistoryImWindow(),
                new NbtEditorImWindow(),
                new PortScannerImWindow(),
                new ServerAddressResolverImWindow(),
                new ServerPingerImWindow()
        );
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action == GLFW.GLFW_PRESS && Vandalism.getInstance().getClientSettings().getMenuSettings().menuKey.getValue() == key) {
            openScreen();
        }
    }

    public void openScreen() {
        final var screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof ChatScreen || screen instanceof ConnectScreen || screen instanceof LevelLoadingScreen || screen instanceof ImGuiScreen) {
            return;
        }

        MinecraftClient.getInstance().setScreen(new ImGuiScreen(this, screen));
    }

    public List<ImWindow> getByCategory(final ImWindow.Category category) {
        return this.getList().stream().filter(imWindow -> imWindow.getCategory() == category).toList();
    }

    public List<ImWindow.Category> getCategories() {
        return this.getList().stream().map(ImWindow::getCategory).distinct().toList();
    }

}
