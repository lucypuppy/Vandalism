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

package de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.data.request.impl;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.data.response.impl.WhereIsResponse;
import de.nekosarekawaii.vandalism.addonthirdparty.serverdiscovery.data.request.Request;

import java.util.UUID;

public class WhereIsRequest extends Request<WhereIsResponse> {

    private final PlayerSearchType playerSearchType;
    private final String playerSearchValue;

    public WhereIsRequest(final String name) {
        super(WhereIsResponse.class, "whereis");
        this.playerSearchType = PlayerSearchType.NAME;
        this.playerSearchValue = name;
    }

    public WhereIsRequest(final UUID uuid) {
        super(WhereIsResponse.class, "whereis");
        this.playerSearchType = PlayerSearchType.UUID;
        this.playerSearchValue = uuid.toString();
    }

    private enum PlayerSearchType {
        NAME, UUID
    }

    @Override
    public String toString() {
        return "WhereIsRequest{" +
                "playerSearchType=" + this.playerSearchType +
                ", playerSearchValue='" + this.playerSearchValue + '\'' +
                '}';
    }

    @Override
    protected JsonObject asJson(final JsonObject jsonObject) {
        jsonObject.addProperty(this.playerSearchType.name().toLowerCase(), this.playerSearchValue);
        return super.asJson(jsonObject);
    }

}