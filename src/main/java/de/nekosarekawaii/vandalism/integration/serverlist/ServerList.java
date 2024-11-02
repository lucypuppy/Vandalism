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

package de.nekosarekawaii.vandalism.integration.serverlist;

import lombok.Getter;

@Getter
public class ServerList {

    public static final String DEFAULT_SERVER_LIST_NAME = "Default";
    private static final String DEFAULT_SERVER_LIST_FILE_NAME = "servers";

    private final String name;
    private int size;

    public ServerList() {
        this(DEFAULT_SERVER_LIST_FILE_NAME);
    }

    public ServerList(final String name) {
        this.name = name;
        this.size = 0;
    }

    public ServerList setSize(final int size) {
        this.size = size;
        return this;
    }

    public boolean isDefault() {
        return this.name.equals(DEFAULT_SERVER_LIST_FILE_NAME);
    }

}
