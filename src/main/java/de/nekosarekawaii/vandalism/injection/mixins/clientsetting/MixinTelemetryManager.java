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

import com.mojang.authlib.minecraft.UserApiService;
import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.session.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TelemetryManager.class)
public abstract class MixinTelemetryManager {

    @Mutable
    @Shadow
    @Final
    private PropertyMap propertyMap;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/session/telemetry/TelemetryManager;propertyMap:Lnet/minecraft/client/session/telemetry/PropertyMap;"))
    private void antiTelemetry(final MinecraftClient client, final UserApiService userApi, final Session session, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            this.propertyMap = PropertyMap.builder().build();
        }
    }

    @Inject(method = "getSender", at = @At("RETURN"), cancellable = true)
    private void antiTelemetry(final CallbackInfoReturnable<TelemetrySender> cir) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            cir.setReturnValue(TelemetrySender.NOOP);
        }
    }

}
