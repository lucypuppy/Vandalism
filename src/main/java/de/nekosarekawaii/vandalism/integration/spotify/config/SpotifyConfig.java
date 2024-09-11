/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.integration.spotify.config;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyManager;
import de.nekosarekawaii.vandalism.util.encryption.AESEncryptionUtil;

public class SpotifyConfig extends AbstractConfig<JsonObject> {

    private final SpotifyManager spotifyManager;

    public SpotifyConfig(final SpotifyManager spotifyManager) {
        super(JsonObject.class, "spotify");
        this.spotifyManager = spotifyManager;
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        try {
            final String key = System.getProperty("user.name");
            mainNode.addProperty(
                    "clientId",
                    AESEncryptionUtil.encrypt(
                            key,
                            this.spotifyManager.getClientId()
                    )
            );
            mainNode.addProperty(
                    "clientSecret",
                    AESEncryptionUtil.encrypt(
                            key,
                            this.spotifyManager.getClientSecret()
                    )
            );
            mainNode.addProperty(
                    "refreshToken",
                    AESEncryptionUtil.encrypt(
                            key,
                            this.spotifyManager.getRefreshToken()
                    )
            );
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to save Spotify config.", e);
        }
        return mainNode;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        try {
            final String key = System.getProperty("user.name");
            String clientId = "";
            if (mainNode.has("clientId")) {
                clientId = AESEncryptionUtil.decrypt(key, mainNode.get("clientId").getAsString());
            }
            if (clientId.isEmpty()) {
                Vandalism.getInstance().getLogger().error("Failed to load Spotify config because the client id is empty.");
                return;
            }
            String clientSecret = "";
            if (mainNode.has("clientSecret")) {
                clientSecret = AESEncryptionUtil.decrypt(key, mainNode.get("clientSecret").getAsString());
            }
            if (clientSecret.isEmpty()) {
                Vandalism.getInstance().getLogger().error("Failed to load Spotify config because the client secret is empty.");
                return;
            }
            String refreshToken = "";
            if (mainNode.has("refreshToken")) {
                refreshToken = AESEncryptionUtil.decrypt(key, mainNode.get("refreshToken").getAsString());
            }
            if (refreshToken.isEmpty()) {
                Vandalism.getInstance().getLogger().error("Failed to load Spotify config because the refresh token is empty.");
                return;
            }
            this.spotifyManager.setClientId(clientId);
            this.spotifyManager.setClientSecret(clientSecret);
            this.spotifyManager.refresh(refreshToken);
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to load Spotify config.", e);
        }
    }

}
