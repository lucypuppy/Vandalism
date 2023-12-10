package de.vandalismdevelopment.vandalism.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.UUID;

public class PlayerSkinRenderer {

    private final PlayerListEntry playerListEntry;
    private int glId = -1;

    public PlayerSkinRenderer(final UUID uuid, final String username) {
        this.playerListEntry = new PlayerListEntry(new GameProfile(uuid, username), true);
    }

    public int getGlId() {
        if (glId == -1) {
            final var skinTexture = this.playerListEntry.getSkinTextures().texture();
            if (skinTexture != null) {
                glId = MinecraftClient.getInstance().getTextureManager().getTexture(skinTexture).getGlId();
            }
        }
        return glId;
    }

}
