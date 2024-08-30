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

package de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.impl;


import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.Request;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.impl.ServerInfoResponse;

public class ServerInfoRequest extends Request<ServerInfoResponse> {

    private final String ip;
    private final int port;

    public ServerInfoRequest(final String ip, final int port) {
        super(ServerInfoResponse.class, "server_info");
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return "ServerInfoRequest{" +
                "ip='" + this.ip + '\'' +
                ", port=" + this.port +
                '}';
    }

    @Override
    protected JsonObject asJson(final JsonObject jsonObject) {
        jsonObject.addProperty("ip", this.ip);
        jsonObject.addProperty("port", this.port);
        return jsonObject;
    }

}