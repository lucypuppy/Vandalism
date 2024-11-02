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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.ModPacketBlockerModule;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkAddon.class, remap = false)
public abstract class MixinClientPlayNetworkAddon {


    @Inject(method = {"invokeRegisterEvent", "invokeUnregisterEvent", "onServerReady"}, at = @At("HEAD"), cancellable = true)
    private void hookModPacketBlocker(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Vandalism.getInstance().getModuleManager().getModPacketBlockerModule();
        if (modPacketBlockerModule.isActive() && modPacketBlockerModule.unloadFabricAPICallbacks.getValue()) {
            ci.cancel();
        }
    }

}
