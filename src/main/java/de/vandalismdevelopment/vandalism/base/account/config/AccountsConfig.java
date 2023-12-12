package de.vandalismdevelopment.vandalism.base.account.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.account.AbstractAccount;
import de.vandalismdevelopment.vandalism.base.account.AccountManager;
import de.vandalismdevelopment.vandalism.base.config.AbstractConfig;

public class AccountsConfig extends AbstractConfig<JsonArray> {

    private final AccountManager accountManager;

    public AccountsConfig(final AccountManager accountManager) {
        super(JsonArray.class, "accounts");

        this.accountManager = accountManager;
    }

    @Override
    public JsonArray save0() {
        final JsonArray array = new JsonArray();
        for (AbstractAccount account : accountManager.getList()) {
            final JsonObject accountNode = new JsonObject();
            try {
                account.save(accountNode);
            } catch (Throwable e) {
                Vandalism.getInstance().getLogger().error("Failed to save account " + account.getDisplayName(), e);
            }

            array.add(accountNode);
        }
        return array;
    }

    @Override
    public void load0(JsonArray mainNode) {
        mainNode.asList().stream().map(JsonElement::getAsJsonObject).forEach(accountNode -> {
            final String type = accountNode.get("type").getAsString();

            for (AbstractAccount accountType : AccountManager.ACCOUNT_TYPES.keySet()) {
                if (accountType.getName().equals(type)) {
                    try {
                        final AbstractAccount account = accountType.getClass().getDeclaredConstructor().newInstance();
                        account.load(accountNode);

                        accountManager.add(account);
                    } catch (Throwable t) {
                        Vandalism.getInstance().getLogger().error("Failed to load account " + type, t);
                    }

                    break;
                }
            }
        });
    }

}
