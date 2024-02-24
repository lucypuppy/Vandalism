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

    private static final List<String> API_KEYS = Arrays.asList(
            "jRqj4pHMt5OFtGHpmxfdQIULrUqvggJ0",
            "aSRu3ELiphaDCNqZhgUWGIV8kDrXQN0v",
            "gMcoEdcuPIkVS1gqYd25o9K7isa1vx6Z",
            "DHp5vQdo830StKzIj3TLYyOO4qtMX5k4",
            "kWsTEfpfiuop1AlHvDni9aS5T4jrM3KS",
            "w79gTBAAHjbqOdQH2JXAGI1ozaaOd2E6",
            "wkqnyRjaYnM4bWNLqiLwhht0O6FhFFoR",
            "420Pc08fotEeefrh7LIRS5qWARFguCud",
            "CS5n9CTKzr9jxWPxj6UJqfQ1xFpmHStr",
            "vq9doRARAWFmBM4Zy4WFaJA5MLL57I3N",
            "vjoDCGE2xGXv3sw89WvE1bGvTQQu2Rbb",
            "1cAVHTO5R6jmUDglwmRVb2mGPrckaHBN",
            "0Ie1I581C8zOKA6aGlg01ZlaMyo8ySis",
            "kmXDZBeuPyEvOknkOMfQtFgLG7JoiCfL",
            "FDYqj5G7t1h0yRDbHLYtD7SCjLQ1FsTP",
            "QyANs82byCOc1iNj5w76PC522vIlkIdl",
            "1l3h7ZfXy65I8Zihx620OmGfFyRmsYli",
            "dGbnfLth9NrkOtRwHqLAEOpwe7epDZK5",
            "ReJuM1cqWB8ahb4eRxXOVoDWiFSDJY6x",
            "9t4HeYeS0y98PaeTBfBcjcBkHqgS7zVw",
            "5cPO84dD3Ykg2uEtNzdBUiV4qNikImiE",
            "hZVgXUmFYs1Mhf5z5u8Mx7g4JVn3VBhq",
            "iNPZPuzKD7mZMFycrIrM9h3gdzSMhZaz",
            "A57gkawNFszFYFmLOxl1zJnTycfA7aIE",
            "MuWd838WYwYbF9e0GTly8tEUBEbgT53D",
            "anJXakSBgDHx6LQLgZQsxfejppiUZjUC",
            "r5jJaFMBzlw9c6PmxMlDyl3queWRlcAx",
            "fWevQRLqntGCdHSB9R2oGd1JnGeZZDmy",
            "JDLIyBr601gLx9ITDb5IJPLQ9wIUClyZ"
    );

    public static Response request(final Request<?> request) {
        Collections.shuffle(API_KEYS);
        for (final String apiKey : API_KEYS) {
            final Pair<Boolean, UserInfoResponse> check = isApiKeyValid(request, apiKey);
            if (check.getLeft()) {
                final UserInfoResponse userInfoResponse = check.getRight();
                final int left = userInfoResponse.getRemainingUses(request);
                Vandalism.getInstance().getLogger().info("Successfully selected Server Seeker API User " + userInfoResponse.discord_username + " (left: " + left + ") -> " + request.getEndpoint());
                return request.send(apiKey);
            }
        }
        return null;
    }

    private static Pair<Boolean, UserInfoResponse> isApiKeyValid(final Request request, final String apiKey) {
        final UserInfoResponse userInfoResponse = new UserInfoRequest().send(apiKey);
        if (userInfoResponse.isError()) {
            Vandalism.getInstance().getLogger().error("Server Seeker API Key " + apiKey + " is invalid due to: " + userInfoResponse.error);
            return new Pair<>(false, null);
        }
        if (userInfoResponse.isRateLimited(request)) {
            Vandalism.getInstance().getLogger().error("Server Seeker API User " + userInfoResponse.discord_username + " is rate limited -> " + request.getEndpoint());
            return new Pair<>(false, null);
        }
        return new Pair<>(true, userInfoResponse);
    }

}
