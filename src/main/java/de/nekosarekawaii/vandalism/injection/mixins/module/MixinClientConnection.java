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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.HAProxySpooferModule;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.haproxy.*;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow
    public Channel channel;

    @Inject(method = "channelActive", at = @At("TAIL"))
    public void channelActive(final ChannelHandlerContext context, final CallbackInfo info) {
        final HAProxySpooferModule haProxySpooferModule = Vandalism.getInstance().getModuleManager().getHaProxySpooferModule();
        if (!haProxySpooferModule.isActive()) return;
        final HAProxyMessage haProxyMessage = new HAProxyMessage(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY, HAProxyProxiedProtocol.TCP4, haProxySpooferModule.customizeIP.getValue() ? haProxySpooferModule.ip.getValue() : RandomUtils.getRandomIp(), "0.0.0.0", 1, 1);
        this.channel.pipeline().addFirst("haproxy", HAProxyMessageEncoder.INSTANCE);
        this.channel.writeAndFlush(haProxyMessage);
        this.channel.pipeline().remove(HAProxyMessageEncoder.INSTANCE);
    }

}
