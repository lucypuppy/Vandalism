/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import com.sun.net.httpserver.HttpServer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.integration.hud.HUDManager;
import de.nekosarekawaii.vandalism.integration.spotify.config.SpotifyConfig;
import de.nekosarekawaii.vandalism.integration.spotify.gui.SpotifyClientMenuWindow;
import de.nekosarekawaii.vandalism.integration.spotify.hud.SpotifyHUDElement;
import net.minecraft.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of <a href="https://developer.spotify.com/documentation/">Spotify</a>.
 */
public class SpotifyManager {

    private static final int REDIRECT_PORT = 30562;
    private static final String REDIRECT_PATH = "/spotify/";
    private static final String REDIRECT_URI = "http://127.0.0.1:" + REDIRECT_PORT + REDIRECT_PATH;

    private String clientId = "";
    private String clientSecret = "";
    private String accessToken = "";
    private String refreshToken = "";

    private final SpotifyTrack spotifyTrack = new SpotifyTrack();

    private Thread executor = null;

    public SpotifyManager(final ConfigManager configManager, final ClientMenuManager clientMenuManager, final HUDManager hudManager) {
        configManager.add(new SpotifyConfig(this));
        clientMenuManager.add(new SpotifyClientMenuWindow(this));
        hudManager.add(new SpotifyHUDElement(this));
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

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void login() {
        try {
            Util.getOperatingSystem().open(
                    "https://accounts.spotify.com/authorize?client_id=" + this.clientId + "&response_type=code&redirect_uri=" + REDIRECT_URI +
                            "&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state"
            );
            final HttpServer server = HttpServer.create(new InetSocketAddress(REDIRECT_PORT), 0);
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
                        final HttpURLConnection connection = (HttpURLConnection) new URL("https://accounts.spotify.com/api/token").openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setDoOutput(true);
                        try (final OutputStream os = connection.getOutputStream()) {
                            final byte[] input = (
                                    "grant_type=authorization_code" +
                                            "&code=" + code +
                                            "&redirect_uri=" + REDIRECT_URI +
                                            "&client_id=" + this.clientId +
                                            "&client_secret=" + this.clientSecret
                            ).getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        try (final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                            final StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            final JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                            if (responseJson.has("access_token") && responseJson.has("refresh_token")) {
                                this.accessToken = responseJson.get("access_token").getAsString();
                                this.refreshToken = responseJson.get("refresh_token").getAsString();
                                success = true;
                            }
                        }
                    } catch (IOException e) {
                        Vandalism.getInstance().getLogger().error("Failed to request access token from Spotify.", e);
                    }
                }
                final String response = "Vandalism Spotify Authentication has been " + (success ? "successfully" : "failed") + ".\nYou can close this tab.";
                exchange.sendResponseHeaders(200, response.length());
                final OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                server.stop(0);
            });
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to open Spotify authentication URL.", e);
        }
    }

    public void refresh(final String refreshToken) {
        this.refreshToken = refreshToken;
        try {
            final String tokenEndpoint = "https://accounts.spotify.com/api/token";
            final String payload = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + this.clientId + "&client_secret=" + this.clientSecret;
            final URL url = new URL(tokenEndpoint);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            try (final OutputStream os = connection.getOutputStream()) {
                final byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                final StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                final JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                if (responseJson.has("access_token")) {
                    this.accessToken = responseJson.get("access_token").getAsString();
                }
            }
        } catch (IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to refresh access token from Spotify.", e);
        }
    }

    public void logout() {
        this.accessToken = "";
        this.refreshToken = "";
    }

    public void requestData() {
        if (!this.isLoggedIn()) {
            return;
        }
        if (this.executor != null && this.executor.isAlive()) {
            return;
        }
        this.executor = new Thread(() -> {
            try {
                final String url = "https://api.spotify.com/v1/me/player/currently-playing";
                final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + this.accessToken);
                connection.setRequestProperty("Accept", "application/json");
                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        final StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        if (!response.isEmpty()) {
                            final JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                            if (responseJson.has("currently_playing_type")) {
                                this.spotifyTrack.setType(responseJson.get("currently_playing_type").getAsString());
                            }
                            if (responseJson.has("item")) {
                                final JsonObject itemJson = responseJson.getAsJsonObject("item");
                                if (itemJson.has("name")) {
                                    this.spotifyTrack.setName(itemJson.get("name").getAsString());
                                }
                                if (itemJson.has("duration_ms")) {
                                    this.spotifyTrack.setDuration(itemJson.get("duration_ms").getAsLong());
                                }
                                if (itemJson.has("artists")) {
                                    this.spotifyTrack.getArtists().clear();
                                    final JsonArray artistsArray = itemJson.getAsJsonArray("artists");
                                    for (final JsonElement artistElement : artistsArray) {
                                        final JsonObject artistJson = artistElement.getAsJsonObject();
                                        if (artistJson.has("name")) {
                                            this.spotifyTrack.getArtists().add(artistJson.get("name").getAsString());
                                        }
                                    }
                                }
                            }
                            if (responseJson.has("is_playing")) {
                                this.spotifyTrack.setPaused(!responseJson.get("is_playing").getAsBoolean());
                            }
                            if (!this.spotifyTrack.isPaused()) {
                                if (responseJson.has("progress_ms")) {
                                    final long currentMs = System.currentTimeMillis();
                                    final long progressMs = responseJson.get("progress_ms").getAsLong() - 2000;
                                    this.spotifyTrack.setTime(currentMs);
                                    this.spotifyTrack.setProgress(progressMs);
                                }
                            }
                        }
                        else {
                            Vandalism.getInstance().getLogger().error("Spotify data request returned an empty response.");
                        }
                    }
                } else {
                    if (responseCode == 401) {
                        Vandalism.getInstance().getLogger().info("Refreshing Spotify access token...");
                        this.refresh(this.refreshToken);
                    }
                    else {
                        Vandalism.getInstance().getLogger().error("Spotify data request failed with response code:");
                        Vandalism.getInstance().getLogger().error(responseCode + " -> " + StatusMessages.getMessage(responseCode));
                    }
                }
            } catch (IOException e) {
                Vandalism.getInstance().getLogger().error("Failed to request data from Spotify.", e);
            }
            this.executor = null;
        });
        this.executor.start();
    }

    public boolean isLoggedIn() {
        return this.accessToken != null && !this.accessToken.isEmpty();
    }

    public SpotifyTrack getCurrentPlaying() {
        return this.spotifyTrack;
    }

}
