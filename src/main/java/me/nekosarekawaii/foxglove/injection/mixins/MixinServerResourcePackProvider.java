package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.gui.screen.CustomResourcePackConfirmScreen;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.util.ProgressListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerResourcePackProvider.class)
public abstract class MixinServerResourcePackProvider {

    @Redirect(method = "download", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/NetworkUtils;downloadResourcePack(Ljava/io/File;Ljava/net/URL;Ljava/util/Map;ILnet/minecraft/util/ProgressListener;Ljava/net/Proxy;)Ljava/util/concurrent/CompletableFuture;"
    ))
    private CompletableFuture<?> redirectDownloadResourcePack(final File file, final URL url, final Map<String, String> headers, final int maxFileSize, final @Nullable ProgressListener progressListener, final Proxy proxy) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().resourcePackSpoof.getValue() && CustomResourcePackConfirmScreen.skipDownload) {
            return NetworkUtils.downloadResourcePack(file, url, headers, maxFileSize, null, proxy);
        }
        return NetworkUtils.downloadResourcePack(file, url, headers, maxFileSize, progressListener, proxy);
    }

    @Inject(method = "verifyFile", at = @At("HEAD"), cancellable = true)
    private void injectVerifyFile(final String expectedSha1, final File file, final CallbackInfoReturnable<Boolean> cir) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().resourcePackSpoof.getValue() && CustomResourcePackConfirmScreen.skipDownload) {
            cir.setReturnValue(true);
        }
    }

}
