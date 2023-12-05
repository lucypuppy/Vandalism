package de.vandalismdevelopment.vandalism.config.impl.account.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.account.Account;
import de.vandalismdevelopment.vandalism.util.EncryptionUtil;
import net.minecraft.client.session.Session;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Optional;
import java.util.UUID;

public class MicrosoftAccount extends Account {

    private String data;

    public MicrosoftAccount(final String data, final UUID uuid, final String username) {
        super("microsoft", username, uuid);
        this.data = data;
    }

    @Override
    public void login() throws Throwable {
        try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
            JsonObject jsonObject = JsonParser.parseString(this.data).getAsJsonObject();
            final StepFullJavaSession.FullJavaSession mcProfile = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(
                    httpClient,
                    MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(jsonObject)
            );
            jsonObject = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(mcProfile);
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
    public void onConfigSave(final JsonObject jsonObject) {
        try {
            jsonObject.addProperty("data", EncryptionUtil.encrypt(this.data, EncryptionUtil.getKeyFromPassword(this.getUsername())));
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Failed to save a microsoft account: " + this.getUsername());
        }
    }

}
