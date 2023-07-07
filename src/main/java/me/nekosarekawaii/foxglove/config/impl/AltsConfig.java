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
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.util.UUID;

public class AltsConfig extends ValueableConfig {

    public AltsConfig() {
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

            account.onConfigSave(accountObject);
            accountArray.add(accountObject);
        }

        configObject.addProperty("lastSession", MinecraftClient.getInstance().session.getUsername());
        configObject.add("alts", accountArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final JsonArray accountArray = jsonObject.getAsJsonArray("alts");
        final String lastSession = jsonObject.get("lastSession").getAsString();

        for (final JsonElement accountElement : accountArray) {
            final JsonObject accountObject = accountElement.getAsJsonObject();
            final String username = accountObject.get("username").getAsString();
            final String type = accountObject.get("type").getAsString();
            final Account account;

            switch (type) {
                case "microsoft" -> {
                    final String email = accountObject.get("email").getAsString();
                    final String password = accountObject.get("password").getAsString();
                    final String refreshToken = accountObject.get("refreshToken").getAsString();
                    final String uuid = accountObject.get("uuid").getAsString();

                    if (refreshToken.isEmpty() || uuid.isEmpty()) {
                        account = new MicrosoftAccount(email, password);
                    } else {
                        account = new MicrosoftAccount(email, password, refreshToken, uuid, username);
                    }
                }

                default -> {
                    account = new CrackedAccount(username, UUID.fromString(accountObject.get("uuid").getAsString()));
                }
            }

            this.accounts.add(account);

            if (lastSession.equals(account.getUsername())) {
                account.login();
            }
        }
    }

    public ObjectArrayList<Account> getAccounts() {
        return this.accounts;
    }

}
