package de.vandalismdevelopment.vandalism.config.impl.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.config.impl.account.impl.CrackedAccount;
import de.vandalismdevelopment.vandalism.config.impl.account.impl.MicrosoftDeviceCodeAccount;
import de.vandalismdevelopment.vandalism.util.EncryptionUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.util.Uuids;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountConfig extends ValueableConfig implements MinecraftWrapper {

    private final static HashMap<String, String> LOG_FILTERS = new HashMap<>();

    static {
        LOG_FILTERS.put("Got GameOwnership, games:", "Got GameOwnership games.");
        LOG_FILTERS.put("Got MC Profile, name:", "Got MC Profile name.");
    }

    private static String overwriteLogContent(final String message) {
        for (final Map.Entry<String, String> entry : LOG_FILTERS.entrySet()) {
            if (StringUtils.contains(message, entry.getKey())) {
                return entry.getValue();
            }
        }
        return message;
    }

    private final List<Account> accounts;

    public AccountConfig(final File dir) {
        super(dir, "accounts");
        this.accounts = new CopyOnWriteArrayList<>();
        MinecraftAuth.LOGGER = new ILogger() {

            @Override
            public void info(final String message) {
                Vandalism.getInstance().getLogger().info(overwriteLogContent(message));
            }

            @Override
            public void warn(final String message) {
                Vandalism.getInstance().getLogger().info(overwriteLogContent(message));
            }

            @Override
            public void error(final String message) {
                Vandalism.getInstance().getLogger().error(overwriteLogContent(message));
            }

        };
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject();
        final JsonArray accountArray = new JsonArray();
        for (final Account account : this.accounts) {
            try {
                final JsonObject accountObject = new JsonObject();
                accountObject.addProperty("username", account.getUsername());
                accountObject.addProperty("type", account.getType());
                if (account.getUuid() != null) {
                    accountObject.addProperty("uuid", account.getUuid().toString());
                }
                account.onConfigSave(accountObject);
                accountArray.add(accountObject);
            } catch (final Throwable throwable) {
                Vandalism.getInstance().getLogger().error("Failed to save a " + account.getType() + " account: " + account.getUsername());
            }
        }
        final UUID sessionUuid = this.mc().session.getUuidOrNull();
        configObject.addProperty("lastSession", (this.mc().session.getUsername() + (sessionUuid != null ? sessionUuid.toString() : "")).hashCode());
        configObject.add("accounts", accountArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final JsonArray accountArray = jsonObject.getAsJsonArray("accounts");
        final int lastSession = jsonObject.get("lastSession").getAsInt();
        for (final JsonElement accountElement : accountArray) {
            final JsonObject accountObject = accountElement.getAsJsonObject();
            final String usernameFromJson = accountObject.get("username").getAsString();
            final String typeFromJson = accountObject.get("type").getAsString();
            final String uuidFromJson = accountObject.has("uuid") ? accountObject.get("uuid").getAsString() : null;
            try {
                final UUID validUUID = uuidFromJson != null ? UUID.fromString(uuidFromJson) : null;
                Account account = switch (typeFromJson) {
                    case "microsoft-device-code" -> new MicrosoftDeviceCodeAccount(
                            EncryptionUtil.decrypt(
                                    accountObject.get("data").getAsString(),
                                    EncryptionUtil.getKeyFromPassword(usernameFromJson)
                            ),
                            validUUID,
                            usernameFromJson
                    );
                    default -> new CrackedAccount(
                            usernameFromJson,
                            uuidFromJson == null ? Uuids.getOfflinePlayerUuid(usernameFromJson) : UUID.fromString(uuidFromJson)
                    );
                };
                this.accounts.add(account);
                if (lastSession == (account.getUsername() + account.getUuid()).hashCode()) {
                    try {
                        account.login();
                    } catch (final Throwable throwable) {
                        Vandalism.getInstance().getLogger().error(
                                "Failed to log into the " + account.getType() + " account: " + account.getUsername(),
                                throwable
                        );
                    }
                }
            } catch (final Throwable throwable) {
                Vandalism.getInstance().getLogger().error("Failed to log create a " + typeFromJson + " account: " + usernameFromJson, throwable);
            }
        }
    }

    public List<Account> getAccounts() {
        return this.accounts;
    }

}
