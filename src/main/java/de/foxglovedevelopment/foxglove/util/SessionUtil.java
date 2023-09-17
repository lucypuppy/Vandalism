package de.foxglovedevelopment.foxglove.util;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.injection.accessors.AccessorRealmsMainScreen;
import de.foxglovedevelopment.foxglove.injection.accessors.AccessorYggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ReporterEnvironment;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;

import java.util.UUID;

public class SessionUtil {

    public static void setSession(final Session session) {
        // Set sessions
        MinecraftClient.getInstance().session = session;
        MinecraftClient.getInstance().getSplashTextLoader().session = session;

        // Refresh the session properties
        MinecraftClient.getInstance().getSessionProperties().clear();
        MinecraftClient.getInstance().getSessionProperties();

        // Refresh userapi stuff
        final UserApiService userApiService = getUserapiService(session);
        MinecraftClient.getInstance().userApiService = userApiService;
        MinecraftClient.getInstance().socialInteractionsManager = new SocialInteractionsManager(MinecraftClient.getInstance(), userApiService);
        MinecraftClient.getInstance().profileKeys = ProfileKeys.create(userApiService, session, MinecraftClient.getInstance().runDirectory.toPath());

        if (MinecraftClient.getInstance().abuseReportContext == null) {
            MinecraftClient.getInstance().abuseReportContext = AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), userApiService);
        } else {
            MinecraftClient.getInstance().abuseReportContext = AbuseReportContext.create(MinecraftClient.getInstance().abuseReportContext.environment, userApiService);
        }

        // No one uses them but i will add them anyway
        final RealmsClient realmsClient = RealmsClient.createRealmsClient(MinecraftClient.getInstance());
        MinecraftClient.getInstance().realmsPeriodicCheckers = new RealmsPeriodicCheckers(realmsClient);
        AccessorRealmsMainScreen.setCheckedClientCompatibility(false);
        AccessorRealmsMainScreen.setRealmsGenericErrorScreen(null);
    }

    private static UserApiService getUserapiService(final Session session) {
        UserApiService userApiService = UserApiService.OFFLINE;

        if (!"-".equals(session.getAccessToken())) {
            try {
                userApiService = getAuthService().createUserApiService(session.getAccessToken());
            } catch (final AuthenticationException e) {
                Foxglove.getInstance().getLogger().error("Failed account authentication.", e);
            }
        }

        return userApiService;
    }

    private static YggdrasilAuthenticationService getAuthService() {
        final YggdrasilAuthenticationService authService = ((YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService()).getAuthenticationService();

        if (((AccessorYggdrasilAuthenticationService) authService).getClientToken() == null) {
            ((AccessorYggdrasilAuthenticationService) authService).setClientToken(UUID.randomUUID().toString());
        }

        return authService;
    }

}
