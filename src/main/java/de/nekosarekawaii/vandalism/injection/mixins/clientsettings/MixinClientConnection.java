package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

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
    private void vandalism$antiTimeoutKick(final ChannelHandlerContext context, final Throwable throwable, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTimeoutKick.getValue()) {
            ci.cancel();
        }
    }

}
