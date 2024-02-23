/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.clientmenu.base;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.feature.hud.gui.HUDClientMenuWindow;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImLoader;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClientMenuScreen extends Screen {

    private final ClientMenuManager clientMenuManager;
    private final Screen prevScreen;

    public ClientMenuScreen(final ClientMenuManager clientMenuManager, final Screen prevScreen) {
        super(Text.literal("Client Menu"));
        this.clientMenuManager = clientMenuManager;
        this.prevScreen = prevScreen;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        ImLoader.draw(() -> {
            final HUDClientMenuWindow hudImWindow = this.clientMenuManager.getByClass(HUDClientMenuWindow.class);
            if (hudImWindow.isActive()) {
                hudImWindow.render(context, mouseX, mouseY, delta);
            } else {
                if (ImGui.beginMainMenuBar()) {
                    for (final ClientMenuWindow.Category category : this.clientMenuManager.getCategories()) {
                        if (ImGui.beginMenu(category.getName())) {
                            for (final ClientMenuWindow clientMenuWindow : this.clientMenuManager.getByCategory(category)) {
                                if (ImGui.checkbox(clientMenuWindow.getName(), clientMenuWindow.isActive())) {
                                    clientMenuWindow.toggle();
                                }
                            }
                            if (category == ClientMenuWindow.Category.CONFIG) {
                                ImGui.separator();
                                if (ImUtils.subButton("Save Configs")) {
                                    Vandalism.getInstance().getConfigManager().save();
                                }
                            } else if (category == ClientMenuWindow.Category.SERVER) {
                                if (ServerConnectionUtil.lastServerExists() && this.client.getCurrentServerEntry() == null) {
                                    ImGui.separator();
                                    if (ImUtils.subButton("Connect to last server")) {
                                        ServerConnectionUtil.connectToLastServer();
                                    }
                                }
                            }
                            ImGui.endMenu();
                        }
                    }
                    ImGui.endMainMenuBar();
                }
                for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
                    if (window.isActive()) {
                        window.render(context, mouseX, mouseY, delta);
                    }
                }
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, true);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
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
        for (final ClientMenuWindow window : this.clientMenuManager.getList()) {
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
