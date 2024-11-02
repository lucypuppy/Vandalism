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

package de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.impl;

import de.nekosarekawaii.vandalism.integration.serverdiscovery.api.response.Response;

import java.util.List;

public class WhereIsResponse extends Response {

    public static class Record {

        public String server;
        public String uuid;
        public String name;
        public Integer last_seen;

        public Record() {
            this.server = "";
            this.uuid = "";
            this.name = "";
            this.last_seen = 0;
        }

        @Override
        public String toString() {
            return "Record{" +
                    "server='" + this.server + '\'' +
                    ", uuid='" + this.uuid + '\'' +
                    ", name='" + this.name + '\'' +
                    ", last_seen=" + this.last_seen +
                    '}';
        }

    }

    public List<Record> data;

    public boolean isError() {
        return this.error != null;
    }

    @Override
    public String toString() {
        return "WhereIsResponse{" +
                "error='" + this.error + '\'' +
                ", data=" + this.data +
                '}';
    }

}