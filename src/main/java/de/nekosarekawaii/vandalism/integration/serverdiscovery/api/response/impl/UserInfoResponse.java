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

package de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.impl;

import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.Request;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.impl.ServerInfoRequest;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.impl.ServersRequest;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.request.impl.WhereIsRequest;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.Response;

public class UserInfoResponse extends Response {

    public String discord_id;
    public String discord_username;
    public String discord_avatar_url;
    public Integer requests_per_day_server_info;
    public Integer requests_per_day_servers;
    public Integer requests_per_day_whereis;
    public Integer requests_made_server_info;
    public Integer requests_made_servers;
    public Integer requests_made_whereis;

    public boolean isError() {
        return this.error != null;
    }

    public int getRemainingUses(final Request request) {
        if (request instanceof WhereIsRequest) {
            return this.requests_per_day_whereis - this.requests_made_whereis;
        }
        else if (request instanceof ServersRequest) {
            return this.requests_per_day_servers - this.requests_made_servers;
        }
        else if (request instanceof ServerInfoRequest) {
            return this.requests_per_day_server_info - this.requests_made_server_info;
        }
        return 0;
    }

    public boolean isRateLimited(final Request request) {
        return this.getRemainingUses(request) <= 0;
    }

    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "error='" + this.error + '\'' +
                ", discord_id='" + this.discord_id + '\'' +
                ", discord_username='" + this.discord_username + '\'' +
                ", discord_avatar_url='" + this.discord_avatar_url + '\'' +
                ", requests_per_day_server_info=" + this.requests_per_day_server_info +
                ", requests_per_day_servers=" + this.requests_per_day_servers +
                ", requests_per_day_whereis=" + this.requests_per_day_whereis +
                ", requests_made_server_info=" + this.requests_made_server_info +
                ", requests_made_servers=" + this.requests_made_servers +
                ", requests_made_whereis=" + this.requests_made_whereis +
                '}';
    }

}