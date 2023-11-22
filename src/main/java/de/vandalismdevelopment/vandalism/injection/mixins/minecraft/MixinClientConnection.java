package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.PacketListener;
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
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(packet, false);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
        else packet = packetEvent.packet;
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void injectSend(Packet<?> packet, final PacketCallbacks callbacks, final boolean flush, final CallbackInfo ci) {
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(packet, true);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
        else packet = packetEvent.packet;
    }

    @Inject(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false, ordinal = 1), cancellable = true)
    private void redirectExceptionCaughtTimeoutDisconnect(final ChannelHandlerContext context, final Throwable ex, final CallbackInfo ci) {
        if (!Vandalism.getInstance().getConfigManager().getMainConfig().accessibilityCategory.antiTimeoutKick.getValue()) {
            ci.cancel();
        }
    }

}
