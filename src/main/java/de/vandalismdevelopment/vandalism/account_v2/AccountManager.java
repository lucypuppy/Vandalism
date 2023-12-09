package de.vandalismdevelopment.vandalism.account_v2;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.account_v2.type.EasyMCAccount;
import de.vandalismdevelopment.vandalism.account_v2.type.MSDeviceCodeAccount;
import de.vandalismdevelopment.vandalism.account_v2.type.SessionAccount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import java.util.UUID;

public class AccountManager extends Storage<AbstractAccount> {

    public static final AbstractAccount[] ACCOUNT_TYPES = new AbstractAccount[] {
            new SessionAccount(),
            new MSDeviceCodeAccount(),
            new EasyMCAccount()
    };

    public AbstractAccount currentAccount;

    @Override
    public void init() {
        final Session session = MinecraftClient.getInstance().getSession();
        currentAccount = new SessionAccount(
                session.getUsername(),
                session.getUuidOrNull() != null ? session.getUuidOrNull().toString() : "",
                session.getAccessToken(),
                session.getXuid().orElse(""),
                session.getClientId().orElse("")
        );

        for (int i = 0; i < 100; i++) {
            add(new SessionAccount(RandomUtils.randomString(10, true, true, false, false),
                    UUID.randomUUID().toString(), "", "", ""));
        }
    }

    public void logIn(final AbstractAccount account) throws Throwable {
        account.logIn();
        currentAccount = account;
    }

    public AbstractAccount getCurrentAccount() {
        return currentAccount;
    }

}
