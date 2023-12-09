package de.vandalismdevelopment.vandalism.account_v2;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.account_v2.type.SessionAccount;

import java.util.UUID;

public class AccountManager extends Storage<AbstractAccount> {
    public static final AbstractAccount[] ACCOUNT_TYPES = new AbstractAccount[] {
            new SessionAccount()
    };

    @Override
    public void init() {
        for (int i = 0; i < 100; i++) {
            add(new SessionAccount(RandomUtils.randomString(10, true, true, false, false),
                    UUID.randomUUID().toString(), "", "", ""));
        }
    }

}
