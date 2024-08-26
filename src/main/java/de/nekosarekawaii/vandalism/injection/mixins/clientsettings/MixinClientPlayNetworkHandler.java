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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.NetworkingSettings;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Redirect(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), remap = false)
    private void noStupidLoggingMessages(final Logger instance, final String s, final Object o1, final Object o2) {
        final NetworkingSettings networkingSettings = Vandalism.getInstance().getClientSettings().getNetworkingSettings();
        if (networkingSettings.noStupidLoggingMessages.getValue()) return;
        instance.warn(s, o1, o2);
    }

    @Redirect(method = "createEntity", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"), remap = false)
    private void noStupidLoggingMessages2(final Logger instance, final String s, final Object o) {
        final NetworkingSettings networkingSettings = Vandalism.getInstance().getClientSettings().getNetworkingSettings();
        if (networkingSettings.noStupidLoggingMessages.getValue()) return;
        instance.warn(s, o);
    }

    @Redirect(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"), remap = false)
    private void noStupidLoggingMessages3(final Logger instance, final String s, final Object o) {
        final NetworkingSettings networkingSettings = Vandalism.getInstance().getClientSettings().getNetworkingSettings();
        if (networkingSettings.noStupidLoggingMessages.getValue()) return;
        instance.warn(s, o);
    }

}
