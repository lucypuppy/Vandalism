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

package de.nekosarekawaii.vandalism.integration.hudrecode.gui;

import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import net.minecraft.client.gui.DrawContext;

public class HUDClientWindow extends ClientWindow {

    public HUDClientWindow() {
        super("HUD Config", Category.CONFIG);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {

    }

    @Override
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {

    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        return super.keyPressed(key, scanCode, modifiers, release);
    }

}