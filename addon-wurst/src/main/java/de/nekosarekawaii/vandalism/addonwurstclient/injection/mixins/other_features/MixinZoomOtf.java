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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.other_features;

import net.wurstclient.other_features.ZoomOtf;
import net.wurstclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ZoomOtf.class)
public abstract class MixinZoomOtf {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/wurstclient/other_features/ZoomOtf;addSetting(Lnet/wurstclient/settings/Setting;)V", ordinal = 0), remap = false)
    private void removeWurstZoomLevelSetting(final ZoomOtf instance, final Setting setting) {
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/wurstclient/other_features/ZoomOtf;addSetting(Lnet/wurstclient/settings/Setting;)V", ordinal = 1), remap = false)
    private void removeWurstZoomScrollSetting(final ZoomOtf instance, final Setting setting) {
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelWurstZoomMouseScrollEvent(final CallbackInfo ci) {
        ci.cancel();
    }

}
