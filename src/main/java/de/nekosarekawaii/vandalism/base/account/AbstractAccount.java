/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.base.account;

import com.google.gson.JsonObject;
import com.mojang.authlib.Environment;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.internal.UpdateSessionListener;
import de.nekosarekawaii.vandalism.util.common.TimeFormatter;
import de.nekosarekawaii.vandalism.util.encryption.AESEncryptionUtil;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.PlayerSkinRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAccount implements MinecraftWrapper {

    private final String type;

    protected Session session;
    protected String status;
    protected PlayerSkinRenderer playerSkin;

    private String lastLogin;

    public AbstractAccount(final String type) {
        this.type = type;
    }

    public abstract void logIn0() throws Throwable;
    
    public abstract void save0(final JsonObject mainNode) throws Throwable;

    public abstract void load0(final JsonObject mainNode) throws Throwable;

    public abstract String getDisplayName();

    public Environment getEnvironment() {
        return YggdrasilEnvironment.PROD.getEnvironment();
    }

    public void save(final JsonObject mainNode) throws Throwable {
        if (this.session != null) {
            final JsonObject sessionNode = new JsonObject();
            this.saveSession(sessionNode);
            mainNode.add("session", sessionNode);
        }
        if (this.lastLogin != null) {
            mainNode.addProperty("lastLogin", this.lastLogin);
        }
        this.save0(mainNode);
    }

    public void load(final JsonObject mainNode) throws Throwable {
        if (mainNode.has("session")) {
            this.updateSession(this.loadSession(mainNode.get("session").getAsJsonObject()));
        }
        if (mainNode.has("lastLogin")) {
            this.lastLogin = mainNode.get("lastLogin").getAsString();
        }
        this.load0(mainNode);
    }

    public abstract AccountFactory factory();

    public PlayerSkinRenderer getPlayerSkin() {
        return this.playerSkin;
    }

    public String getLastLogin() {
        return this.lastLogin;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Session getSession() {
        return this.session;
    }

    public void updateSession(final Session session) {
        final UpdateSessionListener.UpdateSessionEvent event = new UpdateSessionListener.UpdateSessionEvent(this.session, session);
        Vandalism.getInstance().getEventSystem().postInternal(UpdateSessionListener.UpdateSessionEvent.ID, event);
        this.session = event.newSession; // Allow the event to change the session
        this.playerSkin = new PlayerSkinRenderer(session.getUuidOrNull());
        this.lastLogin = TimeFormatter.currentDateTime();
    }

    public String getType() {
        return this.type;
    }

    public void logIn() {
        CompletableFuture.runAsync(() -> {
            try {
                this.logIn0(); // Set the game session and reload the skins
                if (reloadProfileKeys(this.session, getEnvironment())) {
                    this.setStatus("Updated session and logged in");
                } else {
                    this.setStatus("Failed to update UserApiService");
                }
                Vandalism.getInstance().getAccountManager().setCurrentAccount(this);
                Vandalism.getInstance().getLogger().info("Logged in as " + this.getDisplayName());
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }

    public static boolean reloadProfileKeys(final Session session, final Environment environment) {
        final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(mc.getNetworkProxy(), environment);
        mc.session = session; // We already did that in normal cases, but for people who use this as API we need to do it again
        mc.getSplashTextLoader().session = session; // We will never know, right?
        mc.sessionService = authenticationService.createMinecraftSessionService();
        if (session.getAccountType().equals(Session.AccountType.MSA)) {
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
            return userApiService != UserApiService.OFFLINE;
        } else {
            // Legacy account types doesn't need reloading the profile since we can't get the required fields (access token) for it
            // So we just skip it
            return true;
        }
    }

    public void saveSession(final JsonObject node) throws Throwable {
        final String username = this.session.getUsername();
        node.addProperty("username", username);
        if (this.session.getUuidOrNull() != null) {
            node.addProperty("uuid", this.session.getUuidOrNull().toString());
        }
        node.addProperty("accessToken", AESEncryptionUtil.encrypt(username, this.session.getAccessToken()));
        this.session.getXuid().ifPresent(s -> node.addProperty("xuid", s));
        this.session.getClientId().ifPresent(s -> node.addProperty("clientId", s));
        node.addProperty("accountType", this.session.getAccountType().getName());
    }

    public Session loadSession(final JsonObject node) throws Throwable {
        final String username = node.get("username").getAsString();
        final String uuid = node.has("uuid") ? node.get("uuid").getAsString() : null;
        final String accessToken = AESEncryptionUtil.decrypt(username, node.get("accessToken").getAsString());
        final Optional<String> xuid = node.has("xuid") ? Optional.of(node.get("xuid").getAsString()) : Optional.empty();
        final Optional<String> clientId = node.has("clientId") ? Optional.of(node.get("clientId").getAsString()) : Optional.empty();
        final String accountType = node.get("accountType").getAsString();
        return new Session(username, uuid == null ? null : UUID.fromString(uuid), accessToken, xuid, clientId, Session.AccountType.byName(accountType));
    }

}
