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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class NameGenerationUtil {

    private static final List<String> USERNAME_PARTS = new ArrayList<>();
    private static final int MAX_USERNAME_PARTS = 1000;

    public static void loadUsernameParts() {
        final String usernameFileUrl = "https://raw.githubusercontent.com/NekosAreKawaii/NameList/main/usernames";
        try (final HttpClient client = HttpClient.newHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(usernameFileUrl)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(body -> {
                final List<String> usernameParts = Arrays.asList(body.split("\n"));
                Collections.shuffle(usernameParts);
                USERNAME_PARTS.clear();
                USERNAME_PARTS.addAll(usernameParts.subList(0, Math.min(usernameParts.size(), MAX_USERNAME_PARTS)));
            }).join();
            Vandalism.getInstance().getLogger().info("Loaded {} username parts.", USERNAME_PARTS.size());
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to load username parts: {}", e.toString());
        }
    }

    public static String generateUsername() {
        if (USERNAME_PARTS.isEmpty()) {
            return RandomUtils.randomString(MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH, true, true, true, false);
        }
        Collections.shuffle(USERNAME_PARTS);
        final Random random = ThreadLocalRandom.current();
        String username = USERNAME_PARTS.get(random.nextInt(USERNAME_PARTS.size()));
        username += USERNAME_PARTS.get(random.nextInt(USERNAME_PARTS.size()));
        if (username.length() > MinecraftConstants.MAX_USERNAME_LENGTH) {
            username = username.substring(0, MinecraftConstants.MAX_USERNAME_LENGTH);
        }
        return username;
    }

}
