/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

import de.nekosarekawaii.vandalism.base.account.config.AccountsConfig;
import de.nekosarekawaii.vandalism.base.account.gui.AccountsClientWindow;
import de.nekosarekawaii.vandalism.base.account.type.EasyMCAccount;
import de.nekosarekawaii.vandalism.base.account.type.SessionAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSCredentialsAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSDeviceCodeAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSLocalWebserverAccount;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.util.common.Storage;
import de.nekosarekawaii.vandalism.util.common.UUIDUtil;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountManager extends Storage<AbstractAccount> {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static final Map<AbstractAccount, AccountFactory> ACCOUNT_TYPES = new LinkedHashMap<>();

    public AccountManager(final ConfigManager configManager, final ClientWindowManager clientWindowManager) {
        Arrays.asList(
                new SessionAccount(),

                new MSDeviceCodeAccount(),
                new MSLocalWebserverAccount(),
                new MSCredentialsAccount(),

                new EasyMCAccount()
        ).forEach(account -> ACCOUNT_TYPES.put(account, account.factory()));
        configManager.add(new AccountsConfig(this));
        clientWindowManager.add(new AccountsClientWindow(this));
    }

    private AbstractAccount firstAccount;

    @Getter
    @Setter
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

    public void logout() throws Throwable {
        this.firstAccount.login();
    }

    public void loginCracked(final String username) {
        this.loginCracked(username, "", false);
    }

    public void loginCracked(final String username, final boolean add) {
        this.loginCracked(username, "", add);
    }

    public void loginCracked(final String username, final String uuid) {
        this.loginCracked(username, uuid, false);
    }

    public void loginCracked(final String username, final String uuid, final boolean add) {
        EXECUTOR.submit(() -> {
            String fixedUUID = uuid;
            if (fixedUUID.isBlank()) {
                try {
                    fixedUUID = UUIDUtil.getUUIDFromName(username);
                } catch (final Throwable ignored) {
                    fixedUUID = Uuids.getOfflinePlayerUuid(username).toString();
                }
            }
            final SessionAccount sessionAccount = new SessionAccount(
                    username,
                    fixedUUID,
                    "",
                    "",
                    ""
            );
            if (add) {
                this.add(sessionAccount);
            }
            sessionAccount.login();
            ChatUtil.infoChatMessage("Username changed to: " + Formatting.DARK_AQUA + username);
        });
    }

}
