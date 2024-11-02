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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.VisualSettings;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient implements MinecraftWrapper {

    @Shadow
    public abstract boolean isWindowFocused();

    @Shadow
    @Final
    public GameOptions options;

    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    private void unfocusedFPS(final CallbackInfoReturnable<Integer> info) {
        final VisualSettings visualSettings = Vandalism.getInstance().getClientSettings().getVisualSettings();
        if (visualSettings.unfocusedFPS.getValue() && !isWindowFocused()) {
            info.setReturnValue(Math.min(visualSettings.maxUnfocusedFPS.getValue(), this.options.getMaxFps().getValue()));
        }
    }

    @Inject(method = "isTelemetryEnabledByApi", at = @At("HEAD"), cancellable = true)
    private void antiTelemetry_DisableTelemetry(final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isOptionalTelemetryEnabled", at = @At("HEAD"), cancellable = true)
    private void antiTelemetry_DisableOptionalTelemetry(final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isOptionalTelemetryEnabledByApi()Z", at = @At("HEAD"), cancellable = true)
    private void antiTelemetry_DisableOptionalTelemetryFromApi(final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            cir.setReturnValue(false);
        }
    }

}