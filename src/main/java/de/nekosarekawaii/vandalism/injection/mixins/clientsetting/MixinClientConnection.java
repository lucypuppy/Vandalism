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
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 9999)
public abstract class MixinClientConnection {

    @Inject(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false, ordinal = 1), cancellable = true)
    private void antiTimeoutKick(final ChannelHandlerContext context, final Throwable throwable, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTimeoutKick.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    private void printPacketErrorsInConsole(final ChannelHandlerContext context, final Throwable ex, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().printPacketErrorsInConsole.getValue()) {
            Vandalism.getInstance().getLogger().error("Networking packet error.", ex);
        }
    }

}
