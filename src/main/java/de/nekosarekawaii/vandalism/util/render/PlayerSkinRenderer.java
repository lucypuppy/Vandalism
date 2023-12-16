package de.nekosarekawaii.vandalism.util.render;

import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerSkinRenderer {

    private static final ExecutorService SKIN_LOADER = Executors.newSingleThreadExecutor();

    private int glId;

    public PlayerSkinRenderer(final UUID uuid) {
        this.glId = -1;
        CompletableFuture.supplyAsync(() -> {
            final ProfileResult result = MinecraftClient.getInstance().getSessionService().fetchProfile(uuid, false);
            if (result == null) return null;
            return result.profile();
        }, SKIN_LOADER).thenComposeAsync(profile -> {
            if (profile == null) return CompletableFuture.completedFuture(DefaultSkinHelper.getSkinTextures(uuid));
            return MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(profile);
        }, MinecraftClient.getInstance()).thenAcceptAsync(skin -> this.glId = RenderUtil.getGlId(skin.texture()), MinecraftClient.getInstance());
    }

    public int getGlId() {
        return this.glId;
    }

}
