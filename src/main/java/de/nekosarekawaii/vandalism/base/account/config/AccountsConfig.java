package de.nekosarekawaii.vandalism.base.account.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.AbstractAccount;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;

public class AccountsConfig extends AbstractConfig<JsonObject> {

    private final AccountManager accountManager;

    public AccountsConfig(final AccountManager accountManager) {
        super(JsonObject.class, "accounts");

        this.accountManager = accountManager;
    }

    private void writeAccount(final JsonObject accountNode, final AbstractAccount account) {
        try {
            account.save(accountNode);
        } catch (Throwable e) {
            Vandalism.getInstance().getLogger().error("Failed to save account " + account.getDisplayName(), e);
        }
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        final var accountsNode = new JsonArray();
        for (AbstractAccount account : accountManager.getList()) {
            final var accountNode = new JsonObject();
            writeAccount(accountNode, account);

            accountsNode.add(accountNode);
        }
        mainNode.add("accounts", accountsNode);

        final var account = accountManager.getCurrentAccount();
        if (account != null) {
            final var currentAccountNode = new JsonObject();
            writeAccount(currentAccountNode, account);

            mainNode.add("currentAccount", currentAccountNode);
        }

        return mainNode;
    }

    private AbstractAccount loadAccount(final JsonObject accountNode) {
        final String type = accountNode.get("type").getAsString();

        for (AbstractAccount accountType : AccountManager.ACCOUNT_TYPES.keySet()) {
            if (accountType.getName().equals(type)) {
                try {
                    final AbstractAccount account = accountType.getClass().getDeclaredConstructor().newInstance();
                    account.load(accountNode);

                    return account;
                } catch (Throwable t) {
                    Vandalism.getInstance().getLogger().error("Failed to load account " + type, t);
                }
            }
        }
        return null;
    }

    @Override
    public void load0(JsonObject mainNode) {
        final var accountsNode = mainNode.get("accounts").getAsJsonArray();
        accountsNode.asList().stream().map(JsonElement::getAsJsonObject).forEach(accountNode -> accountManager.add(loadAccount(accountNode)));

        if (mainNode.has("currentAccount")) {
            final AbstractAccount account = loadAccount(mainNode.get("currentAccount").getAsJsonObject());

            try {
                account.logIn();
            } catch (Throwable e) {
                Vandalism.getInstance().getLogger().error("Failed to log in last account " + account.getDisplayName(), e);
            }
        }
    }

}
