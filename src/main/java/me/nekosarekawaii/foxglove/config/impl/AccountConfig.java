package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.ValueableConfig;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.Account;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.type.CrackedAccount;
import me.nekosarekawaii.foxglove.config.impl.alt.alttype.type.MicrosoftAccount;
import me.nekosarekawaii.foxglove.util.AES;
import net.minecraft.client.MinecraftClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

public class AccountConfig extends ValueableConfig {

    public AccountConfig() {
        super(Foxglove.getInstance().getDir(), "alts");
    }

    private final ObjectArrayList<Account> accounts = new ObjectArrayList<>();

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject();
        final JsonArray accountArray = new JsonArray();

        for (final Account account : accounts) {
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
                        e.printStackTrace();
                    }
                }

                default -> account = new CrackedAccount(username, UUID.fromString(uuid));
            }

            if (account == null) {
                System.out.println("Failed to load account: " + username);
                continue;
            }

            this.accounts.add(account);

            if (lastSession == (account.getUsername() + account.getUuid()).hashCode()) {
                account.login();
            }
        }
    }

    public ObjectArrayList<Account> getAccounts() {
        return this.accounts;
    }

}
