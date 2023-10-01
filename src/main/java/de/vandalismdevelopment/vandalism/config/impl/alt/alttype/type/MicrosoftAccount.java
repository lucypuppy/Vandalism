package de.vandalismdevelopment.vandalism.config.impl.alt.alttype.type;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.Account;
import de.vandalismdevelopment.vandalism.util.AES;
import de.vandalismdevelopment.vandalism.util.SessionUtil;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.session.Session;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.UUID;

public class MicrosoftAccount extends Account {

    private String refreshToken;

    public MicrosoftAccount(final String refreshToken, UUID uuid, final String username) {
        super("microsoft", username, uuid);
        this.refreshToken = refreshToken;
    }

    private final static MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

    @Override
    public void login() {
        try {
            final MicrosoftAuthResult result = authenticator.loginWithRefreshToken(this.refreshToken);

            this.setUsername(result.getProfile().getName());
            this.refreshToken = result.getRefreshToken();
            this.setUuid(UUID.fromString(result.getProfile().getId()));

            SessionUtil.setSession(new Session(this.getUsername(), this.getUuid(), result.getAccessToken(),
                    Optional.empty(), Optional.empty(), Session.AccountType.MSA));
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Failed to log into a microsoft account.", throwable);
            return;
        }

        Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getAccountConfig());
        Vandalism.getInstance().getLogger().info("Logged in with " + this.getUsername());
    }

    @Override
    public void onConfigSave(final JsonObject jsonObject) {
        try {
            final SecretKey secretKey = AES.getKeyFromPassword(this.getUsername());
            jsonObject.addProperty("refreshToken", AES.encrypt(this.refreshToken, secretKey));
        } catch (final InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                       InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

}
