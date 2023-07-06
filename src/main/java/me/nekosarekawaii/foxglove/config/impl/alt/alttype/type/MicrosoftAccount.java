package me.nekosarekawaii.foxglove.config.impl.alt.alttype.type;

import com.google.gson.JsonObject;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import net.minecraft.client.util.Session;

import java.util.Optional;

public class MicrosoftAccount extends Account {

    private final String email, password;
    private String refreshToken, uuid;

    public MicrosoftAccount() {
        this("browserSession", "-", null, null, "");
    }

    public MicrosoftAccount(final String email, final String password) {
        this(email, password, null, null, email);
    }

    public MicrosoftAccount(final String email, final String password, final String refreshToken, String uuid, final String username) {
        super("microsoft", username);

        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.uuid = uuid;
    }

    private static final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

    public void loginWithBrowser() {
        try {
            final MicrosoftAuthResult result;

            if (this.refreshToken == null || this.refreshToken.isEmpty()) {
                result = authenticator.loginWithWebview();
            } else {
                result = authenticator.loginWithRefreshToken(this.refreshToken);
            }

            this.setUsername(result.getProfile().getName());
            this.refreshToken = result.getRefreshToken();
            this.uuid = result.getProfile().getId();

            mc().session = new Session(this.getUsername(),
                    this.uuid, result.getAccessToken(), Optional.empty(),
                    Optional.empty(), Session.AccountType.MSA);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            return;
        }
        Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
        Foxglove.getInstance().getLogger().info("Logged in with " + this.getUsername());
    }

    @Override
    public void login() {
        try {
            final MicrosoftAuthResult result;

            if (this.refreshToken == null || this.refreshToken.isEmpty()) {
                result = authenticator.loginWithCredentials(this.email, this.password);
            } else {
                result = authenticator.loginWithRefreshToken(this.refreshToken);
            }

            this.setUsername(result.getProfile().getName());
            this.refreshToken = result.getRefreshToken();
            this.uuid = result.getProfile().getId();

            mc().session = new Session(this.getUsername(),
                    this.uuid, result.getAccessToken(), Optional.empty(),
                    Optional.empty(), Session.AccountType.MSA);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            return;
        }
        Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
        Foxglove.getInstance().getLogger().info("Logged in with " + this.getUsername());
    }

    @Override
    public void onConfigSave(final JsonObject jsonObject) {
        jsonObject.addProperty("email", this.email);
        jsonObject.addProperty("password", this.password);
        jsonObject.addProperty("refreshToken", this.refreshToken);
        jsonObject.addProperty("uuid", this.uuid);
    }

    public boolean isBrowserSession() {
        return this.email.equals("browserSession");
    }

}
