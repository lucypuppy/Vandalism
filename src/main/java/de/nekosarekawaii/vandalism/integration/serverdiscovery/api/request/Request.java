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

package de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public abstract class Request<R extends Response> {

    private static final String URL = "https://api.serverseeker.net/";

    private static final Gson GSON = new Gson();

    private final Class<R> responseClass;

    private final String endpoint;

    public Request(final Class<R> responseClass, final String endpoint) {
        this.responseClass = responseClass;
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    protected JsonObject asJson(final JsonObject jsonObject) {
        return jsonObject;
    }

    public R send(final String apiKey) {
        final HttpClient client = HttpClient.newHttpClient();
        try {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("api_key", apiKey);
            return GSON.fromJson(client.send(
                    HttpRequest.newBuilder().uri(URI.create(URL + this.endpoint))
                    .POST(HttpRequest.BodyPublishers.ofString(this.asJson(jsonObject).toString()))
                    .header("Content-Type", "application/json")
                    .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body(),
                    this.responseClass
            );
        } catch (IOException | InterruptedException e) {
            Vandalism.getInstance().getLogger().error("Server Seeker request failed.", e);
            return null;
        }
    }

}
