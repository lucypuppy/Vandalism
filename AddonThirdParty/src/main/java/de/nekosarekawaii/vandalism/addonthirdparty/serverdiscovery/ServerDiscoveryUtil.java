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

package de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.request.Request;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.request.impl.UserInfoRequest;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.Response;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.api.response.impl.UserInfoResponse;
import net.minecraft.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerDiscoveryUtil {

    private static final String API_KEY = "jRqj4pHMt5OFtGHpmxfdQIULrUqvggJ0";

    public static Response request(final Request<?> request) {
        Response response = request.send(API_KEY);
        if (response.error != null) {
            Vandalism.getInstance().getLogger().error("Error while sending request: " + response.error);
        }
        return response;
    }

}
