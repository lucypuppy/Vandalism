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

package de.nekosarekawaii.vandalism.util;

import com.mojang.authlib.Environment;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.internal.UpdateSessionListener;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionUtil implements MinecraftWrapper {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private static Session prevSession;

    public static void checkSessionUpdate(final Session session) {
        if (session != prevSession) {
            Vandalism.getInstance().getEventSystem().callExceptionally(UpdateSessionListener.UpdateSessionEvent.ID, new UpdateSessionListener.UpdateSessionEvent(prevSession, session));
            prevSession = session;
        }
    }

    public static Session createSession(final String name, String uuid) {
        if (uuid.isBlank()) {
            try {
                uuid = UUIDUtil.getUUIDFromName(name);
            } catch (final Exception ignored) {
                uuid = Uuids.getOfflinePlayerUuid(name).toString();
            }
        }
        return new Session(name, UUID.fromString(uuid), "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);
    }

    public static void setSessionAsync(final String name, final String uuid) {
        EXECUTOR.submit(() -> {
            mc.session = createSession(name, uuid);
            checkSessionUpdate(mc.session); // Enforce immediate update if possible
        });
    }

    public static boolean reloadProfileKeys(final Session session, final Environment environment) {
        final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(mc.getNetworkProxy(), environment);
        mc.session = session;
        mc.getSplashTextLoader().session = session; // We will never know, right?
        mc.sessionService = authenticationService.createMinecraftSessionService();
        if (!session.getAccountType().equals(Session.AccountType.MSA)) {
            // Legacy account types don't need reloading the profile since we can't get the required fields (access token)
            // for it, So we just skip it
            return true;
        }
        final UserApiService userApiService = authenticationService.createUserApiService(session.getAccessToken());
        mc.userApiService = userApiService;
        mc.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return userApiService.fetchProperties();
            } catch (AuthenticationException e) {
                MinecraftClient.LOGGER.error("Failed to fetch user properties", e);
                return UserApiService.OFFLINE_PROPERTIES;
            }
        }, Util.getDownloadWorkerExecutor());
        mc.socialInteractionsManager = new SocialInteractionsManager(mc, userApiService);
        mc.profileKeys = ProfileKeys.create(userApiService, session, mc.runDirectory.toPath());
        mc.abuseReportContext = AbuseReportContext.create(mc.abuseReportContext != null ? mc.abuseReportContext.environment : ReporterEnvironment.ofIntegratedServer(), userApiService);
        mc.realmsPeriodicCheckers = new RealmsPeriodicCheckers(RealmsClient.createRealmsClient(mc));
        // TODO this needs updating and is missing some storages as well as ban details
        return userApiService != UserApiService.OFFLINE;
    }

}
