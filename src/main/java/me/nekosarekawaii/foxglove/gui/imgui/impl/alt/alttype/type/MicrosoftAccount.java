package me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.type;

import com.google.gson.JsonObject;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.Account;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

public class MicrosoftAccount extends Account {

    private final String email, password;
    private String refreshToken, uuid;

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

    @Override
    public boolean login() {

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

            MinecraftClient.getInstance().session = new Session(this.getUsername(),
                    uuid, result.getAccessToken(),
                    null, null, Session.AccountType.MSA);

            return true;
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }

        Foxglove.getInstance().getConfigManager().save(Foxglove.getInstance().getConfigManager().getAccountConfig());
        System.out.println("Logged in with " + this.getUsername());
        return false;
    }

    @Override
    public void onConfigSave(JsonObject jsonObject) {
        jsonObject.addProperty("email", this.email);
        jsonObject.addProperty("password", this.password);
        jsonObject.addProperty("refreshToken", this.refreshToken);
        jsonObject.addProperty("uuid", this.uuid);
    }

}
