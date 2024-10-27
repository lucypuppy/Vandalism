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

package de.nekosarekawaii.vandalism.addonwurstclient;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonwurstclient.injection.access.IWurstClient;
import de.nekosarekawaii.vandalism.addonwurstclient.module.WurstClientModule;
import de.nekosarekawaii.vandalism.base.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.hud.impl.WatermarkHUDElement;
import lombok.Getter;
import net.wurstclient.WurstClient;

import java.util.List;

public class AddonWurstClient implements VandalismAddonLauncher {

    @Getter
    private static AddonWurstClient instance;

    // Temporary list to store the enabled hacks when the user disables the WurstClient module
    public static List<String> enabledHacks;

    private WurstClientModule module;

    public IntegerValue wurstOffsetX;
    public IntegerValue wurstOffsetY;

    @Override
    public void onLaunch(final Vandalism vandalism) {
        instance = this;

        // Initialize WurstClient, counterpart in MixinWurstInitializer.java
        WurstClient.INSTANCE.initialize();

        vandalism.getModuleManager().add(this.module = new WurstClientModule());


        final WatermarkHUDElement watermarkHUDElement = vandalism.getHudManager().watermarkHUDElement;
        this.wurstOffsetX = new IntegerValue(watermarkHUDElement, "Wurst Offset X", "The X offset of the Wurst client watermark.",
                86, -512, 1024);
        this.wurstOffsetY = new IntegerValue(watermarkHUDElement, "Wurst Offset Y", "The Y offset of the Wurst client watermark.",
                131, -512, 1024);
    }

    @Override
    public void onLateLaunch(final Vandalism vandalism) {
        ((IWurstClient) (Object) WurstClient.INSTANCE).vandalism$setSilentEnabled(this.module.isActive());
    }

}
