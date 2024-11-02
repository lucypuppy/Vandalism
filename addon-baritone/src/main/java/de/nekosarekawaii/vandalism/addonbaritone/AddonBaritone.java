/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.addonbaritone;

import baritone.Baritone;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonbaritone.gui.BaritoneSettingsClientWindow;
import de.nekosarekawaii.vandalism.addonbaritone.modules.BaritoneAddonModule;
import de.nekosarekawaii.vandalism.addonbaritone.settings.BaritoneSettingMapper;
import de.nekosarekawaii.vandalism.base.VandalismAddonLauncher;

public class AddonBaritone implements VandalismAddonLauncher {

    private BaritoneSettingMapper baritoneSettingMapper;

    @Override
    public void onLaunch(final Vandalism vandalism) {
        this.baritoneSettingMapper = new BaritoneSettingMapper();

        vandalism.getClientWindowManager().add(
                new BaritoneSettingsClientWindow(this.baritoneSettingMapper)
        );

        vandalism.getModuleManager().add(new BaritoneAddonModule());
    }

    @Override
    public void onLateLaunch(final Vandalism vandalism) {
        // Enable default settings because most default settings are dumb.
        Baritone.settings().chatControl.value = false;
        Baritone.settings().allowParkour.value = true;

        // Load baritone settings
        this.baritoneSettingMapper.loadSettings();
    }

}
