package de.vandalismdevelopment.vandalism.config.impl.account.impl;

import de.vandalismdevelopment.vandalism.config.impl.account.Account;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class CrackedAccount extends Account {

    public final static String TOKEN = "FabricMC";

    public CrackedAccount(final String username, final UUID uuid) {
        super("cracked", username, uuid);
    }

    @Override
    public void login() {
        this.setSession(new Session(
                this.getUsername(),
                this.getUuid(),
                TOKEN,
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.LEGACY
        ));
    }

}
