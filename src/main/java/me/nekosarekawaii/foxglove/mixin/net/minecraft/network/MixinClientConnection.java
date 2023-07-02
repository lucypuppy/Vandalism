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

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void injectChannelRead(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo callbackInfo) {
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(PacketListener.PacketEventType.READ, packet);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) callbackInfo.cancel();
        else packet = packetEvent.packet;
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    public void injectSend(Packet<?> packet, final @Nullable PacketCallbacks callbacks, final CallbackInfo ci) {
        final PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(PacketListener.PacketEventType.WRITE, packet);
        DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
        if (packetEvent.isCancelled()) ci.cancel();
        else packet = packetEvent.packet;
    }

}
