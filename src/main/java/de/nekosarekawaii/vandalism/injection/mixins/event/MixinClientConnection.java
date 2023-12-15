package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.base.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.network.OutgoingPacketListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
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

    @Unique
    private boolean vandalism$selfRepeating;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal = 0), cancellable = true)
    private void callIncomingPacketListener(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo ci) {
        final var event = new IncomingPacketListener.IncomingPacketEvent(packet);
        DietrichEvents2.global().postInternal(IncomingPacketListener.IncomingPacketEvent.ID, event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        if (!packet.equals(event.packet)) {
            handlePacket(packet, packetListener);
            ci.cancel();
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void callOutgoingPacketListener(Packet<?> packet, final PacketCallbacks callbacks, final boolean flush, final CallbackInfo ci) {
        if (vandalism$selfRepeating) {
            vandalism$selfRepeating = false;
            return;
        }

        final var event = new OutgoingPacketListener.OutgoingPacketEvent(packet);
        DietrichEvents2.global().postInternal(OutgoingPacketListener.OutgoingPacketEvent.ID, event);
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
        DietrichEvents2.global().postInternal(DisconnectListener.DisconnectEvent.ID,
                new DisconnectListener.DisconnectEvent((ClientConnection) (Object) this, disconnectReason));
    }

}