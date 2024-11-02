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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Objects;

@Mixin(value = ClientConnection.class, priority = 9999)
public abstract class MixinClientConnection {

    @Shadow
    public static <T extends PacketListener> void handlePacket(final Packet<T> packet, final PacketListener listener) {
    }

    @Shadow
    @Nullable
    public volatile PacketListener packetListener;

    @Shadow
    protected abstract void sendImmediately(final Packet<?> packet, final @Nullable PacketCallbacks callbacks, final boolean flush);

    @Shadow
    public Channel channel;

    @Unique
    private boolean vandalism$selfRepeating;

    @Unique
    private boolean vandalism$onIncomingPacket(final Packet<?> packet, final CallbackInfo ci) {
        final IncomingPacketListener.IncomingPacketEvent event = new IncomingPacketListener.IncomingPacketEvent(packet, this.packetListener.getPhase(), (ClientConnection) (Object) this);
        Vandalism.getInstance().getEventSystem().callExceptionally(IncomingPacketListener.IncomingPacketEvent.ID, event);
        if (event.isCancelled()) {
            return true;
        }
        if (!packet.equals(event.packet)) {
            handlePacket(event.packet, packetListener);
            return true;
        }
        return false;
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal = 0), cancellable = true)
    private void callIncomingPacketListener(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo ci) {
        if (packet instanceof final BundleS2CPacket bundleS2CPacket) {
            for (final Iterator<Packet<? super ClientPlayPacketListener>> it = bundleS2CPacket.getPackets().iterator(); it.hasNext(); ) {
                if (vandalism$onIncomingPacket(it.next(), ci)) {
                    it.remove();
                }
            }
        } else {
            if (vandalism$onIncomingPacket(packet, ci)) {
                ci.cancel();
            }
        }

    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void callOutgoingPacketListener(Packet<?> packet, final PacketCallbacks callbacks, final boolean flush, final CallbackInfo ci) {
        if (vandalism$selfRepeating) {
            vandalism$selfRepeating = false;
            return;
        }

        final OutgoingPacketListener.OutgoingPacketEvent event = new OutgoingPacketListener.OutgoingPacketEvent(packet, this.packetListener.getPhase(), (ClientConnection) (Object) this);
        Vandalism.getInstance().getEventSystem().callExceptionally(OutgoingPacketListener.OutgoingPacketEvent.ID, event);
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
        final ClientConnection self = (ClientConnection) (Object) this;
        final ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler != null && Objects.equals(self, networkHandler.getConnection())) {
            Vandalism.getInstance().getEventSystem().callExceptionally(
                    DisconnectListener.DisconnectEvent.ID,
                    new DisconnectListener.DisconnectEvent(self, disconnectReason)
            );
        }
    }

}