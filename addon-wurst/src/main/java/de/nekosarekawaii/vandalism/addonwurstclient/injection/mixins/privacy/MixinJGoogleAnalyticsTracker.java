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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.privacy;

import net.wurstclient.analytics.dmurph.JGoogleAnalyticsTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JGoogleAnalyticsTracker.class)
public abstract class MixinJGoogleAnalyticsTracker {

    @Inject(method = {"startBackgroundThread", "createBuilder"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelWurstJGoogleAnalyticsTrackerThread(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "stopBackgroundThread", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cancelWurstJGoogleAnalyticsTrackerStopThread(final CallbackInfo ci) {
        ci.cancel();
    }

}
