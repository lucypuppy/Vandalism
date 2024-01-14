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

package de.nekosarekawaii.vandalism.addonwurstclient;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.addonwurstclient.injection.access.IWurstClient;
import de.nekosarekawaii.vandalism.addonwurstclient.module.WurstClientModule;
import net.wurstclient.WurstClient;

import java.util.List;

public class AddonWurstClient implements VandalismAddonLauncher {

    // Temporary list to store the enabled hacks when the user disables the WurstClient module
    public static List<String> enabledHacks;

    private WurstClientModule module;

    @Override
    public void onLaunch(final Vandalism vandalism) {
        //Initialize WurstClient, counterpart in MixinWurstInitializer.java
        WurstClient.INSTANCE.initialize();

        vandalism.getModuleManager().add(this.module = new WurstClientModule());
    }

    @Override
    public void onLateLaunch(final Vandalism vandalism) {
        ((IWurstClient) (Object) WurstClient.INSTANCE).vandalism$setSilentEnabled(this.module.isActive());
    }

}
