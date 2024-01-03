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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins;

import de.nekosarekawaii.vandalism.addonwurstclient.AddonWurstClient;
import de.nekosarekawaii.vandalism.addonwurstclient.injection.access.IWurstClient;
import net.minecraft.client.option.KeyBinding;
import net.wurstclient.WurstClient;
import net.wurstclient.analytics.WurstAnalytics;
import net.wurstclient.hack.Hack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

@Mixin(value = WurstClient.class, remap = false)
public abstract class MixinWurstClient implements IWurstClient {

    @Shadow public abstract void setEnabled(boolean enabled);

    @Shadow private boolean enabled;

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/client/keybinding/v1/KeyBindingHelper;registerKeyBinding(Lnet/minecraft/client/option/KeyBinding;)Lnet/minecraft/client/option/KeyBinding;"))
    private KeyBinding cancelWurstZoomKeybinding(final KeyBinding keyBinding) {
        // Prevent the FAPI from loading the keybinding as we are loading the WurstClient to late for the FAPI
        // Since we don't need this keybinding anyway, we can just get rid of it
        return keyBinding;
    }

    @Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/wurstclient/analytics/WurstAnalytics;trackPageView(Ljava/lang/String;Ljava/lang/String;)V", remap = false))
    private void ignoreTrackPageView(final WurstAnalytics instance, final String url, final String title) {
        // Prevents the client from crashing...
    }

    @Override
    public void vandalism$setTrackedEnabled(boolean enabled) {
        if (!enabled) {
            // When the client is about to disable, store the enabled hacks
            // and create a new instance of the list
            AddonWurstClient.enabledHacks = new ArrayList<>();
            for (Hack allHax : WurstClient.INSTANCE.getHax().getAllHax()) {
                if (allHax.isEnabled()) {
                    AddonWurstClient.enabledHacks.add(allHax.getName());
                }
            }
        } else if (AddonWurstClient.enabledHacks != null) {
            // If the client is about to enable, check if it was previously disabled and
            // stored enabled modules, then enable them again
            for (Hack allHax : WurstClient.INSTANCE.getHax().getAllHax()) {
                if (AddonWurstClient.enabledHacks.contains(allHax.getName())) {
                    allHax.setEnabled(true);
                }
            }
            AddonWurstClient.enabledHacks = null;
        }

        // Do the normal enable/disable stuff
        this.setEnabled(enabled);
    }

    @Override
    public void vandalism$setSilentEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
