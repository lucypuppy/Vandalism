package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.Account;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.type.CrackedAccount;
import de.vandalismdevelopment.vandalism.config.impl.alt.alttype.type.MicrosoftAccount;
import de.vandalismdevelopment.vandalism.util.AES;
import net.minecraft.client.MinecraftClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountConfig extends ValueableConfig {

    private final List<Account> accounts;

    public AccountConfig() {
        super(Vandalism.getInstance().getDir(), "alts");
        this.accounts = new CopyOnWriteArrayList<>();
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject();
        final JsonArray accountArray = new JsonArray();

        for (final Account account : this.accounts) {
            final JsonObject accountObject = new JsonObject();
            accountObject.addProperty("username", account.getUsername());
            accountObject.addProperty("type", account.getType());
            if (account.getUuid() != null) {
                accountObject.addProperty("uuid", account.getUuid().toString());
            }

            account.onConfigSave(accountObject);
            accountArray.add(accountObject);
        }

        final UUID sessionUuid = MinecraftClient.getInstance().session.getUuidOrNull();
        configObject.addProperty("lastSession", (MinecraftClient.getInstance().session.getUsername() + (sessionUuid != null ? sessionUuid.toString() : "")).hashCode());
        configObject.add("alts", accountArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final JsonArray accountArray = jsonObject.getAsJsonArray("alts");
        final int lastSession = jsonObject.get("lastSession").getAsInt();

        for (final JsonElement accountElement : accountArray) {
            final JsonObject accountObject = accountElement.getAsJsonObject();
            final String username = accountObject.get("username").getAsString();
            final String type = accountObject.get("type").getAsString();
            final String uuid = accountObject.has("uuid") ? accountObject.get("uuid").getAsString() : null;
            Account account = null;

            switch (type) {
                case "microsoft" -> {
                    try {
                        final SecretKey secretKey = AES.getKeyFromPassword(username);
                        final String refreshToken = AES.decrypt(accountObject.get("refreshToken").getAsString(), secretKey);

                        account = new MicrosoftAccount(refreshToken, uuid != null ? UUID.fromString(uuid) : null, username);
                    } catch (final InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException |
                                   BadPaddingException | InvalidAlgorithmParameterException e) {
                        Vandalism.getInstance().getLogger().error("Microsoft account authentication failed.", e);
                    }
                }

                default -> account = new CrackedAccount(username, UUID.fromString(uuid));
            }

            if (account == null) {
                Vandalism.getInstance().getLogger().error("Failed to load account: " + username);
                continue;
            }

            this.accounts.add(account);

            if (lastSession == (account.getUsername() + account.getUuid()).hashCode()) {
                account.login();
            }
        }
    }

    public List<Account> getAccounts() {
        return this.accounts;
    }

}
