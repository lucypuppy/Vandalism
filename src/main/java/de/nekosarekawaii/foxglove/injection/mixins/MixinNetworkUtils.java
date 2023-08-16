package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.gui.screen.CustomResourcePackConfirmScreen;
import net.minecraft.client.util.NetworkUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.InputStream;

@Mixin(NetworkUtils.class)
public abstract class MixinNetworkUtils {

    @Redirect(method = "method_15303", at = @At(value = "INVOKE", target = "Ljava/io/InputStream;read([B)I"))
    private static int redirectByteWriting(final InputStream inputStream, final byte[] bs) throws IOException {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().resourcePackSpoof.getValue() && CustomResourcePackConfirmScreen.skipDownload) {
            return -1;
        }
        return inputStream.read(bs);
    }

    @Redirect(method = "method_15303", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;)V", ordinal = 0))
    private static void redirectThreadInterruption(final Logger instance, final String s) {
        instance.error("The resource pack download thread has been interrupted!");
    }

}
