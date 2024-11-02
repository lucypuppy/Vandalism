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

package de.nekosarekawaii.vandalism.integration.spotify.gui;

import java.util.HashMap;

public class StatusMessages {

    private static final HashMap<Integer, String> STATUS_MESSAGES = new HashMap<>();

    static  {
        STATUS_MESSAGES.put(
                200,
                "OK - The request has succeeded. The client can read the result of the request in the body and the headers of the response."
        );
        STATUS_MESSAGES.put(
                201,
                "Created - The request has been fulfilled and resulted in a new resource being created."
        );
        STATUS_MESSAGES.put(
                202,
                "Accepted - The request has been accepted for processing, but the processing has not been completed."
        );
        STATUS_MESSAGES.put(
                204,
                "No Content - The request has succeeded but returns no message body."
        );
        STATUS_MESSAGES.put(
                304,
                "Not Modified. See Conditional requests."
        );
        STATUS_MESSAGES.put(
                400,
                "Bad Request - The request could not be understood by the server due to malformed syntax. The message body will contain more information; see Response Schema."
        );
        STATUS_MESSAGES.put(
                401,
                "Unauthorized - The request requires user authentication or, if the request included authorization credentials, " +
                        "authorization has been refused for those credentials."
        );
        STATUS_MESSAGES.put(
                403,
                "Forbidden - The server understood the request, but is refusing to fulfill it."
        );
        STATUS_MESSAGES.put(
                404,
                "Not Found - The requested resource could not be found. This error can be due to a temporary or permanent condition."
        );
        STATUS_MESSAGES.put(
                411,
                "Length Required - The server refuses to accept the request without a defined Content-Length header. " +
                        "Include the Content-Length header in your request to specify the size of the message body."
        );
        STATUS_MESSAGES.put(
                429,
                "Too Many Requests - Rate limiting has been applied.");
        STATUS_MESSAGES.put(
                500,
                "Internal Server Error. You should never receive this error because our clever coders catch them all ... " +
                        "but if you are unlucky enough to get one, please report it to us through a comment at the bottom of this page."
        );
        STATUS_MESSAGES.put(
                502,
                "Bad Gateway - The server was acting as a gateway or proxy and received an invalid response from the upstream server."
        );
        STATUS_MESSAGES.put(
                503,
                "Service Unavailable - The server is currently unable to handle the request due to a temporary condition which will be alleviated after some delay. " +
                        "You can choose to resend the request again."
        );
    }

    public static String getMessage(final int code) {
        if (!STATUS_MESSAGES.containsKey(code)) {
            return "Unknown status code";
        }
        return STATUS_MESSAGES.get(code);
    }

}
