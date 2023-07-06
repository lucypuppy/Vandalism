package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.ValueableConfig;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.Account;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.type.CrackedAccount;
import me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype.type.MicrosoftAccount;

import java.io.IOException;

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

            account.onConfigSave(accountObject);
            accountArray.add(accountObject);
        }

        configObject.add("alts", accountArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final JsonArray accountArray = jsonObject.getAsJsonArray("alts");

        for (final JsonElement accountElement : accountArray) {
            final JsonObject accountObject = accountElement.getAsJsonObject();
            final String username = accountObject.get("username").getAsString();
            final String type = accountObject.get("type").getAsString();

            switch (type) {
                case "microsoft" -> {
                    final String email = accountObject.get("email").getAsString();
                    final String password = accountObject.get("password").getAsString();
                    final String refreshToken = accountObject.get("refreshToken").getAsString();
                    final String uuid = accountObject.get("uuid").getAsString();

                    if (refreshToken.isEmpty() || uuid.isEmpty()) {
                        this.accounts.add(new MicrosoftAccount(email, password));
                    } else {
                        this.accounts.add(new MicrosoftAccount(email, password, refreshToken, uuid, username));
                    }
                }

                default -> this.accounts.add(new CrackedAccount(username));
            }
        }
    }

    public ObjectArrayList<Account> getAccounts() {
        return this.accounts;
    }

}
