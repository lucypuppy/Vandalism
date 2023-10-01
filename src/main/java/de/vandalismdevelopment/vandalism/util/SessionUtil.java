package de.vandalismdevelopment.vandalism.util;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;

public class SessionUtil {

    public static void setSession(final Session session) throws RuntimeException {
        // Set account session
        MinecraftClient.getInstance().session = session;

        // Set skin / cape session
        MinecraftClient.getInstance().getSplashTextLoader().session = session;

        if (session.getAccountType().equals(Session.AccountType.LEGACY)) return;

        // Refresh api
        final UserApiService userApiService;
        try {
            userApiService = new YggdrasilAuthenticationService(
                    MinecraftClient.getInstance().getNetworkProxy(),
                    YggdrasilEnvironment.PROD.getEnvironment()
            ).createUserApiService(session.getAccessToken());
        } catch (final AuthenticationException e) {
            throw new RuntimeException(e);
        }

        MinecraftClient.getInstance().userApiService = userApiService;
        MinecraftClient.getInstance().socialInteractionsManager = new SocialInteractionsManager(
                MinecraftClient.getInstance(),
                userApiService
        );
        MinecraftClient.getInstance().profileKeys = ProfileKeys.create(
                userApiService,
                session,
                MinecraftClient.getInstance().runDirectory.toPath()
        );
        if (MinecraftClient.getInstance().abuseReportContext == null) {
            MinecraftClient.getInstance().abuseReportContext = AbuseReportContext.create(
                    ReporterEnvironment.ofIntegratedServer(),
                    userApiService
            );
        } else {
            MinecraftClient.getInstance().abuseReportContext = AbuseReportContext
                    .create(MinecraftClient.getInstance()
                            .abuseReportContext
                            .environment, userApiService);
        }

        // Refresh realms
        final RealmsClient realmsClient = RealmsClient.createRealmsClient(MinecraftClient.getInstance());
        MinecraftClient.getInstance().realmsPeriodicCheckers = new RealmsPeriodicCheckers(realmsClient);
    }

}
