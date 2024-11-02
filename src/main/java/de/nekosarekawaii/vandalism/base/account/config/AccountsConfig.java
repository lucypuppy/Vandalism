/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.base.account.Account;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.base.config.Config;

public class AccountsConfig extends Config<JsonObject> {

    private final AccountManager accountManager;

    public AccountsConfig(final AccountManager accountManager) {
        super(JsonObject.class, "accounts");
        this.accountManager = accountManager;
    }

    private void saveAccount(final JsonObject accountNode, final Account account) {
        accountNode.addProperty("type", account.getType());
        try {
            account.save(accountNode);
        } catch (Throwable t) {
            Vandalism.getInstance().getLogger().error("Failed to save the account: {}", account.getDisplayName(), t);
        }
    }

    @Override
    public JsonObject save0() {
        final JsonObject mainNode = new JsonObject();
        final JsonArray accountsNode = new JsonArray();
        for (final Account account : this.accountManager.getList()) {
            final JsonObject accountNode = new JsonObject();
            this.saveAccount(accountNode, account);
            accountsNode.add(accountNode);
        }
        mainNode.add("accounts", accountsNode);
        final Account account = this.accountManager.getCurrentAccount();
        if (account != null) {
            final JsonObject currentAccountNode = new JsonObject();
            this.saveAccount(currentAccountNode, account);
            mainNode.add("lastAccount", currentAccountNode);
        }
        return mainNode;
    }

    private Account loadAccount(final JsonObject accountNode) {
        if (accountNode.has("type")) {
            final String type = accountNode.get("type").getAsString();
            for (final Account accountType : AccountManager.ACCOUNT_TYPES.keySet()) {
                if (accountType.getType().equals(type)) {
                    try {
                        final Account account = accountType.getClass().getDeclaredConstructor().newInstance();
                        account.load(accountNode);
                        return account;
                    } catch (Throwable t) {
                        Vandalism.getInstance().getLogger().error("Failed to load an account of the type: {}", type, t);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void load0(final JsonObject mainNode) {
        if (mainNode.has("accounts")) {
            final JsonArray accountsNode = mainNode.get("accounts").getAsJsonArray();
            accountsNode.asList().stream().map(JsonElement::getAsJsonObject).forEach(accountNode -> {
                final Account account = this.loadAccount(accountNode);
                if (account != null) {
                    this.accountManager.add(account);
                }
            });
        }
        if (mainNode.has("lastAccount")) {
            final Account lastAccount = this.loadAccount(mainNode.get("lastAccount").getAsJsonObject());
            if (lastAccount != null) {
                lastAccount.login();
            } else {
                Vandalism.getInstance().getLogger().error("Failed to load the last account!");
            }
        }
    }

}
