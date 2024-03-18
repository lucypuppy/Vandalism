/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindowScreen;
import de.nekosarekawaii.vandalism.clientwindow.config.ClientWindowConfig;
import de.nekosarekawaii.vandalism.clientwindow.impl.ServerAddressResolverClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.ServerPingerClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.nbteditor.gui.NbtEditorClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.port.PortScannerClientWindow;
import de.nekosarekawaii.vandalism.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.normal.render.Render2DListener;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
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

public class ClientWindowManager extends Storage<ClientWindow> implements KeyboardInputListener, Render2DListener, MinecraftWrapper {

    public ClientWindowManager(final ConfigManager configManager, final File runDirectory) {
        configManager.add(new ClientWindowConfig(this));
        Vandalism.getInstance().getEventSystem().subscribe(this, KeyboardInputEvent.ID, Render2DEvent.ID);

        ImLoader.init(runDirectory);
    }

    @Override
    public void init() {
        this.add(
                new PortScannerClientWindow(),
                new ServerAddressResolverClientWindow(),
                new ServerPingerClientWindow(),
                new NbtEditorClientWindow()
        );
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action == GLFW.GLFW_PRESS && Vandalism.getInstance().getClientSettings().getMenuSettings().menuKey.getValue() == key && !(MinecraftClient.getInstance().currentScreen instanceof ChatScreen)) {
            openScreen();
        }
    }

    public void openScreen() {
        RenderSystem.recordRenderCall(() -> {
            Screen screen = MinecraftClient.getInstance().currentScreen;
            if (screen != null) {
                if (screen instanceof ConnectScreen || screen instanceof LevelLoadingScreen || screen instanceof MessageScreen || screen instanceof ClientWindowScreen) {
                    return;
                } else if (screen instanceof ChatScreen || screen instanceof HandledScreen<?> && !(screen instanceof InventoryScreen)) {
                    screen = null;
                }
            }
            mc.setScreen(new ClientWindowScreen(this, screen));
        });
    }

    public List<ClientWindow> getByCategory(final ClientWindow.Category category) {
        return this.getList().stream().filter(imWindow -> imWindow.getCategory() == category).toList();
    }

    public List<ClientWindow.Category> getCategories() {
        return this.getList().stream().map(ClientWindow::getCategory).distinct().toList();
    }

}
