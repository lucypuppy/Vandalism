package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.integration.CustomRPConfirmScreen;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.util.ServerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ServerResourcePackProvider;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.util.ProgressListener;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Mixin(ServerResourcePackProvider.class)
public abstract class MixinServerResourcePackProvider implements MinecraftWrapper {

    @Redirect(method = "method_4634", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V"))
    private void vandalism$moreResourcePackOptionsDump1(final MinecraftClient instance, final Runnable runnable) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().moreResourcePackOptions.getValue() && CustomRPConfirmScreen.dump) {
            return;
        }
        instance.execute(runnable);
    }

    @Unique
    private void vandalism_zipResourcePackFiles(final File folder, final String currentPath, final ZipOutputStream zos) throws IOException {
        if (folder == null) return;
        final File[] files = folder.listFiles();
        if (files == null) return;
        for (final File file : files) {
            String entryName = currentPath + file.getName();
            if (file.isDirectory()) {
                entryName += "/";
                vandalism_zipResourcePackFiles(file, entryName, zos);
            } else {
                try (final FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(entryName));
                    final byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                } catch (final IOException ioException) {
                    Vandalism.getInstance().getLogger().error("Failed to add zip entry into server resource pack zip!", ioException);
                }
            }
        }
    }

    @Redirect(method = "method_4634", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/ServerResourcePackProvider;loadServerPack(Ljava/io/File;Lnet/minecraft/resource/ResourcePackSource;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> vandalism$moreResourcePackOptionsDump2(final ServerResourcePackProvider instance, final File file, final ResourcePackSource packSource) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().moreResourcePackOptions.getValue() && CustomRPConfirmScreen.dump) {
            final File resourcePackFile = new File(this.mc.getResourcePackDir().toFile(), ServerUtil.getLastServerInfo().address + "-server-resource-pack-" + file.getName());
            try {
                Files.move(file.toPath(), resourcePackFile.toPath());
                final File tempDir = new File(resourcePackFile.getParentFile(), resourcePackFile.getName() + "-temp");
                tempDir.mkdirs();
                try (final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(resourcePackFile))) {
                    ZipEntry entry;
                    while ((entry = zipIn.getNextEntry()) != null) {
                        final File entryFile = new File(tempDir, entry.getName());
                        entryFile.getParentFile().mkdirs();
                        entryFile.createNewFile();
                        try (final FileOutputStream fos = new FileOutputStream(entryFile)) {
                            final byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zipIn.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        } catch (final IOException ioException) {
                            Vandalism.getInstance().getLogger().error("Failed to write entry of server resource pack!", ioException);
                        } finally {
                            zipIn.closeEntry();
                        }
                    }
                } catch (final IOException ioException) {
                    Vandalism.getInstance().getLogger().error("Failed to read server resource pack!", ioException);
                }
                resourcePackFile.delete();
                final File[] dirFiles = tempDir.listFiles();
                if (dirFiles != null && dirFiles.length > 0) {
                    try (final FileOutputStream fos = new FileOutputStream(new File(tempDir.getParentFile(), resourcePackFile.getName() + ".zip")); final ZipOutputStream zos = new ZipOutputStream(fos)) {
                        this.vandalism_zipResourcePackFiles(tempDir, "", zos);
                    } catch (final IOException ioException) {
                        Vandalism.getInstance().getLogger().error("Failed to finish conversion of server resource pack into a resource pack zip!", ioException);
                    }
                } else Vandalism.getInstance().getLogger().error("Empty server resource pack zip!");
                FileUtils.deleteDirectory(tempDir);
            } catch (final IOException ioException) {
                Vandalism.getInstance().getLogger().error("Failed to convert the server resource pack into a resource pack zip!", ioException);
            }
            return CompletableFuture.completedFuture(null);
        }
        return instance.loadServerPack(file, packSource);
    }

    @Redirect(method = "download", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/NetworkUtils;downloadResourcePack(Ljava/io/File;Ljava/net/URL;Ljava/util/Map;ILnet/minecraft/util/ProgressListener;Ljava/net/Proxy;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<?> vandalism$moreResourcePackOptionsSkipDownload1(final File file, final URL url, final Map<String, String> headers, final int maxFileSize, final @Nullable ProgressListener progressListener, final Proxy proxy) {
        final boolean cancelProgressListener = Vandalism.getInstance().getClientSettings().getMenuSettings().moreResourcePackOptions.getValue() && CustomRPConfirmScreen.skipDownload;
        return NetworkUtils.downloadResourcePack(file, url, headers, maxFileSize, cancelProgressListener ? null : progressListener, proxy);
    }

    @Inject(method = "verifyFile", at = @At("HEAD"), cancellable = true)
    private void vandalism$moreResourcePackOptionsSkipDownload2(final String expectedSha1, final File file, final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().moreResourcePackOptions.getValue() && CustomRPConfirmScreen.skipDownload) {
            cir.setReturnValue(true);
        }
    }

}
