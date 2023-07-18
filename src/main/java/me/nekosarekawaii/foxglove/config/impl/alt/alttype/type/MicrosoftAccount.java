package me.nekosarekawaii.foxglove.config.impl.alt.alttype.type;

import com.google.gson.JsonObject;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import me.nekosarekawaii.foxglove.util.AES;
import me.nekosarekawaii.foxglove.util.minecraft.SessionUtil;
import net.minecraft.client.util.Session;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public class MicrosoftAccount extends Account {

    private String refreshToken;

    public MicrosoftAccount(final String refreshToken, String uuid, final String username) {
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
            this.setUuid(result.getProfile().getId());

            SessionUtil.setSession(new Session(this.getUsername(), this.getUuid(), result.getAccessToken(),
                    Optional.empty(), Optional.empty(), Session.AccountType.MSA));
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            return;
        }

        Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
        Foxglove.getInstance().getLogger().info("Logged in with " + this.getUsername());
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
