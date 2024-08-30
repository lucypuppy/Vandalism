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

import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.Response;

import java.util.List;

public class ServerInfoResponse extends Response {

    public Boolean cracked;
    public String description;
    public Integer last_seen;
    public Integer max_players;
    public Integer online_players;
    public Integer protocol;
    public String version;

    public static class Player {

        public String name;
        public String uuid;
        public Integer last_seen;

        @Override
        public String toString() {
            return "Player{" +
                    "name='" + this.name + '\'' +
                    ", uuid='" + this.uuid + '\'' +
                    ", last_seen=" + this.last_seen +
                    '}';
        }

    }

    public List<Player> players;

    public boolean isError() {
        return this.error != null;
    }

    @Override
    public String toString() {
        return "ServerInfoResponse{" +
                "error='" + this.error + '\'' +
                ", cracked=" + this.cracked +
                ", description='" + this.description + '\'' +
                ", last_seen=" + this.last_seen +
                ", max_players=" + this.max_players +
                ", online_players=" + this.online_players +
                ", protocol=" + this.protocol +
                ", version='" + this.version + '\'' +
                ", players=" + this.players +
                '}';
    }

}