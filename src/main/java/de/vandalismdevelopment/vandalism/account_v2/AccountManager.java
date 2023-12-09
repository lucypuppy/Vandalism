package de.vandalismdevelopment.vandalism.account_v2;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.account_v2.type.EasyMCAccount;
import de.vandalismdevelopment.vandalism.account_v2.type.microsoft.MSCredentialsAccount;
import de.vandalismdevelopment.vandalism.account_v2.type.microsoft.MSDeviceCodeAccount;
import de.vandalismdevelopment.vandalism.account_v2.type.SessionAccount;
import de.vandalismdevelopment.vandalism.account_v2.type.microsoft.MSLocalWebserverAccount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountManager extends Storage<AbstractAccount> {

    public static final Map<AbstractAccount, AccountFactory> ACCOUNT_TYPES = new HashMap<>();

    public AccountManager() {
        Arrays.asList(
                new MSDeviceCodeAccount(),
                new MSLocalWebserverAccount(),
                new MSCredentialsAccount(),

                new SessionAccount(),
                new EasyMCAccount()
        ).forEach(account -> ACCOUNT_TYPES.put(account, account.factory()));
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
