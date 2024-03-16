/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.normal.network.DisconnectListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 9999)
public abstract class MixinClientConnection {

    @Shadow
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {
    }

    @Shadow @Nullable private volatile PacketListener packetListener;

    @Shadow protected abstract void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush);

    @Shadow
    private Channel channel;

    @Shadow
    @Final
    public static AttributeKey<NetworkState.PacketHandler<?>> CLIENTBOUND_PROTOCOL_KEY;

    @Shadow
    @Final
    public static AttributeKey<NetworkState.PacketHandler<?>> SERVERBOUND_PROTOCOL_KEY;

    @Unique
    private boolean vandalism$selfRepeating;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal = 0), cancellable = true)
    private void callIncomingPacketListener(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo ci) {
        final var event = new IncomingPacketListener.IncomingPacketEvent(packet, this.channel.attr(CLIENTBOUND_PROTOCOL_KEY).get().getState(), (ClientConnection) (Object) this);
        Vandalism.getInstance().getEventSystem().postInternal(IncomingPacketListener.IncomingPacketEvent.ID, event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        if (!packet.equals(event.packet)) {
            handlePacket(event.packet, packetListener);
            ci.cancel();
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void callOutgoingPacketListener(Packet<?> packet, final PacketCallbacks callbacks, final boolean flush, final CallbackInfo ci) {
        if (vandalism$selfRepeating) {
            vandalism$selfRepeating = false;
            return;
        }

        final var event = new OutgoingPacketListener.OutgoingPacketEvent(packet, this.channel.attr(SERVERBOUND_PROTOCOL_KEY).get().getState(), (ClientConnection) (Object) this);
        Vandalism.getInstance().getEventSystem().postInternal(OutgoingPacketListener.OutgoingPacketEvent.ID, event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (!packet.equals(event.packet)) {
            vandalism$selfRepeating = true;
            this.sendImmediately(event.packet, callbacks, flush);
        }
    }

    @Inject(method = "disconnect", at = @At("RETURN"))
    private void callDisconnectListener(Text disconnectReason, CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(
                DisconnectListener.DisconnectEvent.ID,
                new DisconnectListener.DisconnectEvent((ClientConnection) (Object) this, disconnectReason)
        );
    }

}