package de.nekosarekawaii.vandalism.base.account;

import com.google.gson.JsonObject;
import com.mojang.authlib.Environment;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import de.florianmichael.rclasses.io.mappings.TimeFormatter;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.internal.UpdateSessionListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.PlayerSkinRenderer;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAccount implements IName, MinecraftWrapper {

    private final static ImGuiInputTextCallback USERNAME_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData callbackData) {
            final var data = callbackData.getEventChar();
            if (data != 0 && !Character.isLetterOrDigit(data) && data != '_' && data != '§') {
                callbackData.setEventChar((char) 0);
            }
        }
    };

    private final String name;
    protected Session session;
    protected String status;
    protected PlayerSkinRenderer playerSkin;

    private String lastLogin;

    public AbstractAccount(String name) {
        this.name = name;
    }

    public abstract void logIn0() throws Throwable;
    
    public abstract void save0(final JsonObject mainNode) throws Throwable;
    public abstract void load0(final JsonObject mainNode) throws Throwable;

    public abstract String getDisplayName();

    public Environment getEnvironment() {
        return YggdrasilEnvironment.PROD.getEnvironment();
    }

    public void save(final JsonObject mainNode) throws Throwable {
        if (session != null) {
            final JsonObject sessionNode = new JsonObject();
            saveSession(sessionNode);

            mainNode.add("session", sessionNode);
        }
        if (lastLogin != null) {
            mainNode.addProperty("lastLogin", lastLogin);
        }
        save0(mainNode);
    }

    public void load(final JsonObject mainNode) throws Throwable {
        if (mainNode.has("session")) {
            updateSession(loadSession(mainNode.get("session").getAsJsonObject()));
        }
        if (mainNode.has("lastLogin")) {
            lastLogin = mainNode.get("lastLogin").getAsString();
        }
        load0(mainNode);
    }

    public abstract AccountFactory factory();

    public PlayerSkinRenderer getPlayerSkin() {
        return playerSkin;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Session getSession() {
        return session;
    }

    public void updateSession(Session session) {
        final var event = new UpdateSessionListener.UpdateSessionEvent(this.session, session);
        Vandalism.getInstance().getEventSystem().postInternal(UpdateSessionListener.UpdateSessionEvent.ID, event);
        this.session = event.newSession; // Allow the event to change the session

        playerSkin = new PlayerSkinRenderer(session.getUuidOrNull());
        lastLogin = TimeFormatter.currentDateTime();
    }

    @Override
    public String getName() {
        return name;
    }

    public void logIn() {
        CompletableFuture.runAsync(() -> {
            try {
                logIn0(); // Set the game session and reload the skins
                if (reloadProfileKeys(session, getEnvironment())) {
                    setStatus("Updated session and logged in");
                } else {
                    setStatus("Failed to update UserApiService");
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean reloadProfileKeys(final Session session, final Environment environment) {
        final var authenticationService = new YggdrasilAuthenticationService(mc.getNetworkProxy(), environment);
        mc.session = session; // We already did that in normal cases, but for people who use this as API we need to do it again
        mc.getSplashTextLoader().session = session; // We will never know, right?
        mc.sessionService = authenticationService.createMinecraftSessionService();

        if (session.getAccountType().equals(Session.AccountType.MSA)) {
            UserApiService userApiService;
            try {
                userApiService = authenticationService.createUserApiService(session.getAccessToken());
            } catch (AuthenticationException e) {
                userApiService = UserApiService.OFFLINE; // Technically trash, but whatever, Java is bad, Mojang is bad, everything is bad
            }
            mc.userApiService = userApiService;
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

    public void saveSession(final JsonObject node) {
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

}
