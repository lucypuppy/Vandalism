package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.network.IncomingPacketListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 9999)
public abstract class MixinClientConnection {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal = 0), cancellable = true)
    private void vandalism$callPacketReceivedEvent(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo ci) {
        final IncomingPacketListener.IncomingPacketEvent packetEvent = new IncomingPacketListener.IncomingPacketEvent(packet, IncomingPacketListener.PacketEventState.RECEIVED);
        DietrichEvents2.global().postInternal(IncomingPacketListener.IncomingPacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
        else packet = packetEvent.packet;
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void vandalism$callPacketSendEvent(Packet<?> packet, final PacketCallbacks callbacks, final boolean flush, final CallbackInfo ci) {
        final IncomingPacketListener.IncomingPacketEvent packetEvent = new IncomingPacketListener.IncomingPacketEvent(packet, IncomingPacketListener.PacketEventState.SEND);
        DietrichEvents2.global().postInternal(IncomingPacketListener.IncomingPacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
        else packet = packetEvent.packet;
    }

}