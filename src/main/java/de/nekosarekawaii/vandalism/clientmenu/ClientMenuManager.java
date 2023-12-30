package de.nekosarekawaii.vandalism.clientmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuScreen;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.config.ClientMenuConfig;
import de.nekosarekawaii.vandalism.clientmenu.impl.ServerAddressResolverClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.impl.ServerPingerClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.impl.namehistory.NameHistoryClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.impl.nbteditor.NbtEditorClientMenuWindow;
import de.nekosarekawaii.vandalism.clientmenu.impl.port.PortScannerClientMenuWindow;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.imgui.ImLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

public class ClientMenuManager extends Storage<ClientMenuWindow> implements KeyboardInputListener, Render2DListener, MinecraftWrapper {

    public ClientMenuManager(final ConfigManager configManager, final File runDirectory) {
        configManager.add(new ClientMenuConfig(this));
        Vandalism.getInstance().getEventSystem().subscribe(this, KeyboardInputEvent.ID, Render2DEvent.ID);

        ImLoader.init(runDirectory);
    }

    @Override
    public void init() {
        this.add(
                new PortScannerClientMenuWindow(),
                new ServerAddressResolverClientMenuWindow(),
                new ServerPingerClientMenuWindow(),
                new NameHistoryClientMenuWindow(),
                new NbtEditorClientMenuWindow()
        );
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action == GLFW.GLFW_PRESS && Vandalism.getInstance().getClientSettings().getMenuSettings().menuKey.getValue() == key && !(MinecraftClient.getInstance().currentScreen instanceof ChatScreen)) {
            openScreen();
        }
    }

    public void openScreen() {
        RenderSystem.recordRenderCall(() -> {
            Screen screen = MinecraftClient.getInstance().currentScreen;
            if (screen != null) {
                if (screen instanceof ConnectScreen || screen instanceof LevelLoadingScreen || screen instanceof MessageScreen || screen instanceof ClientMenuScreen) {
                    return;
                } else if (screen instanceof ChatScreen || screen instanceof HandledScreen<?> && !(screen instanceof InventoryScreen)) {
                    screen = null;
                }
            }
            mc.setScreen(new ClientMenuScreen(this, screen));
        });
    }

    public List<ClientMenuWindow> getByCategory(final ClientMenuWindow.Category category) {
        return this.getList().stream().filter(imWindow -> imWindow.getCategory() == category).toList();
    }

    public List<ClientMenuWindow.Category> getCategories() {
        return this.getList().stream().map(ClientMenuWindow::getCategory).distinct().toList();
    }

}
