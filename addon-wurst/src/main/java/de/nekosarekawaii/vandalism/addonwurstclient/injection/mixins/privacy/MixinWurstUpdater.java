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

import net.wurstclient.update.WurstUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WurstUpdater.class)
public abstract class MixinWurstUpdater {

    @Inject(method = {"onUpdate", "checkForUpdates"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelWurstUpdater(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "isOutdated", at = @At("HEAD"), cancellable = true, remap = false)
    private void wurstIsNeverOutdated(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
