package me.nekosarekawaii.foxglove.mixin.net.minecraft.network;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import io.netty.channel.ChannelHandlerContext;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.PacketListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class)
public abstract class MixinClientConnection {

    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at =
            @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void injectChannelRead0(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo ci) {
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(PacketListener.PacketEventType.READ, packet);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    private void injectSend(Packet<?> packet, final @Nullable PacketCallbacks callback, final CallbackInfo ci) {
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(PacketListener.PacketEventType.WRITE, packet);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
    }

    @Inject(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V", ordinal = 1), cancellable = true)
    private void redirectExceptionCaughtTimeoutDisconnect(final ChannelHandlerContext context, final Throwable ex, final CallbackInfo ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().antiTimeoutKick.getValue())
            ci.cancel();
    }

}
