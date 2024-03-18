/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.base.account;

import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.base.account.config.AccountsConfig;
import de.nekosarekawaii.vandalism.base.account.gui.AccountsClientMenuWindow;
import de.nekosarekawaii.vandalism.base.account.type.EasyMCAccount;
import de.nekosarekawaii.vandalism.base.account.type.SessionAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSCredentialsAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSDeviceCodeAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSLocalWebserverAccount;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccountManager extends Storage<AbstractAccount> {

    public static final Map<AbstractAccount, AccountFactory> ACCOUNT_TYPES = new LinkedHashMap<>();

    public AccountManager(final ConfigManager configManager, final ClientMenuManager clientMenuManager) {
        Arrays.asList(
                new SessionAccount(),

                new MSDeviceCodeAccount(),
                new MSLocalWebserverAccount(),
                new MSCredentialsAccount(),

                new EasyMCAccount()
        ).forEach(account -> ACCOUNT_TYPES.put(account, account.factory()));
        configManager.add(new AccountsConfig(this));
        clientMenuManager.add(new AccountsClientMenuWindow(this));
    }

    private AbstractAccount firstAccount;
    private AbstractAccount currentAccount;

    @Override
    public void init() {
        final Session session = MinecraftClient.getInstance().getSession();
        this.firstAccount = this.currentAccount = new SessionAccount(
                session.getUsername(),
                session.getUuidOrNull() != null ? session.getUuidOrNull().toString() : "",
                session.getAccessToken(),
                session.getXuid().orElse(""),
                session.getClientId().orElse("")
        );
    }

    public void setCurrentAccount(final AbstractAccount currentAccount) {
        this.currentAccount = currentAccount;
    }

    public AbstractAccount getCurrentAccount() {
        return this.currentAccount;
    }

    public void logOut() throws Throwable {
        this.firstAccount.logIn();
    }

}
