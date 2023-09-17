package de.foxglovedevelopment.foxglove.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.config.ValueableConfig;
import de.foxglovedevelopment.foxglove.config.impl.alt.alttype.Account;
import de.foxglovedevelopment.foxglove.config.impl.alt.alttype.type.CrackedAccount;
import de.foxglovedevelopment.foxglove.config.impl.alt.alttype.type.MicrosoftAccount;
import de.foxglovedevelopment.foxglove.util.AES;
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
        super(Foxglove.getInstance().getDir(), "alts");
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
            accountObject.addProperty("uuid", account.getUuid());

            account.onConfigSave(accountObject);
            accountArray.add(accountObject);
        }

        configObject.addProperty("lastSession", (MinecraftClient.getInstance().session.getUsername() + MinecraftClient.getInstance().session.getUuid()).hashCode());
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
            final String uuid = accountObject.get("uuid").getAsString();
            Account account = null;

            switch (type) {
                case "microsoft" -> {
                    try {
                        final SecretKey secretKey = AES.getKeyFromPassword(username);
                        final String refreshToken = AES.decrypt(accountObject.get("refreshToken").getAsString(), secretKey);

                        account = new MicrosoftAccount(refreshToken, uuid, username);
                    } catch (final InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException |
                                   BadPaddingException | InvalidAlgorithmParameterException e) {
                        Foxglove.getInstance().getLogger().error("Microsoft account authentication failed.", e);
                    }
                }

                default -> account = new CrackedAccount(username, UUID.fromString(uuid));
            }

            if (account == null) {
                Foxglove.getInstance().getLogger().error("Failed to load account: " + username);
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
