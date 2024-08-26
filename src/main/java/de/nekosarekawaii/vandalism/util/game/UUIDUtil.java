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

package de.nekosarekawaii.vandalism.util.game;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UUIDUtil {

    private static final HttpClient REQUESTER = HttpClient.newHttpClient();

    public static String getUUIDFromName(final String name) throws Exception {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                .GET()
                .build();
        final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
        final String mojangApiContent = response.body();
        if (!mojangApiContent.isBlank() && mojangApiContent.contains("\"id\" : \"")) {
            return mojangApiContent.split("\"id\" : \"")[1].split("\",")[0].replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            );
        } else {
            throw new Exception("Couldn't get UUID from name: " + name);
        }
    }

}
