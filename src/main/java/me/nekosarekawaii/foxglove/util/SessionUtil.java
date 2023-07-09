package me.nekosarekawaii.foxglove.util;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import me.nekosarekawaii.foxglove.mixin.com.mojang.authlib.yggdrasil.MixinYggdrasilAuthenticationServiceAccessor;
import me.nekosarekawaii.foxglove.mixin.net.minecraft.client.realms.gui.screen.RealmsMainScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;

import java.util.UUID;

public class SessionUtil {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void setSession(final Session session) {
        // Set sessions
        client.session = session;
        client.getSplashTextLoader().session = session;

        // Refresh the session properties
        client.getSessionProperties().clear();
        client.getSessionProperties();

        // Refresh userapi stuff
        final UserApiService userApiService = getUserapiService(session);
        client.userApiService = userApiService;
        client.socialInteractionsManager = new SocialInteractionsManager(client, userApiService);
        client.profileKeys = ProfileKeys.create(userApiService, session, client.runDirectory.toPath());
        client.abuseReportContext = AbuseReportContext.create(client.abuseReportContext.environment, userApiService);

        // No one uses them but i will add them anyway
        final RealmsClient realmsClient = RealmsClient.createRealmsClient(client);
        client.realmsPeriodicCheckers = new RealmsPeriodicCheckers(realmsClient);
        RealmsMainScreenAccessor.setCheckedClientCompatibility(false);
        RealmsMainScreenAccessor.setRealmsGenericErrorScreen(null);
    }

    private static UserApiService getUserapiService(final Session session) {
        UserApiService userApiService = UserApiService.OFFLINE;

        if (!"-".equals(session.getAccessToken())) {
            try {
                userApiService = getAuthService().createUserApiService(session.getAccessToken());
            } catch (final AuthenticationException e) {
                e.printStackTrace();
            }
        }

        return userApiService;
    }

    private static YggdrasilAuthenticationService getAuthService() {
        final YggdrasilAuthenticationService authService = ((YggdrasilMinecraftSessionService) MinecraftClient.getInstance()
                .getSessionService()).getAuthenticationService();

        if (((MixinYggdrasilAuthenticationServiceAccessor) authService).getClientToken() == null) {
            ((MixinYggdrasilAuthenticationServiceAccessor) authService).setClientToken(UUID.randomUUID().toString());
        }

        return authService;
    }

}
