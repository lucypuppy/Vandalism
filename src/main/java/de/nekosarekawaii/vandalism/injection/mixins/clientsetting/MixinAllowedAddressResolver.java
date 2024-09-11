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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AllowedAddressResolver.class)
public abstract class MixinAllowedAddressResolver {

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/Address;)Z"))
    private boolean antiServerBlockList(final BlockListChecker instance, final Address address) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiServerBlockList.getValue()) {
            return true;
        }
        return instance.isAllowed(address);
    }

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/ServerAddress;)Z"))
    private boolean antiServerBlockList(final BlockListChecker instance, final ServerAddress serverAddress) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiServerBlockList.getValue()) {
            return true;
        }
        return instance.isAllowed(serverAddress);
    }

}
