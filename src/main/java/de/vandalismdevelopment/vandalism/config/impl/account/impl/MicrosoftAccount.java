package de.vandalismdevelopment.vandalism.config.impl.account.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.account.Account;
import de.vandalismdevelopment.vandalism.util.AES;
import de.vandalismdevelopment.vandalism.util.SessionUtil;
import net.minecraft.client.session.Session;
import net.raphimc.mcauth.MinecraftAuth;
import net.raphimc.mcauth.step.java.StepMCProfile;
import net.raphimc.mcauth.util.MicrosoftConstants;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.crypto.SecretKey;
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
            final StepMCProfile.MCProfile mcProfile = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(
                    httpClient,
                    MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(jsonObject)
            );
            jsonObject = mcProfile.toJson();
            this.data = jsonObject.toString();
            this.setUsername(mcProfile.name());
            this.setUuid(mcProfile.id());
            SessionUtil.setSession(new Session(
                    this.getUsername(),
                    this.getUuid(),
                    mcProfile.prevResult().prevResult().access_token(),
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
            final SecretKey secretKey = AES.getKeyFromPassword(this.getUsername());
            jsonObject.addProperty("data", AES.encrypt(this.data, secretKey));
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Failed to save a microsoft account: " + this.getUsername());
        }
    }

}
