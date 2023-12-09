package de.vandalismdevelopment.vandalism.base.account;

import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.base.account.config.AccountsConfig;
import de.vandalismdevelopment.vandalism.base.account.type.EasyMCAccount;
import de.vandalismdevelopment.vandalism.base.account.type.microsoft.MSCredentialsAccount;
import de.vandalismdevelopment.vandalism.base.account.type.microsoft.MSDeviceCodeAccount;
import de.vandalismdevelopment.vandalism.base.account.type.SessionAccount;
import de.vandalismdevelopment.vandalism.base.account.type.microsoft.MSLocalWebserverAccount;
import de.vandalismdevelopment.vandalism.base.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AccountManager extends Storage<AbstractAccount> {

    public static final Map<AbstractAccount, AccountFactory> ACCOUNT_TYPES = new HashMap<>();

    public AccountManager(final ConfigManager configManager) {
        Arrays.asList(
                new MSDeviceCodeAccount(),
                new MSLocalWebserverAccount(),
                new MSCredentialsAccount(),

                new SessionAccount(),
                new EasyMCAccount()
        ).forEach(account -> ACCOUNT_TYPES.put(account, account.factory()));

        configManager.add(new AccountsConfig(this));
    }

    public AbstractAccount currentAccount;
    private AbstractAccount firstAccount;

    @Override
    public void init() {
        final Session session = MinecraftClient.getInstance().getSession();
        firstAccount = currentAccount = new SessionAccount(
                session.getUsername(),
                session.getUuidOrNull() != null ? session.getUuidOrNull().toString() : "",
                session.getAccessToken(),
                session.getXuid().orElse(""),
                session.getClientId().orElse("")
        );
    }

    public void logIn(final AbstractAccount account) throws Throwable {
        account.logIn();
        currentAccount = account;
    }

    public AbstractAccount getCurrentAccount() {
        return currentAccount;
    }

    public AbstractAccount getFirstAccount() {
        return firstAccount;
    }

}
