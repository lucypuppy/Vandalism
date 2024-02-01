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

package de.nekosarekawaii.vandalism.integration.hud.gui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientmenu.base.ClientMenuWindow;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.integration.hud.HUDManager;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class HUDClientMenuWindow extends ClientMenuWindow {

    private final HUDManager hudManager;

    private HUDElement draggedElement;
    private boolean mouseDown = false;
    private int lastMouseX, lastMouseY;

    public HUDClientMenuWindow(final HUDManager hudManager) {
        super("HUD Config", Category.CONFIG);
        this.hudManager = hudManager;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.begin("HUD Config##hudconfigwindow");
        if (ImGui.button("Close HUD Config##closehudconfig")) {
            this.setActive(false);
        }
        ImGui.sameLine();
        if (ImGui.button("Reset HUD Config##resethudconfig")) {
            for (final HUDElement hudElement : this.hudManager.getList()) {
                hudElement.reset();
            }
            Vandalism.getInstance().getConfigManager().save();
        }
        ImGui.separator();
        if (ImGui.beginTabBar("##hudconfig")) {
            for (final HUDElement hudElement : this.hudManager.getList()) {
                if (ImGui.beginTabItem(hudElement.getName() + "##" + hudElement.getName() + "hudconfig")) {
                    if (ImGui.button("Reset##reset" + hudElement.getName() + "hudconfig")) {
                        hudElement.reset();
                        Vandalism.getInstance().getConfigManager().save();
                    }
                    ImGui.spacing();
                    ImGui.beginChild("##values" + hudElement.getName());
                    hudElement.renderValues();
                    ImGui.endChild();
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }
        ImGui.separator();
        ImGui.spacing();
        ImGui.end();
        final Window window = this.mc.getWindow();
        final double scaledWidth = window.getScaledWidth(), scaledHeight = window.getScaledHeight();
        boolean mouseOver = false;
        for (final HUDElement hudElement : this.hudManager.getList()) {
            if (hudElement.render(
                    this.draggedElement,
                    this.mouseDown,
                    mouseX,
                    mouseY,
                    mouseX - this.lastMouseX,
                    mouseY - this.lastMouseY,
                    scaledWidth,
                    scaledHeight,
                    context,
                    delta
            )) {
                mouseOver = true;
            }
            if (hudElement.isDragged()) {
                this.draggedElement = hudElement;
            }
        }
        if (!mouseOver && this.mouseDown) {
            this.draggedElement = null;
        }
        context.drawHorizontalLine(0, (int) scaledWidth, (int) (scaledHeight * 0.66), Color.green.getRGB());
        context.drawHorizontalLine(0, (int) scaledWidth, (int) (scaledHeight * 0.33), Color.green.getRGB());
        context.drawVerticalLine((int) (scaledWidth * 0.66), 0, (int) scaledHeight, Color.green.getRGB());
        context.drawVerticalLine((int) (scaledWidth * 0.33), 0, (int) scaledHeight, Color.green.getRGB());
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    @Override
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
        if (button == 0) {
            this.mouseDown = !release;
            if (release) {
                boolean save = false;
                for (final HUDElement hudElement : this.hudManager.getList()) {
                    if (hudElement.shouldSave()) {
                        hudElement.setShouldSave(false);
                        save = true;
                    }
                }
                if (save) {
                    Vandalism.getInstance().getConfigManager().save();
                }
            }
        }
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        for (final HUDElement hudElement : this.hudManager.getList()) {
            hudElement.onKeyInput(this.mc.getWindow().getHandle(), key, scanCode, release ? GLFW.GLFW_RELEASE : GLFW.GLFW_PRESS, modifiers);
        }
        return super.keyPressed(key, scanCode, modifiers, release);
    }

}