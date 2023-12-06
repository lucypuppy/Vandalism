package de.vandalismdevelopment.vandalism.config.impl.account.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.account.Account;
import de.vandalismdevelopment.vandalism.util.EncryptionUtil;
import net.minecraft.client.session.Session;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.AbstractStep;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Optional;
import java.util.UUID;

//TODO: Fix this, because this login method is not working!
public class MicrosoftLocalWebServerAccount extends Account {

    public static final AbstractStep<?, StepFullJavaSession.FullJavaSession> LOCAL_WEBSERVER_LOGIN = MinecraftAuth.builder()
            .withClientId("4c1c68f2-ac5a-4d41-a6c3-dca6cb0e37ec")
            .withClientSecret("YQ-8Q~EUcl2cqxhRNkEwy0CvWL.bTcJx44jdPbTa")
            .withScope("service::user.auth.xboxlive.com::MBI_SSL")
            .withRedirectUri("http://localhost:18703")
            .localWebServer()
            .withoutDeviceToken()
            .regularAuthentication(MicrosoftConstants.JAVA_XSTS_RELYING_PARTY)
            .buildMinecraftJavaProfileStep(false);

    private String data;

    public MicrosoftLocalWebServerAccount(final String data, final UUID uuid, final String username) {
        super("microsoft-local-web-server", username, uuid);
        this.data = data;
    }

    @Override
    public void login() throws Throwable {
        try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
            JsonObject jsonObject = JsonParser.parseString(this.data).getAsJsonObject();
            final StepFullJavaSession.FullJavaSession mcProfile = LOCAL_WEBSERVER_LOGIN.refresh(
                    httpClient,
                    LOCAL_WEBSERVER_LOGIN.fromJson(jsonObject)
            );
            jsonObject = LOCAL_WEBSERVER_LOGIN.toJson(mcProfile);
            this.data = jsonObject.toString();
            this.setUsername(mcProfile.getMcProfile().getName());
            this.setUuid(mcProfile.getMcProfile().getId());
            this.setSession(new Session(
                    this.getUsername(),
                    this.getUuid(),
                    mcProfile.getMcProfile().getMcToken().getAccessToken(),
                    Optional.empty(),
                    Optional.empty(),
                    Session.AccountType.MSA
            ));
        }
        Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
    }

    @Override
    public void onConfigSave(final JsonObject jsonObject) throws Throwable {
        jsonObject.addProperty("data", EncryptionUtil.encrypt(this.data, EncryptionUtil.getKeyFromPassword(this.getUsername())));
    }

}
