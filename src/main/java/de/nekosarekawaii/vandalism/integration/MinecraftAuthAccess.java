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

package de.nekosarekawaii.vandalism.integration;

import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.util.MicrosoftConstants;

public class MinecraftAuthAccess {

    public static final StepFullJavaSession INGAME_ACCOUNT_SWITCHER = createLocalWebServerLogin("54fd49e4-2103-4044-9603-2b028c814ec3", "XboxLive.signin offline_access", "http://localhost", null);

    public static StepFullJavaSession createLocalWebServerLogin(final String clientId, final String scope, final String redirectUri, final String clientSecret) {
        return MinecraftAuth.builder().withClientId(clientId).withScope(scope).withRedirectUri(redirectUri).withClientSecret(clientSecret).localWebServer()
                .withoutDeviceToken()
                .regularAuthentication(MicrosoftConstants.JAVA_XSTS_RELYING_PARTY)
                .buildMinecraftJavaProfileStep(false);
    }

}
