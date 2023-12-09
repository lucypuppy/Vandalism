package de.vandalismdevelopment.vandalism.account_v2;

import com.google.gson.JsonObject;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.PlayerSkinRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractAccount implements IName {

    private final String name;
    protected Session session;
    protected String status;
    protected PlayerSkinRenderer playerSkin;

    public AbstractAccount(String name) {
        this.name = name;
    }

    public abstract void logIn0() throws Throwable;
    
    public abstract void save0(final JsonObject mainNode);
    public abstract void load0(final JsonObject mainNode);

    public abstract String getDisplayName();

    public void save(final JsonObject mainNode) {
        if (session != null) {
            final JsonObject sessionNode = new JsonObject();
            saveSession(sessionNode, session);

            mainNode.add("session", sessionNode);
        }
        save0(mainNode);
    }

    public void load(final JsonObject mainNode) {
        if (mainNode.has("session")) {
            updateSession(loadSession(mainNode.get("session").getAsJsonObject()));
        }
        load0(mainNode);
    }

    public abstract AccountFactory factory();

    public PlayerSkinRenderer getPlayerSkin() {
        return playerSkin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void updateSession(final Session session) {
        this.session = session;
        playerSkin = new PlayerSkinRenderer(session.getUuidOrNull(), session.getUsername());
    }

    @Override
    public String getName() {
        return name;
    }

    public void logIn() throws Throwable {
        final MinecraftClient mc = MinecraftClient.getInstance();
        logIn0();
        mc.session = session;
        mc.getSplashTextLoader().session = session;

        if (session.getAccountType().equals(Session.AccountType.LEGACY)) {
            Vandalism.getInstance().getLogger().info("Logged in as: " + session.getUsername());
            return;
        }
        UserApiService userApiService;
        try {
            userApiService = new YggdrasilAuthenticationService(mc.getNetworkProxy(), YggdrasilEnvironment.PROD.getEnvironment()).createUserApiService(session.getAccessToken());
        } catch (final AuthenticationException e) {
            userApiService = UserApiService.OFFLINE;
        }
        mc.userApiService = userApiService;
        mc.socialInteractionsManager = new SocialInteractionsManager(mc, userApiService);
        mc.profileKeys = ProfileKeys.create(userApiService, session, mc.runDirectory.toPath());
        mc.abuseReportContext = AbuseReportContext.create(mc.abuseReportContext != null ? mc.abuseReportContext.environment : ReporterEnvironment.ofIntegratedServer(), userApiService);
        mc.realmsPeriodicCheckers = new RealmsPeriodicCheckers(RealmsClient.createRealmsClient(mc));

        if (userApiService == UserApiService.OFFLINE) {
            setStatus("Failed to update UserApiService");
        } else {
            setStatus("Updated session and logged in");
        }
    }

    public void saveSession(final JsonObject node, final Session session) {
        node.addProperty("username", session.getUsername());
        if (session.getUuidOrNull() != null) {
            node.addProperty("uuid", session.getUuidOrNull().toString());
        }
        node.addProperty("accessToken", session.getAccessToken());
        session.getXuid().ifPresent(s -> node.addProperty("xuid", s));
        session.getClientId().ifPresent(s -> node.addProperty("clientId", s));
        node.addProperty("accountType", session.getAccountType().getName());
    }

    public Session loadSession(final JsonObject node) {
        final String username = node.get("username").getAsString();
        final String uuid = node.has("uuid") ? node.get("uuid").getAsString() : null;
        final String accessToken = node.get("accessToken").getAsString();
        final Optional<String> xuid = node.has("xuid") ? Optional.of(node.get("xuid").getAsString()) : Optional.empty();
        final Optional<String> clientId = node.has("clientId") ? Optional.of(node.get("clientId").getAsString()) : Optional.empty();
        final String accountType = node.get("accountType").getAsString();

        return new Session(username, uuid == null ? null : UUID.fromString(uuid), accessToken, xuid, clientId, Session.AccountType.byName(accountType));
    }

    public interface AccountFactory {
        
        void displayFactory();

        AbstractAccount make();

    }
    
}
