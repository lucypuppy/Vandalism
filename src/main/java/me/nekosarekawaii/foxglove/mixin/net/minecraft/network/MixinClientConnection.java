package me.nekosarekawaii.foxglove.mixin.net.minecraft.network;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import io.netty.channel.ChannelHandlerContext;
import me.nekosarekawaii.foxglove.event.PacketListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection {

    @Inject(at =
    @At(value = "INVOKE",
            target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V",
            ordinal = 0
    ),
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            cancellable = true
    )
    private void injectChannelRead0(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo ci) {
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(PacketListener.PacketEventType.READ, packet);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", cancellable = true)
    private void injectSend(Packet<?> packet, final @Nullable PacketCallbacks callback, final CallbackInfo ci) {
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(PacketListener.PacketEventType.WRITE, packet);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
    }

}
