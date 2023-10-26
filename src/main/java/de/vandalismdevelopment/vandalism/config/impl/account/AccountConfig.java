package de.vandalismdevelopment.vandalism.config.impl.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.config.impl.account.impl.CrackedAccount;
import de.vandalismdevelopment.vandalism.config.impl.account.impl.MicrosoftAccount;
import de.vandalismdevelopment.vandalism.util.AES;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Uuids;
import net.raphimc.mcauth.MinecraftAuth;
import net.raphimc.mcauth.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountConfig extends ValueableConfig {

    private final List<Account> accounts;

    public AccountConfig(final File dir) {
        super(dir, "accounts");
        this.accounts = new CopyOnWriteArrayList<>();
        MinecraftAuth.LOGGER = new ILogger() {

            @Override
            public void info(final String message) {
                Vandalism.getInstance().getLogger().info(message);
            }

            @Override
            public void warn(final String message) {
                Vandalism.getInstance().getLogger().info(message);
            }

            @Override
            public void error(final String message) {
                Vandalism.getInstance().getLogger().error(message);
            }

        };
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
        final UUID sessionUuid = MinecraftClient.getInstance().getSession().getUuidOrNull();
        configObject.addProperty(
                "lastSession",
                (MinecraftClient.getInstance().getSession().getUsername() + (sessionUuid != null ? sessionUuid.toString() : "")).hashCode()
        );
        configObject.add("accounts", accountArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final JsonArray accountArray = jsonObject.getAsJsonArray("accounts");
        final int lastSession = jsonObject.get("lastSession").getAsInt();
        for (final JsonElement accountElement : accountArray) {
            final JsonObject accountObject = accountElement.getAsJsonObject();
            final String
                    username = accountObject.get("username").getAsString(),
                    type = accountObject.get("type").getAsString(),
                    uuid = accountObject.has("uuid") ? accountObject.get("uuid").getAsString() : null;
            Account account = null;
            switch (type) {
                case "microsoft" -> {
                    try {
                        account = new MicrosoftAccount(
                                AES.decrypt(
                                        accountObject.get("data").getAsString(),
                                        AES.getKeyFromPassword(username)
                                ),
                                uuid != null ? UUID.fromString(uuid) : null,
                                username
                        );
                    } catch (final Throwable throwable) {
                        Vandalism.getInstance().getLogger().error("Failed to log into a microsoft account.", throwable);
                    }
                }
                default -> account = new CrackedAccount(
                        username,
                        uuid == null ? Uuids.getOfflinePlayerUuid(username) : UUID.fromString(uuid)
                );
            }
            if (account == null) {
                Vandalism.getInstance().getLogger().error("Failed to load account: " + username);
                continue;
            }
            this.accounts.add(account);
            if (lastSession == (account.getUsername() + account.getUuid()).hashCode()) {
                try {
                    account.login();
                }
                catch (final Throwable throwable) {
                    Vandalism.getInstance().getLogger().error("Failed to log into the " + account.getType() + " account: " + account.getUsername(), throwable);
                }
            }
        }
    }

    public List<Account> getAccounts() {
        return this.accounts;
    }

}
