package de.vandalismdevelopment.vandalism.config.impl.account;

import com.google.gson.JsonObject;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;

import java.util.UUID;

public abstract class Account implements MinecraftWrapper {

    private final String type;
    private String username;
    private UUID uuid;

    public Account(final String type, final String username, final UUID uuid) {
        this.type = type;
        this.username = username;
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }

    public abstract void login() throws Throwable;

    public void onConfigSave(final JsonObject jsonObject) throws Throwable {
    }

    public void setSession(final Session session) throws RuntimeException {
        this.mc().session = session;
        this.mc().getSplashTextLoader().session = session;
        if (session.getAccountType().equals(Session.AccountType.LEGACY)) {
            Vandalism.getInstance().getLogger().info("Logged in as: " + session.getUsername());
            return;
        }
        final UserApiService userApiService;
        try {
            userApiService = new YggdrasilAuthenticationService(this.mc().getNetworkProxy(), YggdrasilEnvironment.PROD.getEnvironment()).createUserApiService(session.getAccessToken());
        } catch (final AuthenticationException e) {
            throw new RuntimeException(e);
        }
        this.mc().userApiService = userApiService;
        this.mc().socialInteractionsManager = new SocialInteractionsManager(this.mc(), userApiService);
        this.mc().profileKeys = ProfileKeys.create(userApiService, session, this.mc().runDirectory.toPath());
        if (this.mc().abuseReportContext == null) {
            this.mc().abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), userApiService);
        } else {
            this.mc().abuseReportContext = AbuseReportContext.create(this.mc().abuseReportContext.environment, userApiService);
        }
        this.mc().realmsPeriodicCheckers = new RealmsPeriodicCheckers(RealmsClient.createRealmsClient(this.mc()));
        Vandalism.getInstance().getLogger().info("Logged in as: " + session.getUsername());
    }

}