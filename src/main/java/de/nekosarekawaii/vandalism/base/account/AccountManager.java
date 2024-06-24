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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.config.AccountsConfig;
import de.nekosarekawaii.vandalism.base.account.gui.AccountsClientWindow;
import de.nekosarekawaii.vandalism.base.account.type.EasyMCAccount;
import de.nekosarekawaii.vandalism.base.account.type.SessionAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSCredentialsAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSDeviceCodeAccount;
import de.nekosarekawaii.vandalism.base.account.type.microsoft.MSLocalWebserverAccount;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.event.internal.UpdateSessionListener;
import de.nekosarekawaii.vandalism.injection.access.ISession;
import de.nekosarekawaii.vandalism.util.common.Storage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.session.Session;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccountManager extends Storage<AbstractAccount> implements UpdateSessionListener {

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
        Vandalism.getInstance().getEventSystem().subscribe(UpdateSessionEvent.ID, this);
    }

    @Override
    public void onUpdateSession(UpdateSessionEvent event) {
        if (this.firstAccount == null) {
            this.firstAccount = this.currentAccount = fromSession(event.newSession);
        } else if (!((ISession) event.newSession).vandalism$isSelfInflicted()) {
            this.currentAccount = fromSession(event.newSession);
        }
    }

    private AbstractAccount fromSession(final Session session) {
        return new SessionAccount(
                session.getUsername(),
                session.getUuidOrNull() != null ? session.getUuidOrNull().toString() : "",
                session.getAccessToken(),
                session.getXuid().orElse(""),
                session.getClientId().orElse("")
        );
    }

    public AbstractAccount getFirstAccount() {
        return firstAccount;
    }

}
