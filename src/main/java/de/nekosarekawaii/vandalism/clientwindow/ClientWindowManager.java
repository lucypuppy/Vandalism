/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.clientwindow;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.config.gui.ConfigsClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindowScreen;
import de.nekosarekawaii.vandalism.clientwindow.config.ClientWindowConfig;
import de.nekosarekawaii.vandalism.clientwindow.impl.AboutClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.GlobalSearchClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.PlayerKickerClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.ServerAddressResolverClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.port.PortScannerClientWindow;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.integration.imgui.ImLoader;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

public class ClientWindowManager extends Storage<ClientWindow> implements KeyboardInputListener, Render2DListener, MinecraftWrapper {

    private final File runDirectory;
    private MenuSettings menuSettings;

    public ClientWindowManager(final ConfigManager configManager, final File runDirectory) {
        this.runDirectory = runDirectory;

        configManager.add(new ClientWindowConfig(this));
        Vandalism.getInstance().getEventSystem().subscribe(this, KeyboardInputEvent.ID, Render2DEvent.ID);
    }

    public void load(final ClientSettings clientSettings) {
        this.menuSettings = clientSettings.getMenuSettings();
        ImLoader.init(runDirectory, menuSettings.menuScale.getValue());
    }

    @Override
    public void init() {
        this.add(
                new ConfigsClientWindow(),
                new GlobalSearchClientWindow(),
                new AboutClientWindow(),
                new PortScannerClientWindow(),
                new ServerAddressResolverClientWindow(),
                new PlayerKickerClientWindow()
        );
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action == GLFW.GLFW_PRESS && this.menuSettings.menuKey.getValue() == key && !(MinecraftClient.getInstance().currentScreen instanceof ChatScreen)) {
            this.openScreen();
        }
    }

    private void openScreen() {
        RenderSystem.recordRenderCall(() -> {
            Screen screen = MinecraftClient.getInstance().currentScreen;
            if (screen != null) {
                if (
                        screen instanceof ConnectScreen || screen instanceof LevelLoadingScreen ||
                                screen instanceof MessageScreen || screen instanceof ClientWindowScreen ||
                                screen instanceof CommandBlockScreen || screen instanceof SignEditScreen ||
                                screen instanceof BookEditScreen || screen instanceof BookScreen
                ) {
                    return;
                } else if (screen instanceof ChatScreen || screen instanceof HandledScreen<?> && !(screen instanceof InventoryScreen)) {
                    screen = null;
                }
            }
            mc.setScreen(new ClientWindowScreen(this, screen));
        });
    }

    public List<ClientWindow> getByCategory(final ClientWindow.Category category) {
        return this.getList().stream().filter(imWindow -> imWindow.getCategory() != null && imWindow.getCategory() == category).toList();
    }

    public List<ClientWindow.Category> getCategories() {
        return this.getList().stream().map(ClientWindow::getCategory).distinct().toList();
    }

}
