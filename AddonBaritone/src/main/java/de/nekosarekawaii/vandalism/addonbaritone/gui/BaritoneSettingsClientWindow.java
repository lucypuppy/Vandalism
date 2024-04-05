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

package de.nekosarekawaii.vandalism.addonbaritone.gui;

import de.nekosarekawaii.vandalism.addonbaritone.settings.BaritoneSettingParser;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import net.minecraft.client.gui.DrawContext;

public class BaritoneSettingsClientWindow extends ClientWindow {

    private final BaritoneSettingParser baritoneSettingParser;

    public BaritoneSettingsClientWindow(final BaritoneSettingParser baritoneSettingParser) {
        super("Baritone Settings", Category.MISC);
        this.baritoneSettingParser = baritoneSettingParser;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.baritoneSettingParser.renderValues();
    }

}
