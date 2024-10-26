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

package de.nekosarekawaii.vandalism.feature.hud.gui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.feature.hud.HUDManager;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.awt.*;

public class HUDClientWindow extends ClientWindow {

    private final HUDManager hudManager;

    public HUDClientWindow(final HUDManager hudManager) {
        super("HUD Config", Category.CONFIG, 500f, 600f);
        this.hudManager = hudManager;
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName().replace(" ", "");
        if (ImGui.beginTabBar(id)) {
            for (final HUDElement hudElement : this.hudManager.getList()) {
                final String name = hudElement.getName();
                final String tabId = id + name + "tab";
                if (ImGui.beginTabItem(name + tabId)) {
                    ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.0f);
                    ImGui.beginChild(tabId + "values", ImGui.getColumnWidth(), - ImGui.getTextLineHeightWithSpacing() * 2.5f, true);
                    hudElement.renderValues();
                    ImGui.endChild();
                    ImGui.popStyleColor();
                    if (ImGui.button("Reset " + name + " Element" + id + name + "resetElement", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                        hudElement.resetValues();
                        Vandalism.getInstance().getConfigManager().save();
                    }
                    if (ImGui.button("Close HUD Config" + id + "close", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                        this.setActive(false);
                    }
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }
        for (final HUDElement hudElement : this.hudManager.getList()) {
            hudElement.render(context, delta, false);
        }
        final Window window = mc.getWindow();
        final double scaledWidth = window.getScaledWidth(), scaledHeight = window.getScaledHeight();
        context.drawHorizontalLine(0, (int) scaledWidth, (int) (scaledHeight * 0.66), Color.GREEN.getRGB());
        context.drawHorizontalLine(0, (int) scaledWidth, (int) (scaledHeight * 0.33), Color.GREEN.getRGB());
        context.drawVerticalLine((int) (scaledWidth * 0.66), 0, (int) scaledHeight, Color.GREEN.getRGB());
        context.drawVerticalLine((int) (scaledWidth * 0.33), 0, (int) scaledHeight, Color.GREEN.getRGB());
    }

}