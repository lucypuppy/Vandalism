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

package de.nekosarekawaii.vandalism.util;

import lombok.Getter;

@Getter
public enum PingState {

    FAILED("There was an error fetching the server info."),
    BIND_FAILED("Cannot assign requested address."),
    UNKNOWN_HOST("Unknown host."),
    CONNECTION_REFUSED("Connection refused."),
    CONNECTION_TIMED_OUT("Connection timed out."),
    DATA_READ_FAILED("Failed to read data."),
    PACKET_READ_FAILED("Failed to read packet."),
    SUCCESS("Successfully fetched the server info."),
    WAITING_RESPONSE("Waiting for response..."),
    WAITING_INPUT("Waiting for input...");

    private final String message;

    PingState(final String message) {
        this.message = message;
    }

}