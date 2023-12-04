package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.minecraft.CustomRPConfirmScreen;
import net.minecraft.client.util.NetworkUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.InputStream;

@Mixin(NetworkUtils.class)
public abstract class MixinNetworkUtils {

    @Redirect(method = "method_15303", at = @At(value = "INVOKE", target = "Ljava/io/InputStream;read([B)I"))
    private static int vandalism$moreResourcePacketOptionsSkipDownload(final InputStream inputStream, final byte[] bs) throws IOException {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.moreResourcePackOptions.getValue() && CustomRPConfirmScreen.skipDownload) {
            return -1;
        }
        return inputStream.read(bs);
    }

}
