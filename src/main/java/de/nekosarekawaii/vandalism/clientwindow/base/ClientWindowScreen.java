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

package de.nekosarekawaii.vandalism.clientwindow.base;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.clientwindow.impl.AboutClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.GlobalSearchClientWindow;
import de.nekosarekawaii.vandalism.feature.hud.gui.HUDClientWindow;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImLoader;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClientWindowScreen extends Screen {

    private final ClientWindowManager clientWindowManager;
    private final Screen prevScreen;

    public ClientWindowScreen(final ClientWindowManager clientWindowManager, final Screen prevScreen) {
        super(Text.literal("Client Window"));
        this.clientWindowManager = clientWindowManager;
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();

        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.init();
        }
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        ImLoader.draw(() -> {
            final HUDClientWindow hudImWindow = this.clientWindowManager.getByClass(HUDClientWindow.class);
            if (hudImWindow.isActive()) {
                hudImWindow.render(context, mouseX, mouseY, delta);
            } else {
                if (ImGui.beginMainMenuBar()) {
                    if (ImGui.button("Global Search")) {
                        this.clientWindowManager.getByClass(GlobalSearchClientWindow.class).toggle();
                    }
                    for (final ClientWindow.Category category : this.clientWindowManager.getCategories()) {
                        if (category == null) continue;
                        if (ImGui.beginMenu(category.getName())) {
                            for (final ClientWindow clientWindow : this.clientWindowManager.getByCategory(category)) {
                                if (ImGui.checkbox(clientWindow.getName(), clientWindow.isActive())) {
                                    clientWindow.toggle();
                                }
                            }
                            if (category == ClientWindow.Category.CONFIG) {
                                ImGui.separator();
                                if (ImUtils.subButton("Save Configs")) {
                                    Vandalism.getInstance().getConfigManager().save();
                                }
                            } else if (category == ClientWindow.Category.SERVER) {
                                if (ServerUtil.lastServerExists() && this.client.getCurrentServerEntry() == null) {
                                    ImGui.separator();
                                    if (ImUtils.subButton("Connect to last server")) {
                                        ServerUtil.connectToLastServer();
                                    }
                                }
                            }
                            ImGui.endMenu();
                        }
                    }
                    if (ImGui.button("About")) {
                        this.clientWindowManager.getByClass(AboutClientWindow.class).toggle();
                    }
                    ImGui.endMainMenuBar();
                }
                for (final ClientWindow window : this.clientWindowManager.getList()) {
                    if (window.isActive()) {
                        window.render(context, mouseX, mouseY, delta);
                    }
                }
                if (ImGui.beginMainMenuBar()) {
                    if (ImGui.button("Close")) {
                        this.close();
                    }
                    ImGui.endMainMenuBar();
                }
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, true);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            if (!window.keyPressed(keyCode, scanCode, modifiers, false)) {
                return false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            if (!window.keyPressed(keyCode, scanCode, modifiers, true)) {
                return false;
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        ImLoader.forceUpdateMouse();
        if (this.prevScreen == null) {
            this.client.mouse.lockCursor();
            if (this.client.player == null) {
                return;
            }
        }
        this.client.setScreen(this.prevScreen);
    }

}
