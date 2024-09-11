/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.account.config.AccountsConfig;
import de.nekosarekawaii.vandalism.base.account.gui.AccountsClientWindow;
import de.nekosarekawaii.vandalism.base.account.template.MicrosoftAccount;
import de.nekosarekawaii.vandalism.base.account.type.SessionAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSCredentialsAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSDeviceCodeAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSLocalWebserverAccount;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.event.internal.UpdateSessionListener;
import de.nekosarekawaii.vandalism.injection.access.ISession;
import de.nekosarekawaii.vandalism.util.storage.Storage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.session.Session;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class AccountManager extends Storage<Account> implements UpdateSessionListener {

    public static final Map<Account, AccountFactory> ACCOUNT_TYPES = new LinkedHashMap<>();

    public AccountManager(final ConfigManager configManager, final ClientWindowManager clientWindowManager) {
        Arrays.asList(
                new SessionAccount(),

                new MSDeviceCodeAccount(),
                new MSLocalWebserverAccount(),
                new MSCredentialsAccount()

                // new EasyMCAccount() | R.I.P. EasyMC :c
        ).forEach(account -> ACCOUNT_TYPES.put(account, account.factory()));
        configManager.add(new AccountsConfig(this));
        clientWindowManager.add(new AccountsClientWindow(this));
    }

    private Account firstAccount;

    @Setter
    private Account currentAccount;

    @Override
    public void init() {
        Vandalism.getInstance().getEventSystem().subscribe(UpdateSessionEvent.ID, this);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (final InterruptedException ignored) {
                }
                if (FabricBootstrap.INITIALIZED) {
                    for (final Account abstractAccount : this.getList()) {
                        if (abstractAccount instanceof final MicrosoftAccount account) {
                            final long tokenExpirationTime = account.getTokenExpirationTime();
                            if (tokenExpirationTime == -1) {
                                continue;
                            }
                            if (account.getRefreshAttempts() > 2) {
                                continue;
                            }
                            if (tokenExpirationTime - System.currentTimeMillis() <= 0) {
                                Vandalism.getInstance().getLogger().info("Refreshing microsoft account {}...", account.getDisplayName());
                                try {
                                    account.refresh();
                                } catch (final Throwable throwable) {
                                    Vandalism.getInstance().getLogger().error("Failed to refresh microsoft account: " + account.getDisplayName(), throwable);
                                }
                                account.increaseRefreshAttempts();
                                if (account.getRefreshAttempts() > 2) {
                                    Vandalism.getInstance().getLogger().warn("Microsoft account {} has reached the maximum amount of refresh attempts.", account.getDisplayName());
                                }
                            }
                        }
                    }
                }
            }
        }, "Microsoft Token Refresher").start();
    }

    @Override
    public void onUpdateSession(UpdateSessionEvent event) {
        if (this.firstAccount == null) {
            this.firstAccount = this.currentAccount = fromSession(event.newSession);
        } else if (!((ISession) event.newSession).vandalism$isSelfInflicted()) {
            this.currentAccount = fromSession(event.newSession);
        }
    }

    private Account fromSession(final Session session) {
        return new SessionAccount(
                session.getUsername(),
                session.getUuidOrNull() != null ? session.getUuidOrNull().toString() : "",
                session.getAccessToken(),
                session.getXuid().orElse(""),
                session.getClientId().orElse("")
        );
    }

}
