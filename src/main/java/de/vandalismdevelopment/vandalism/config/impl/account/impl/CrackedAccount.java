package de.vandalismdevelopment.vandalism.config.impl.account.impl;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.account.Account;
import de.vandalismdevelopment.vandalism.util.SessionUtil;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

public class CrackedAccount extends Account {

    public CrackedAccount(final String username, final UUID uuid) {
        super("cracked", username, uuid);
    }

    @Override
    public void login() {
        SessionUtil.setSession(new Session(
                this.getUsername(),
                this.getUuid(),
                "FabricMC",
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.LEGACY
        ));
        Vandalism.getInstance().getLogger().info("Logged in as " + this.getUsername());
    }

}
