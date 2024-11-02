/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.net.httpserver.HttpServer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.feature.hud.HUDManager;
import de.nekosarekawaii.vandalism.integration.spotify.config.SpotifyConfig;
import de.nekosarekawaii.vandalism.integration.spotify.gui.SpotifyClientWindow;
import de.nekosarekawaii.vandalism.integration.spotify.gui.SpotifyData;
import de.nekosarekawaii.vandalism.integration.spotify.gui.StatusMessages;
import de.nekosarekawaii.vandalism.integration.spotify.hud.SpotifyHUDElement;
import de.nekosarekawaii.vandalism.util.MSTimer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Util;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An implementation of <a href="https://developer.spotify.com/documentation/">Spotify</a>.
 */
public class SpotifyManager {

    private static final HttpClient REQUESTER = HttpClient.newHttpClient();

    private static final String REDIRECT_URI_START = "http://127.0.0.1:";
    private static final String REDIRECT_PATH = "/spotify/";

    private boolean isRunning = false;

    private String clientId = "";
    private String clientSecret = "";

    private int httpServerPort = 30562;

    private String accessToken = "";
    private String refreshToken = "";

    private final SpotifyData currentSpotifyData = new SpotifyData();
    private final MSTimer updateTimer = new MSTimer();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public SpotifyManager(final ConfigManager configManager, final HUDManager hudManager, final ClientWindowManager clientWindowManager) {
        configManager.add(new SpotifyConfig(this));
        hudManager.add(new SpotifyHUDElement(this));
        clientWindowManager.add(new SpotifyClientWindow());
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public int getHttpServerPort() {
        return this.httpServerPort;
    }

    public void setHttpServerPort(final int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getRedirectUri() {
        return REDIRECT_URI_START + this.httpServerPort + REDIRECT_PATH;
    }

    private void tryStartHttpServer() {
        if (this.isRunning) {
            return;
        }
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(this.httpServerPort), 0);
            server.createContext(REDIRECT_PATH, exchange -> {
                final String query = exchange.getRequestURI().getQuery();
                final Map<String, String> queryParams = new HashMap<>();
                if (query != null) {
                    for (final String param : query.split("&")) {
                        final String[] entry = param.split("=");
                        if (entry.length > 1) {
                            queryParams.put(entry[0], entry[1]);
                        } else {
                            queryParams.put(entry[0], "");
                        }
                    }
                }
                boolean success = false;
                final String code = queryParams.get("code");
                if (code != null) {
                    try {
                        final HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://accounts.spotify.com/api/token"))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                        "grant_type=authorization_code&code=" + code + "&redirect_uri=" + this.getRedirectUri() + "&client_id=" + this.clientId + "&client_secret=" + this.clientSecret)
                                )
                                .build();
                        final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
                        final int responseCode = response.statusCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            final String responseBody = response.body();
                            if (!responseBody.isEmpty()) {
                                final JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                                if (responseJson.has("access_token") && responseJson.has("refresh_token")) {
                                    this.accessToken = responseJson.get("access_token").getAsString();
                                    this.refreshToken = responseJson.get("refresh_token").getAsString();
                                    success = true;
                                }
                            } else {
                                Vandalism.getInstance().getLogger().error("Spotify access token request returned an empty response.");
                            }
                        } else {
                            Vandalism.getInstance().getLogger().error("Spotify access token request failed with response code:");
                            Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
                        }
                    } catch (Exception e) {
                        Vandalism.getInstance().getLogger().error("Failed to request access token from Spotify.", e);
                    }
                }
                final String response = "Vandalism Spotify Authentication has been " + (success ? "successfully" : "failed") + ".\nYou can close this tab.";
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                final OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                server.stop(0);
                this.isRunning = false;
            });
            server.setExecutor(null);
            server.start();
            this.isRunning = true;
        } catch (IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to create Spotify authentication Http Server.", e);
        }
    }

    public void login() {
        Util.getOperatingSystem().open(
                "https://accounts.spotify.com/authorize?client_id=" + this.clientId + "&response_type=code&redirect_uri=" + this.getRedirectUri() +
                        "&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state"
        );
        this.tryStartHttpServer();
    }

    public void refresh(final String refreshToken) {
        this.refreshToken = refreshToken;
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://accounts.spotify.com/api/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + this.clientId + "&client_secret=" + this.clientSecret))
                    .build();
            final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
            final int responseCode = response.statusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                final String responseBody = response.body();
                if (!responseBody.isEmpty()) {
                    final JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                    if (responseJson.has("access_token")) {
                        this.accessToken = responseJson.get("access_token").getAsString();
                    }
                } else {
                    Vandalism.getInstance().getLogger().error("Spotify refresh request returned an empty response.");
                }
            } else {
                Vandalism.getInstance().getLogger().error("Spotify refresh request failed with response code:");
                Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
            }
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to refresh access token from Spotify.", e);
        }
    }

    public void logout() {
        this.accessToken = "";
        this.refreshToken = "";
    }

    private void execute(final Runnable runnable) {
        if (!this.isLoggedIn()) {
            return;
        }
        this.executor.submit(runnable);
    }

    public void previous() {
        this.execute(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.spotify.com/v1/me/player/previous"))
                        .header("Authorization", "Bearer " + this.accessToken)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
                final int responseCode = response.statusCode();
                if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                    Vandalism.getInstance().getLogger().error("Spotify previous request failed with response code:");
                    Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
                }
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to request previous from Spotify.", e);
            }
        });
    }

    public void play() {
        this.execute(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.spotify.com/v1/me/player/play"))
                        .header("Authorization", "Bearer " + this.accessToken)
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();
                final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
                final int responseCode = response.statusCode();
                if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                    Vandalism.getInstance().getLogger().error("Spotify play request failed with response code:");
                    Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
                }
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to request play from Spotify.", e);
            }
        });
    }

    public void pause() {
        this.execute(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.spotify.com/v1/me/player/pause"))
                        .header("Authorization", "Bearer " + this.accessToken)
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();
                final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
                final int responseCode = response.statusCode();
                if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                    Vandalism.getInstance().getLogger().error("Spotify pause request failed with response code:");
                    Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
                }
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to request pause from Spotify.", e);
            }
        });
    }

    public void next() {
        this.execute(() -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.spotify.com/v1/me/player/next"))
                        .header("Authorization", "Bearer " + this.accessToken)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
                final int responseCode = response.statusCode();
                if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                    Vandalism.getInstance().getLogger().error("Spotify next request failed with response code:");
                    Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
                }
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to request next from Spotify.", e);
            }
        });
    }

    public void update() {
        if (this.updateTimer.hasReached(10000, true)) {
            this.execute(() -> {
                try {
                    final HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://api.spotify.com/v1/me/player/currently-playing"))
                            .header("Authorization", "Bearer " + this.accessToken)
                            .headers("Accept", "application/json")
                            .GET()
                            .build();
                    final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
                    final int responseCode = response.statusCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        final String responseBody = response.body();
                        if (!responseBody.isEmpty()) {
                            final JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                            if (responseJson.has("currently_playing_type")) {
                                String type = responseJson.get("currently_playing_type").getAsString();
                                if (!type.isEmpty()) {
                                    type = type.substring(0, 1).toUpperCase() + type.substring(1);
                                }
                                this.currentSpotifyData.setType(type);
                            }
                            if (responseJson.has("item")) {
                                final JsonObject itemJson = responseJson.getAsJsonObject("item");
                                if (itemJson.has("name")) {
                                    this.currentSpotifyData.setName(itemJson.get("name").getAsString());
                                }
                                if (itemJson.has("duration_ms")) {
                                    this.currentSpotifyData.setDuration(itemJson.get("duration_ms").getAsLong());
                                }
                                if (itemJson.has("artists")) {
                                    final JsonArray artistsArray = itemJson.getAsJsonArray("artists");
                                    StringBuilder artists = new StringBuilder();
                                    for (final JsonElement artistElement : artistsArray) {
                                        final JsonObject artistJson = artistElement.getAsJsonObject();
                                        if (artistJson.has("name")) {
                                            artists.append(artistJson.get("name").getAsString()).append(", ");
                                        }
                                    }
                                    if (artists.toString().endsWith(", ")) {
                                        artists = new StringBuilder(artists.substring(0, artists.length() - 2));
                                    }
                                    this.currentSpotifyData.setArtists(artists.toString());
                                }
                                if (itemJson.has("album")) {
                                    final JsonObject albumJson = itemJson.getAsJsonObject("album");
                                    if (albumJson.has("images")) {
                                        final JsonArray imagesArray = albumJson.getAsJsonArray("images");
                                        if (!imagesArray.isEmpty()) {
                                            final JsonObject imageJson = imagesArray.get(1).getAsJsonObject();
                                            if (imageJson.has("url")) {
                                                final String imageUrl = imageJson.get("url").getAsString();
                                                if (!this.currentSpotifyData.getImageUrl().equals(imageUrl)) {
                                                    this.currentSpotifyData.setImageUrl(imageUrl);
                                                    try (final InputStream inputStream = new URL(imageUrl).openStream()) {
                                                        final ByteArrayOutputStream out = new ByteArrayOutputStream();
                                                        ImageIO.write(ImageIO.read(inputStream), "png", out);
                                                        final NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(out.toByteArray()));
                                                        out.close();
                                                        RenderSystem.recordRenderCall(() -> {
                                                            final NativeImageBackedTexture image = new NativeImageBackedTexture(nativeImage);
                                                            MinecraftClient.getInstance().getTextureManager().registerTexture(SpotifyData.IMAGE_IDENTIFIER, image);
                                                            this.currentSpotifyData.setImage(image);
                                                        });
                                                    } catch (IOException e) {
                                                        Vandalism.getInstance().getLogger().error("Failed to load Spotify track image.", e);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (responseJson.has("is_playing")) {
                                this.currentSpotifyData.setPaused(!responseJson.get("is_playing").getAsBoolean());
                            }
                            if (!this.currentSpotifyData.isPaused()) {
                                if (responseJson.has("progress_ms")) {
                                    this.currentSpotifyData.setTime(System.currentTimeMillis());
                                    this.currentSpotifyData.setProgress(responseJson.get("progress_ms").getAsLong() - 2000);
                                }
                            }
                        } else {
                            Vandalism.getInstance().getLogger().error("Spotify data request returned an empty response.");
                        }
                    } else {
                        if (responseCode == 401) {
                            Vandalism.getInstance().getLogger().info("Refreshing Spotify access token...");
                            this.refresh(this.refreshToken);
                        } else if (responseCode != 204) {
                            Vandalism.getInstance().getLogger().error("Spotify data request failed with response code:");
                            Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
                        }
                    }
                } catch (Exception e) {
                    Vandalism.getInstance().getLogger().error("Failed to request data from Spotify.", e);
                }
            });
        }
    }

    public boolean isLoggedIn() {
        return this.accessToken != null && !this.accessToken.isEmpty();
    }

    public SpotifyData getCurrentSpotifyData() {
        return this.currentSpotifyData;
    }

}
