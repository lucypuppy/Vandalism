/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        accountNode.addProperty("type", account.getName());
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
