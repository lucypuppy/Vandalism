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

package de.nekosarekawaii.vandalism.integration.serverdiscovery.data.request.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.data.request.Request;
import de.nekosarekawaii.vandalism.integration.serverdiscovery.data.response.impl.ServersResponse;

public class ServersRequest extends Request<ServersResponse> {

    public int asn;
    public String country_code;
    public boolean cracked;
    public String description;

    private JsonArray max_players;
    private JsonArray online_players;

    public int online_after;
    public int protocol;
    public boolean ignore_modded;
    public boolean only_bungeespoofable;
    public Software software;

    public ServersRequest(final int asn, final String country_code, final boolean cracked, final String description, final Software software, final int min_players, final int max_players, final int min_online_players, final int max_online_players, final int online_after, final int protocol, final boolean ignore_modded, final boolean only_bungeespoofable) {
        super(ServersResponse.class, "servers");
        this.asn = asn;
        this.country_code = country_code;
        this.cracked = cracked;
        this.description = description;
        this.software = software;
        this.setMaxPlayers(min_players, max_players);
        this.setOnlinePlayers(min_online_players, max_online_players);
        this.online_after = online_after;
        this.protocol = protocol;
        this.ignore_modded = ignore_modded;
        this.only_bungeespoofable = only_bungeespoofable;
    }

    public void setMaxPlayers(final int min_players, final int max_players) {
        this.max_players = new JsonArray();
        this.max_players.add(min_players);
        if (max_players == -1) {
            this.max_players.add("inf");
        }
        else {
            this.max_players.add(max_players);
        }
    }

    public void setOnlinePlayers(final int min_online_players) {
        this.setOnlinePlayers(min_online_players, -1);
    }

    public void setOnlinePlayers(final int min_online_players, final int max_online_players) {
        this.online_players = new JsonArray();
        this.online_players.add(min_online_players);
        if (max_online_players == -1) {
            this.online_players.add("inf");
        }
        else {
            this.online_players.add(max_online_players);
        }
    }

    public enum Software {
        ANY,
        BUKKIT,
        SPIGOT,
        PAPER,
        VANILLA;

        private final String name;

        Software() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        public String getName() {
            return this.name;
        }

    }

    @Override
    public String toString() {
        return "ServersRequest{" +
                "asn=" + this.asn +
                ", country_code='" + this.country_code + '\'' +
                ", cracked=" + this.cracked +
                ", description='" + this.description + '\'' +
                ", max_players=" + this.max_players +
                ", online_after=" + this.online_after +
                ", online_players=" + this.online_players +
                ", protocol=" + this.protocol +
                ", ignore_modded=" + this.ignore_modded +
                ", only_bungeespoofable=" + this.only_bungeespoofable +
                ", software=" + this.software +
                '}';
    }

    @Override
    protected JsonObject asJson(final JsonObject jsonObject) {
        jsonObject.addProperty("asn", this.asn);
        jsonObject.addProperty("country_code", this.country_code);
        jsonObject.addProperty("cracked", this.cracked);
        jsonObject.addProperty("description", this.description);
        jsonObject.add("max_players", this.max_players);
        jsonObject.addProperty("online_after", this.online_after);
        jsonObject.add("online_players", this.online_players);
        jsonObject.addProperty("protocol", this.protocol);
        jsonObject.addProperty("ignore_modded", this.ignore_modded);
        jsonObject.addProperty("only_bungeespoofable", this.only_bungeespoofable);
        jsonObject.addProperty("software", this.software.name().toLowerCase());
        return jsonObject;
    }

}